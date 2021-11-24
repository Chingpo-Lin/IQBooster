package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.fragment.MyPost;
import com.example.iqbooster.fragment.tabs.userPageFollowersFragment;
import com.example.iqbooster.fragment.tabs.userPageFollowingFragment;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Tags;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.iqbooster.notification.FirebaseUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfilePage extends AppCompatActivity implements ActivityInterface {

    public static final String EXTRA = "USER_UID";

    private MaterialToolbar mToolbar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;

    private CircleImageView mProfileImage;
    private TextView mDisplayName;
    private TextView mUserName;
    private TextView mUserLocation;
    private ChipGroup mChipGroup;
    private MaterialButton mFollowBtn;
    private MaterialButton mFollowBtnToolBar;
    private NestedScrollView mScrollView;

    private String currPageUID;
    private String currPageUserDisplayName;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private MyPost myPost;
    private com.example.iqbooster.fragment.tabs.userPageFollowersFragment userPageFollowersFragment;
    private com.example.iqbooster.fragment.tabs.userPageFollowingFragment userPageFollowingFragment;

    private static final String TAG = "UserProfilePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        mToolbar = findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_ab_back_material, null);
        upArrow.setColorFilter(Color.parseColor("#212121"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Drawable overflowIcon = mToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            Drawable newIcon = overflowIcon.mutate();
            newIcon.setColorFilter(Color.parseColor("#212121"), PorterDuff.Mode.MULTIPLY);
            mToolbar.setOverflowIcon(newIcon);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String intentUID = getIntent().getStringExtra(EXTRA);
        currPageUID = intentUID;

        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference().child(getApplicationContext().getResources().getString(R.string.db_users));
        mAuth = FirebaseAuth.getInstance();

        mProfileImage = findViewById(R.id.user_profile_circleImageView);
        mDisplayName = findViewById(R.id.user_profile_displayName);
        mUserName = findViewById(R.id.user_profile_username);
        mUserLocation = findViewById(R.id.user_profile_location);
        mChipGroup = findViewById(R.id.user_profile_chipGroup);
        mFollowBtn = findViewById(R.id.user_profile_followBtn);
        mFollowBtnToolBar = findViewById(R.id.user_profile_toolbar_followBtn);
        mScrollView = findViewById(R.id.user_profile_scrollView);

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
        mTabLayout.setNestedScrollingEnabled(false);

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
                currPageUserDisplayName = mDisplayName.getText().toString();
                mUserName.setText("@" + currUser.getUsername());
                try {
                    mUserLocation.setText(currUser.getLocation());
                    DataSnapshot profileSnapshot = snapshot.child(getResources().getString(R.string.db_profile_image));
                    if (profileSnapshot.exists()) {
                        String url = profileSnapshot.getValue(String.class);
                        RequestOptions requestoptions = new RequestOptions();
                        Glide.with(getApplicationContext())
                                .load(url)
                                .apply(requestoptions.fitCenter())
                                .into(mProfileImage);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUsers.child(intentUID).child(getResources().getString(R.string.db_tags)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Tags tags = snapshot.getValue(Tags.class);
                    addChipToGroup(tags);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getSupportActionBar().setTitle("");
        final boolean[] hasTitle = {false};
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.d(TAG, "y:" + mScrollView.getScrollY());
                if (!hasTitle[0] && mScrollView.getScrollY() >= 400) {
                    getSupportActionBar().setTitle(currPageUserDisplayName);
                    if (mAuth.getCurrentUser() != null && !mAuth.getUid().equalsIgnoreCase(intentUID)) {
                        mFollowBtnToolBar.setVisibility(View.VISIBLE);
                    }
                    hasTitle[0] = true;
                } else if (mScrollView.getScrollY() < 400 && hasTitle[0]) {
                    getSupportActionBar().setTitle("");
                    if (mAuth.getCurrentUser() != null && !mAuth.getUid().equalsIgnoreCase(intentUID)) {
                        mFollowBtnToolBar.setVisibility(View.INVISIBLE);
                    }
                    hasTitle[0] = false;
                }
            }
        });

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
//                            mFollowBtnToolBar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_done_white_24, 0,0,0);
                            mFollowBtnToolBar.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_done_white_24));
                            mFollowBtnToolBar.setContentDescription(getResources().getString(R.string.following));
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
                        mFollowBtnToolBar.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_done_white_24));
                        mFollowBtnToolBar.setContentDescription(getResources().getString(R.string.following));
                        if (intentUserProfile[0] != null) {
                            followingRef.child(intentUID).setValue(intentUserProfile[0]);
                        }
                        if (myProfile[0] != null) {
                            otherFollowerRef.child(mAuth.getUid()).setValue(myProfile[0]);
                        }
                        // send notification to intent user
                        FirebaseUtil.sendSingleNotification(getApplicationContext(), intentUID, getResources().getString(R.string.msg_tile),
                                getResources().getString(R.string.msg_body_follow, myProfile[0].getName()), TAG);
                    } else if (mb.getText().toString().equalsIgnoreCase(getResources().getString(R.string.following))) {
                        mFollowBtnToolBar.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_add_24));

                        mFollowBtnToolBar.setContentDescription(getResources().getString(R.string.follow));
                        mb.setText(getResources().getString(R.string.follow));
                        followingRef.child(intentUID).removeValue();
                        otherFollowerRef.child(mAuth.getUid()).removeValue();
                    }
                }
            });

            mFollowBtnToolBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialButton mb = (MaterialButton) v;
                    if (mb.getContentDescription() == getResources().getString(R.string.follow)) {
                        mFollowBtn.setText(getResources().getString(R.string.following));
                        mb.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_done_white_24));
                        mb.setContentDescription(getResources().getString(R.string.following));
                        if (intentUserProfile[0] != null) {
                            followingRef.child(intentUID).setValue(intentUserProfile[0]);
                        }
                        if (myProfile[0] != null) {
                            otherFollowerRef.child(mAuth.getUid()).setValue(myProfile[0]);
                        }
                    } else if (mb.getContentDescription() == getResources().getString(R.string.following)) {
                        mFollowBtn.setText(getResources().getString(R.string.follow));
                        mb.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_add_24));
                        mb.setContentDescription(getResources().getString(R.string.follow));
                        followingRef.child(intentUID).removeValue();
                        otherFollowerRef.child(mAuth.getUid()).removeValue();
                    }
                }
            });
        }
    }

    private void addChipToGroup(Tags tags) {
        mChipGroup.removeAllViews();
        for (String tag : tags.allTrue()) {
            final Chip chip = new Chip(this);
            chip.setText("#" + tag);
            chip.setClickable(false);
            chip.setCloseIconVisible(false);
            chip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
            mChipGroup.addView(chip);
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
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getUid().equalsIgnoreCase(currPageUID)) {
                getMenuInflater().inflate(R.menu.user_profile_page_threedot_menu, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.threedots_logout:
                mAuth.signOut();
                recreate();
            default:
                return super.onOptionsItemSelected(item);
        }
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