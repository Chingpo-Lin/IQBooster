package com.example.iqbooster.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iqbooster.ActivityInterface;
import com.example.iqbooster.R;
import com.example.iqbooster.fragment.tabs.businessFragment;
import com.example.iqbooster.fragment.tabs.entertainmentFragment;
import com.example.iqbooster.fragment.tabs.followingUsersPostsFragment;
import com.example.iqbooster.fragment.tabs.foodFragment;
import com.example.iqbooster.fragment.tabs.healthFragment;
import com.example.iqbooster.fragment.tabs.homeFragment;
import com.example.iqbooster.fragment.tabs.psychologyFragment;
import com.example.iqbooster.fragment.tabs.sportFragment;
import com.example.iqbooster.fragment.tabs.technologyFragment;
import com.example.iqbooster.fragment.tabs.travelFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeed extends Fragment {

    View v;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private NestedScrollView mNestedScrollView;
    private FloatingActionButton mFloatingBtn;

    private PostCreation mComposeFragment;
    private homeFragment mHomeFragment;
    private followingUsersPostsFragment mFollowingUsersPostsFragment;
    private technologyFragment mTechnologyFragment;
    private businessFragment mBusinessFragment;
    private entertainmentFragment mEntertainmentFragment;
    private foodFragment mFoodFragment;
    private healthFragment mHealthFragment;
    private psychologyFragment mPsychologyFragment;
    private sportFragment mSportFragment;
    private travelFragment mTravelFragment;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private ActivityInterface activityInterface;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewsFeed() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment newsfeed.
     */
    public static NewsFeed newInstance(String param1, String param2) {
        NewsFeed fragment = new NewsFeed();
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
//        setExitTransition(new MaterialElevationScale(false));
//        setReenterTransition(new MaterialElevationScale(true));
    }

    @Override
    public void onStart() {
//        ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
        if (mAuth.getCurrentUser() == null) {
            mFloatingBtn.setVisibility(View.GONE);
        } else {
            mFloatingBtn.setVisibility(View.VISIBLE);
//            mDatabaseRef.child(getResources().getString(R.string.db_users)).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.hasChild(getResources().getString(R.string.db_following_users))) {
////                        ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
//                    } else {
//                        if (mTabLayout.getSelectedTabPosition() == 1) {
//                            selectHomeTab();
//                        }
//                        if (viewPagerAdapter.getPageTitle(1).toString().equalsIgnoreCase(getResources().getString(R.string.following_tab))) {
//                            viewPagerAdapter.notifyChangeInPosition(1);
//                            viewPagerAdapter.removeFragment(0);
//                        }
////                        ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }
        super.onStart();
    }

    private void selectHomeTab() {
        try {
            mTabLayout.getTabAt(0).select();
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mTabLayout = v.findViewById(R.id.newsfeed_tabLayout);
        mViewPager = v.findViewById(R.id.newsfeed_viewpager);
        mFloatingBtn = v.findViewById(R.id.newsfeed_make_post_btn);
        mNestedScrollView = v.findViewById(R.id.newsfeed_nestedScrollView);

        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComposeFragment = new PostCreation();
                mComposeFragment.setActivityInterface(activityInterface);
                FragmentManager fragmentManager = activityInterface.getActivityFragmentManger();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.new_post_enter,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.new_post_exit  // back
                        )
                        .add(R.id.main_container, mComposeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // create instance of all tabs here
        mHomeFragment = new homeFragment();
        mFollowingUsersPostsFragment = new followingUsersPostsFragment();
        mTechnologyFragment = new technologyFragment();
        mBusinessFragment = new businessFragment();
        mEntertainmentFragment = new entertainmentFragment();
        mFoodFragment = new foodFragment();
        mHealthFragment = new healthFragment();
        mPsychologyFragment = new psychologyFragment();
        mSportFragment = new sportFragment();
        mTravelFragment = new travelFragment();

        mHomeFragment.setActivityInterface(activityInterface);
        mFollowingUsersPostsFragment.setActivityInterface(activityInterface);
        mTechnologyFragment.setActivityInterface(activityInterface);
        mBusinessFragment.setActivityInterface(activityInterface);
        mEntertainmentFragment.setActivityInterface(activityInterface);
        mFoodFragment.setActivityInterface(activityInterface);
        mHealthFragment.setActivityInterface(activityInterface);
        mPsychologyFragment.setActivityInterface(activityInterface);
        mSportFragment.setActivityInterface(activityInterface);
        mTravelFragment.setActivityInterface(activityInterface);

        mTabLayout.setupWithViewPager(mViewPager);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);

        viewPagerAdapter.addFragment(mHomeFragment, getResources().getString(R.string.home));
//        viewPagerAdapter.addFragment(mFollowingUsersPostsFragment, getResources().getString(R.string.following_tab));
        viewPagerAdapter.addFragment(mTechnologyFragment, getResources().getString(R.string.hash_technology));
        viewPagerAdapter.addFragment(mBusinessFragment, getResources().getString(R.string.hash_business));
        viewPagerAdapter.addFragment(mEntertainmentFragment, getResources().getString(R.string.hash_entertainment));
        viewPagerAdapter.addFragment(mFoodFragment, getResources().getString(R.string.hash_food));
        viewPagerAdapter.addFragment(mHealthFragment, getResources().getString(R.string.hash_health));
        viewPagerAdapter.addFragment(mPsychologyFragment, getResources().getString(R.string.hash_psychology));
        viewPagerAdapter.addFragment(mSportFragment, getResources().getString(R.string.hash_sport));
        viewPagerAdapter.addFragment(mTravelFragment, getResources().getString(R.string.hash_travel));

        mViewPager.setAdapter(viewPagerAdapter);
//        ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
//        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    mFloatingBtn.setVisibility(View.INVISIBLE);
//                } else {
//                    mFloatingBtn.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        return v;
    }

    public void setActivityInterface(ActivityInterface activityInterface) {
        this.activityInterface = activityInterface;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentsTitle = new ArrayList<>();
        private long baseId = 0;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title, int position) {
            fragments.add(position, fragment);
            fragmentsTitle.add(position, title);
            notifyDataSetChanged();
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