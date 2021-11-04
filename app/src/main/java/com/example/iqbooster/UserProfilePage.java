package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.iqbooster.model.AdapterUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfilePage extends AppCompatActivity {

    public static final String EXTRA = "USER_UID";

    private Toolbar mToolbar;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;

    private TextView mDisplayName;
    private TextView mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        mToolbar = findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(mToolbar);

        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference().child(getApplicationContext().getResources().getString(R.string.db_users));

        mDisplayName = findViewById(R.id.user_profile_displayName);
        mUserName = findViewById(R.id.user_profile_username);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String currUID = getIntent().getStringExtra(EXTRA);

        mUsers.child(currUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdapterUser currUser = snapshot.getValue(AdapterUser.class);
                mDisplayName.setText(currUser.getName());
                mUserName.setText("@"+currUser.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getSupportActionBar().setTitle("");
    }

    /**
     * Override system method onCreateOptionsMenu which Inflates(replaces)
     * the ToolBar to three dot menu
     *
     * @param menu [the "three dot menu" button specifically]
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_page_threedot_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}