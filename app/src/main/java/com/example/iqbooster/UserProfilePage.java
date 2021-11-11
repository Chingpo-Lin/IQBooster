package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.iqbooster.fragment.MyPost;
import com.example.iqbooster.fragment.tabs.userPageFollowersFragment;
import com.example.iqbooster.fragment.tabs.userPageFollowingFragment;
import com.example.iqbooster.model.AdapterUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfilePage extends AppCompatActivity implements ActivityInterface{

    public static final String EXTRA = "USER_UID";

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;

    private TextView mDisplayName;
    private TextView mUserName;
    private MaterialButton mFollowBtn;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private MyPost myPost;
    private com.example.iqbooster.fragment.tabs.userPageFollowersFragment userPageFollowersFragment;
    private com.example.iqbooster.fragment.tabs.userPageFollowingFragment userPageFollowingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        mToolbar = findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String intentUID = getIntent().getStringExtra(EXTRA);

        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference().child(getApplicationContext().getResources().getString(R.string.db_users));
        mAuth = FirebaseAuth.getInstance();

        mDisplayName = findViewById(R.id.user_profile_displayName);
        mUserName = findViewById(R.id.user_profile_username);
        mFollowBtn = findViewById(R.id.user_profile_followBtn);


        mTabLayout = findViewById(R.id.user_profile_tabLayout);
        mViewPager = findViewById(R.id.user_profile_viewPager);

        myPost = new MyPost();
        myPost = myPost.newInstance(intentUID);
        myPost.setActivityInterface(this);

        userPageFollowersFragment = new userPageFollowersFragment();
        userPageFollowersFragment = userPageFollowersFragment.newInstance(intentUID);
        userPageFollowingFragment = new userPageFollowingFragment();
        userPageFollowingFragment = userPageFollowingFragment.newInstance(intentUID);

        mTabLayout.setupWithViewPager(mViewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragment(myPost, getResources().getString(R.string.posts_tab));
        viewPagerAdapter.addFragment(userPageFollowersFragment, getResources().getString(R.string.follower_tab));
        viewPagerAdapter.addFragment(userPageFollowingFragment, getResources().getString(R.string.following_tab));

        mViewPager.setAdapter(viewPagerAdapter);

        mUsers.child(intentUID).addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (mAuth.getCurrentUser() != null && !mAuth.getUid().equalsIgnoreCase(intentUID)) {
            mFollowBtn.setVisibility(View.VISIBLE);

            DatabaseReference followingRef = mUsers.child(mAuth.getUid()).child(getResources().getString(R.string.db_following_users));
            DatabaseReference otherFollowerRef = mUsers.child(intentUID).child(getResources().getString(R.string.db_followers_users));

            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        AdapterUser dsUser = ds.getValue(AdapterUser.class);
                        if (dsUser.getUid().equalsIgnoreCase(intentUID)) {
                            mFollowBtn.setText(getResources().getString(R.string.following));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final AdapterUser[] intentUserProfile = new AdapterUser[1];
            mUsers.child(intentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    intentUserProfile[0] = snapshot.getValue(AdapterUser.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final AdapterUser[] myProfile = new AdapterUser[1];
            mUsers.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myProfile[0] = snapshot.getValue(AdapterUser.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialButton mb = (MaterialButton) v;

                    if (mb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.follow))) {
                        mb.setText(getResources().getString(R.string.following));
                        if (intentUserProfile[0] != null) {
                            followingRef.child(intentUID).setValue(intentUserProfile[0]);
                        }
                        if (myProfile[0] != null) {
                            otherFollowerRef.child(mAuth.getUid()).setValue(myProfile[0]);
                        }
                    } else if (mb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.following))) {
                        mb.setText(getResources().getString(R.string.follow));
                        followingRef.child(intentUID).removeValue();
                        otherFollowerRef.child(mAuth.getUid()).removeValue();
                    }
                }
            });
        }
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

    @Override
    public FragmentManager getActivityFragmentManger() {
        return getSupportFragmentManager();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentsTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }
}