package com.example.iqbooster.login;

import androidx.annotation.NonNull;
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
        potentialUsers = new ArrayList<AdapterUser>();

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
        toolbar.setTitle("People you may follow");
        setSupportActionBar(toolbar);

        mAdapter = new UserSuggestionAdapter(getApplicationContext(), potentialUsers, mAuth);

        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                Log.d(TAG, "adding tags" );
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

        mRecyclerView = findViewById(R.id.suggestion_recyclerView);
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
        if (item.getItemId() == R.id.suggestion_done) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}