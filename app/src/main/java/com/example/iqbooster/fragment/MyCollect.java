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
import com.example.iqbooster.model.AdapterPost;
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
 * Use the {@link MyCollect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCollect extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "MyCollect Nav Tab";

    private View v;
    private RecyclerView mRecyclerView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<Post> potentialPosts;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    private ActivityInterface activityInterface;

    public MyCollect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCollect.
     */
    public static MyCollect newInstance(String param1, String param2) {
        MyCollect fragment = new MyCollect();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tab, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myCollectRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_users)).child(mAuth.getUid()).child(getContext().getString(R.string.db_collect_posts));
        DatabaseReference postRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_posts));

        mRecyclerView = v.findViewById(R.id.fragment_tab_recyclerView);
        potentialPosts = new ArrayList<Post>();
        mAdapter = new NewsFeedAdapter(getContext(), potentialPosts, mAuth, false, Screen.MY_COLLECT);
        mAdapter.setActivityInterface(activityInterface);
        mRecyclerView.setAdapter(mAdapter);

//        myCollectRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                potentialPosts.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    AdapterPost post = ds.getValue(AdapterPost.class);
//                    Log.d(TAG, post.getRandomID());
//                    postRef.child(post.getRandomID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            Log.d(TAG, post.getRandomID());
//                            Post post = task.getResult().getValue(Post.class);
////                            potentialPosts.add(post);
//                            Log.d(TAG, post.getTitle());
////                            mAdapter.notifyItemInserted(potentialPosts.size()-1);
//                            mAdapter.push_back(post);
//                        }
//                    });
//                }
////                mAdapter.updateList(potentialPosts);
//                mRecyclerView.setAdapter(mAdapter);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        myCollectRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AdapterPost post = snapshot.getValue(AdapterPost.class);

                postRef.child(post.getRandomID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Post post = task.getResult().getValue(Post.class);
                        mAdapter.push_back(post);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AdapterPost post = snapshot.getValue(AdapterPost.class);

                postRef.child(post.getRandomID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Post post = task.getResult().getValue(Post.class);
                        mAdapter.changeChild(post.getRandomID(), post);
                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                AdapterPost post = snapshot.getValue(AdapterPost.class);

                postRef.child(post.getRandomID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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