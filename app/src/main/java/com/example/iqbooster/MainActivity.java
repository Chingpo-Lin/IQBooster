package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iqbooster.adapter.UserSuggestionAdapter;
import com.example.iqbooster.fragment.MyCollect;
import com.example.iqbooster.fragment.MyPost;
import com.example.iqbooster.fragment.NewsFeed;
import com.example.iqbooster.login.LoginActivity;
import com.example.iqbooster.model.Tags;
import com.example.iqbooster.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity: ";

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    
    // drawer stuff
    private View mheaderView;
    private ImageView mProfilePic;
    private TextView mUsername;
    private TextView mEmailAddress;
    private MenuItem mNewsFeedItem;
    private MenuItem mPostItem;
    private MenuItem mCollectItem;
    private MenuItem mLogoutItem;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // fragment
    private NewsFeed mNewsFeedFragment;

    private Tags tags = new Tags(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tags.setTechnology(true);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.drawer_view);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open,
                R.string.close
        );

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        ;
        mNavigationView.setNavigationItemSelectedListener(this);

        mheaderView = mNavigationView.getHeaderView(0);
        mProfilePic = (ImageView) mheaderView.findViewById(R.id.drawer_avatar);
        // TODO: add profile on click listener here...
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null) {
                    goToLoginActivityHelper();
                    closeDrawer();
                } else {
                    // GO TO PROFILE PAGE
                    goToProfilePageActivityHelper();
                    closeDrawer();
                }
            }
        });

        mUsername = (TextView) mheaderView.findViewById(R.id.drawer_username);
        mEmailAddress = (TextView) mheaderView.findViewById(R.id.drawer_email);
        mNewsFeedItem = (MenuItem) mNavigationView.getMenu().getItem(0);
        mPostItem = (MenuItem) mNavigationView.getMenu().getItem(1);
        mCollectItem = (MenuItem) (MenuItem) mNavigationView.getMenu().getItem(2);
        mLogoutItem = (MenuItem) mNavigationView.getMenu().getItem(3);

        mNewsFeedItem.setChecked(true);
        getSupportActionBar().setTitle("News Feed");

        mNewsFeedFragment = new NewsFeed();
        setFragment(mNewsFeedFragment);

        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("users");
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d(TAG, ds.getKey());
                    String uid = ds.getValue(User.class).getUid();
                    df.child(uid).child("tags").setValue(tags);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            mPostItem.setVisible(false);
            mCollectItem.setVisible(false);
            mLogoutItem.setVisible(false);
        } else {
            mUsername.setText(user.getDisplayName());
            mEmailAddress.setText(user.getEmail());
            mPostItem.setVisible(true);
            mCollectItem.setVisible(true);
            mLogoutItem.setVisible(true);
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == mNewsFeedItem.getItemId()) {
            if (getSupportActionBar().getTitle() != "News Feed") {
                getSupportActionBar().setTitle("News Feed");
                setFragment(new NewsFeed());
            }
        } else if (id == mPostItem.getItemId()) {
            if (getSupportActionBar().getTitle() != "My Post") {
                getSupportActionBar().setTitle("My Post");
                setFragment(new MyPost());
            }
        } else if (id == mCollectItem.getItemId()) {
            if (getSupportActionBar().getTitle() != "Collect") {
                getSupportActionBar().setTitle("Collect");
                setFragment(new MyCollect());
            }
        } else if (id == mLogoutItem.getItemId()) {
            mAuth.signOut();
            recreate();
        }
        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method which uses Intent to go to the Login Page
     */
    private void goToLoginActivityHelper() {
        Intent loginPageIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginPageIntent);
    }

    /**
     * Helper method which uses Intent to go to the Profile Page
     */
    private void goToProfilePageActivityHelper() {
//        Intent loginPageIntent = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(loginPageIntent);
    }

    /**
     * Replacing R.id.main_container with corresponding fragment from the drawer
     */
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

}