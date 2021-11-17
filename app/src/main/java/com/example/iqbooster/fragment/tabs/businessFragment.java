package com.example.iqbooster.fragment.tabs;

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
import com.example.iqbooster.model.Tags;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link businessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class businessFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View v;
    private RecyclerView mRecyclerView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<Post> potentialPosts;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    private final String TAG = "businessFragment";
    private ActivityInterface activityInterface;

    public businessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment businessFragment.
     */
    public static businessFragment newInstance(String param1, String param2) {
        businessFragment fragment = new businessFragment();
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
        DatabaseReference postRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_posts));

        mRecyclerView = v.findViewById(R.id.fragment_tab_recyclerView);
        potentialPosts = new ArrayList<Post>();
        mAdapter = new NewsFeedAdapter(getContext(), potentialPosts, mAuth, true, Screen.UN_SPECIFY);
        mAdapter.setActivityInterface(activityInterface);
        mRecyclerView.setAdapter(mAdapter);

//        postRef.orderByChild(getContext().getResources().getString(R.string.db_timestamp)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                potentialPosts.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    Post currPost = ds.getValue(Post.class);
//                    if ((getContext() != null) && ds.hasChild(getContext().getResources().getString(R.string.db_tags))) {
//                        Tags currPostTags = ds.child(getContext().getResources().getString(R.string.db_tags)).getValue(Tags.class);
//                        if (currPostTags != null && currPostTags.isBusiness()) {
//                            potentialPosts.add(currPost);
//                        }
//                    }
//                }
//                mAdapter.updateList(potentialPosts);
//                mAdapter.notifyDataSetChanged();
//                mRecyclerView.setAdapter(mAdapter);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        postRef.orderByChild(getContext().getResources().getString(R.string.db_timestamp)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post currPost = snapshot.getValue(Post.class);
                if (currPost.getTags().isBusiness()) {
                    mAdapter.push_back(currPost);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post currPost = snapshot.getValue(Post.class);
                if (currPost.getTags().isBusiness()) {
                    mAdapter.changeChild(currPost.getRandomID(), currPost);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Post currPost = snapshot.getValue(Post.class);
                if (currPost.getTags().isBusiness()) {
                    mAdapter.remove(currPost.getRandomID());
                }
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