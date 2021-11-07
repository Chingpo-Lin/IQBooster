package com.example.iqbooster.fragment.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iqbooster.R;
import com.example.iqbooster.adapter.UserSuggestionAdapter;
import com.example.iqbooster.model.AdapterUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link userPageFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class userPageFollowingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = "userPagePostsFollowingFragment";

    private View v;
    private RecyclerView mRecyclerView;
    private UserSuggestionAdapter mAdapter;
    private ArrayList<AdapterUser> potentialUsers;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    public userPageFollowingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment userPagePostsFollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static userPageFollowingFragment newInstance(String param1) {
        userPageFollowingFragment fragment = new userPageFollowingFragment();
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
        DatabaseReference pageUserRef = mDatabaseRef.child(getContext().getResources().getString(R.string.db_users)).child(mParam1);

        mRecyclerView = v.findViewById(R.id.fragment_tab_recyclerView);
        potentialUsers = new ArrayList<AdapterUser>();
        mAdapter = new UserSuggestionAdapter(getContext(), potentialUsers, mAuth);
        mRecyclerView.setAdapter(mAdapter);

        pageUserRef.child(getContext().getResources().getString(R.string.db_following_users)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                potentialUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdapterUser dsUser = ds.getValue(AdapterUser.class);
                    potentialUsers.add(dsUser);
                }
                mAdapter.updateList(potentialUsers);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return v;
    }
}