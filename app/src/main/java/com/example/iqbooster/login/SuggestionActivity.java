package com.example.iqbooster.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.iqbooster.MainActivity;
import com.example.iqbooster.R;
import com.example.iqbooster.adapter.UserSuggestionAdapter;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Tags;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SuggestionActivity extends AppCompatActivity {

    public static final String EXTRA = "SELECTED TAGS";
    final String TAG = "SuggestionActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUsersRef;
    HashSet<String> tagsHashSet;
    ArrayList<AdapterUser> potentialUsers;
    RecyclerView mRecyclerView;
    UserSuggestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference().child(getResources().getString(R.string.db_users));
        tagsHashSet = new HashSet<String>();

        final String selectedTags = getIntent().getStringExtra(EXTRA);
        Log.d(TAG, "selectedTags has " + selectedTags);
        final String[] split = selectedTags.split(",");
        ArrayList<String> splitArrayList = new ArrayList<>();
        Collections.addAll(splitArrayList, split);
        for (String d : splitArrayList) {
            Log.d(TAG, "splitArrayList: " + d);
        }
        tagsHashSet.addAll(splitArrayList);
        for (String d : tagsHashSet) {
            Log.d(TAG, "tagsHashSet: " + d);
        }

        Toolbar toolbar = findViewById(R.id.suggestion_toolbar);
        mRecyclerView = findViewById(R.id.suggestion_recyclerView);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        potentialUsers = new ArrayList<AdapterUser>();
        mAdapter = new UserSuggestionAdapter(SuggestionActivity.this, potentialUsers, mAuth);
        mRecyclerView.setAdapter(mAdapter);

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                potentialUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdapterUser currUser = ds.getValue(AdapterUser.class);
                    Log.d(TAG, "checking user: " + currUser.getUid());
                    if (ds.hasChild(getApplicationContext().getResources().getString(R.string.db_tags))) {
                        Tags currTags = ds.child(getApplicationContext().getResources().getString(R.string.db_tags)).getValue(Tags.class);
                        boolean found = false;
                        for (String t : currTags.allTrue()) {
                            Log.d(TAG, "checking: " + t);
                            if (found) break;
                            if (tagsHashSet.contains(t)) {
                                Log.d(TAG, "adding tags");
                                potentialUsers.add(currUser);
                                Log.d(TAG, "adding to potential users current size: " + potentialUsers.size());
                                found = true;
                            }
                        }
                    }
                }
                mAdapter.updateList(potentialUsers);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        mUsersRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                AdapterUser currUser = snapshot.getValue(AdapterUser.class);
//                if (snapshot.hasChild(getApplicationContext().getResources().getString(R.string.db_tags))) {
//                    Tags currTags = snapshot.child(getApplicationContext().getResources().getString(R.string.db_tags)).getValue(Tags.class);
//                    boolean found = false;
//                    for (String t : currTags.allTrue()) {
//                        Log.d(TAG, "checking: " + t);
//                        if (found) break;
//                        if (tagsHashSet.contains(t)) {
//                            Log.d(TAG, "adding tags");
//                            mAdapter.push_back(currUser);
//                            Log.d(TAG, "adding to potential users current size: " + potentialUsers.size());
//                            found = true;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                AdapterUser currUser = snapshot.getValue(AdapterUser.class);
//                if (snapshot.hasChild(getApplicationContext().getResources().getString(R.string.db_tags))) {
//                    Tags currTags = snapshot.child(getApplicationContext().getResources().getString(R.string.db_tags)).getValue(Tags.class);
//                    boolean found = false;
//                    for (String t : currTags.allTrue()) {
//                        Log.d(TAG, "checking: " + t);
//                        if (found) break;
//                        if (tagsHashSet.contains(t)) {
//                            Log.d(TAG, "adding tags");
//                            mAdapter.changeChild();
//                            Log.d(TAG, "adding to potential users current size: " + potentialUsers.size());
//                            found = true;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suggestion_menu, menu);
        return true;
    }

    /**
     * Override onOptionsItemSelected to define the behavior of the "check" button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.suggestion_done:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

