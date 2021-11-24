package com.example.iqbooster.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iqbooster.ActivityInterface;
import com.example.iqbooster.R;
import com.example.iqbooster.Screen;
import com.example.iqbooster.adapter.NewsFeedAdapter;
import com.example.iqbooster.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPost extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "MyPost Nav Tab";
    private ActivityInterface activityInterface;

    private View v;
    private RecyclerView mRecyclerView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<Post> potentialPosts;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    private String mParam1;
    private String mParam2;

    public MyPost() {
        // Required empty public constructor
        mParam1 = ARG_PARAM1;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyPost.
     */
    public static MyPost newInstance(String param1) {
        MyPost fragment = new MyPost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tab, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myPostRef;

        mRecyclerView = v.findViewById(R.id.fragment_tab_recyclerView);
        potentialPosts = new ArrayList<Post>();

        if (mParam1.equalsIgnoreCase(ARG_PARAM1)) {
            // enter from nav drawer
            myPostRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_users)).child(mAuth.getUid()).child(getContext().getString(R.string.db_my_posts));
            mAdapter = new NewsFeedAdapter(getContext(), potentialPosts, mAuth, false, Screen.UN_SPECIFY);
        } else {
            // in profile page
            myPostRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_users)).child(mParam1).child(getContext().getString(R.string.db_my_posts));
            mAdapter = new NewsFeedAdapter(getContext(), potentialPosts, mAuth, false, Screen.PROFILE_PAGE);
        }

        DatabaseReference postRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_posts));
        mAdapter.setActivityInterface(activityInterface);
        mRecyclerView.setAdapter(mAdapter);

//        myPostRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                potentialPosts.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    String postID = ds.getValue(String.class);
//                    Log.d(TAG, postID);
//                    postRef.child(postID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            Log.d(TAG, postID);
//                            Post post = task.getResult().getValue(Post.class);
//                            potentialPosts.add(post);
//                            Log.d(TAG, post.getTitle());
//                            mAdapter.notifyItemInserted(potentialPosts.size()-1);
//                        }
//                    });
//                }
//                mAdapter.updateList(potentialPosts);
//                mAdapter.notifyDataSetChanged();
//                mRecyclerView.setAdapter(mAdapter);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG, "sth removed");
//
//            }
//        });

        myPostRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String currPostID = snapshot.getValue(String.class);

                postRef.child(currPostID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Post post = task.getResult().getValue(Post.class);
                            mAdapter.push_back(post);
                        }
                    });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String currPostID = snapshot.getValue(String.class);

                postRef.child(currPostID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Post post = task.getResult().getValue(Post.class);
                        mAdapter.changeChild(post.getRandomID(), post);
                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String currPostID = snapshot.getValue(String.class);

                postRef.child(currPostID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Post post = task.getResult().getValue(Post.class);
                        mAdapter.removeChild(post.getRandomID());
                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRecyclerView.hasFixedSize();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return v;
    }

    public void setActivityInterface(ActivityInterface activityInterface) {
        this.activityInterface = activityInterface;
    }
}