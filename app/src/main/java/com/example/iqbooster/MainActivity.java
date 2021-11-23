package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.fragment.MyCollect;
import com.example.iqbooster.fragment.MyPost;
import com.example.iqbooster.fragment.NewsFeed;
import com.example.iqbooster.fragment.PostDetail;
import com.example.iqbooster.login.LoginActivity;
import com.example.iqbooster.login.MapsActivity;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityInterface, SensorEventListener {

    private static final String TAG = "MainActivity: ";
    private static final String BUILD_VERSION = "Current Build Version: 1.2.231";

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    
    // drawer stuff
    private View mHeaderView;
    private ImageView mProfilePic;
    private TextView mUsername;
    private TextView mEmailAddress;
    private MenuItem mNewsFeedItem;
    private MenuItem mPostItem;
    private MenuItem mCollectItem;
    private MenuItem mLogoutItem;

    // firebase
    private FirebaseAuth mAuth;

    // fragment
    private NewsFeed mNewsFeedFragment;

    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_THRESHOLD = 1300;
    // 两次检测的时间间隔
    private static final int UPDATE_INTERVAL_TIME = 120;
    SensorManager sensorManager;
    private Sensor sensor;
    Vibrator vibrator;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mAuth = FirebaseAuth.getInstance();

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
        mNavigationView.setNavigationItemSelectedListener(this);

        mHeaderView = mNavigationView.getHeaderView(0);
        mProfilePic = (ImageView) mHeaderView.findViewById(R.id.drawer_avatar);

        mUsername = (TextView) mHeaderView.findViewById(R.id.drawer_username);
        mEmailAddress = (TextView) mHeaderView.findViewById(R.id.drawer_email);
        mNewsFeedItem = (MenuItem) mNavigationView.getMenu().getItem(0);
        mPostItem = (MenuItem) mNavigationView.getMenu().getItem(1);
        mCollectItem = (MenuItem) (MenuItem) mNavigationView.getMenu().getItem(2);
        mLogoutItem = (MenuItem) mNavigationView.getMenu().getItem(3);

        initShakeSensor();

        mNewsFeedItem.setChecked(true);
        getSupportActionBar().setTitle("News Feed");

        mNewsFeedFragment = new NewsFeed();
        mNewsFeedFragment.setActivityInterface(this);
        setFragment(mNewsFeedFragment);
    }

    private void initShakeSensor() {
        // making the use of accelerometer and vibrator
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    goToLoginActivityHelper();
                } else {
                    // GO TO PROFILE PAGE
                    String uid = mAuth.getUid();
                    goToProfilePageActivityHelper(uid);
                }
                closeDrawer();
            }
        });

        if (user == null) {
            mUsername.setText(getResources().getString(R.string.drawer_username));
            mEmailAddress.setText(getResources().getString(R.string.drawer_email));
            mProfilePic.setImageResource(R.drawable.avatar);
            mPostItem.setVisible(false);
            mCollectItem.setVisible(false);
            mLogoutItem.setVisible(false);
        } else {
            mUsername.setText(user.getDisplayName());
            FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.db_users)).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String emailAndUserName = user.getEmail();
                    if (snapshot.hasChild(getResources().getString(R.string.db_username))) {
                        String username = snapshot.child(getResources().getString(R.string.db_username)).getValue(String.class);
                        emailAndUserName = "@" + username + " \u22C5 " + emailAndUserName;
                        mEmailAddress.setText(emailAndUserName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mPostItem.setVisible(true);
            mCollectItem.setVisible(true);
            mLogoutItem.setVisible(true);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child(getResources().getString(R.string.db_users)).child(mAuth.getCurrentUser().getUid())
                    .child(getResources().getString(R.string.db_profile_image))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String url = snapshot.getValue(String.class);
                        if (url != null && !url.isEmpty()) {
                            RequestOptions requestoptions = new RequestOptions();
                            Glide.with(getApplicationContext())
                                    .load(url)
                                    .apply(requestoptions.fitCenter())
                                    .into(mProfilePic);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        mUsername.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  BUILD_VERSION, Snackbar.LENGTH_LONG);
                View view = sn.getView();
                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                tv.setTextColor(Color.parseColor("#FFD700"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                sn.show();
                return true;
            }
        });
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;

        // 判断是否达到了检测时间间隔
        if (timeInterval < UPDATE_INTERVAL_TIME)
            return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;

        /* 当传感器数值发生改变时调用的函数*/
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        // 达到速度阀值，发出提示
        if (speed >= SPEED_THRESHOLD) {
//            Toast.makeText(getApplicationContext(),"speed"+speed, Toast.LENGTH_SHORT).show();
            long[] pattern = {300, 500};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrator.vibrate(pattern, -1);
            }

            // 开始效果
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.card_textwithimg, null);

            final Post[] retPost = new Post[1];
            FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.db_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    long randomIdx = ThreadLocalRandom.current().nextLong(count);
                    long currIdx = 0;

                    Log.d(TAG, "count: " + count);
                    Log.d(TAG, "randomIdx: " + randomIdx);

                    for (DataSnapshot dsPost : snapshot.getChildren()) {
                        if (currIdx < randomIdx) {
                            ++currIdx;
                            continue;
                        }
                        Log.d(TAG, "currIdx: " + currIdx);
                        retPost[0] = dsPost.getValue(Post.class);
                        break;
                    }

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Felling Lucky")
                            .setView(dialogView)
                            .setPositiveButton("YAY!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PostDetail postDetail = new PostDetail();
                                    getSupportFragmentManager().beginTransaction().add(R.id.main_container, postDetail.newInstance(retPost[0].getRandomID())).addToBackStack(null).commit();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    // fill details about the post
                    CircleImageView mCircleImageView = dialogView.findViewById(R.id.post_heading_circleImageView);
                    TextView mTitle = dialogView.findViewById(R.id.post_heading_title);
                    TextView mInfo = dialogView.findViewById(R.id.post_heading_info);

                    ImageView mThumbnail = dialogView.findViewById(R.id.card_textwithimg_thumbnail);
                    TextView mSubtitle = dialogView.findViewById(R.id.card_textwithimg_subtitle);
                    Chip mFirstChip = dialogView.findViewById(R.id.post_heading_tagChip);
                    Chip mSecondChip = dialogView.findViewById(R.id.post_heading_tagChip2);
                    Chip mThirdChip = dialogView.findViewById(R.id.post_heading_tagChip3);

                    LikeButton mLikeBtn = dialogView.findViewById(R.id.like_collect_share_likeButton);
                    TextView mLikeCount = dialogView.findViewById(R.id.like_collect_share_likeCount);
                    ImageView mShare = dialogView.findViewById(R.id.like_collect_share_share);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference
                            .child(getResources().getString(R.string.db_users))
                            .child(retPost[0].getAuthor())
                            .child(getResources().getString(R.string.db_profile_image))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String url = snapshot.getValue(String.class);
                                        RequestOptions requestoptions = new RequestOptions();
                                        Glide.with(MainActivity.this)
                                                .load(url)
                                                .apply(requestoptions.fitCenter())
                                                .into(mCircleImageView);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    mTitle.setText(retPost[0].getTitle());

                    DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference()
                            .child(getResources().getString(R.string.db_users))
                            .child(retPost[0].getAuthor());
                    postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                AdapterUser postUser = snapshot.getValue(AdapterUser.class);
                                String info = postUser.getName()  + " \u22C5 " + retPost[0].getDate();
                                mInfo.setText(info);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    try {
                        String thumbnailUrl = retPost[0].getThumbnail_image();
                        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                            RequestOptions requestoptions = new RequestOptions();
                            Glide.with(MainActivity.this)
                                    .load(thumbnailUrl)
                                    .apply(requestoptions.fitCenter())
                                    .into(mThumbnail);
                            mThumbnail.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {

                    }

                    mSubtitle.setText(retPost[0].getSubTitle());
                    final Tags[] currTags = new Tags[1];
                    FirebaseDatabase.getInstance().getReference()
                            .child(getResources().getString(R.string.db_posts))
                            .child(retPost[0].getRandomID())
                            .child(getResources().getString(R.string.db_tags))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currTags[0] = snapshot.getValue(Tags.class);
                            ArrayList<String> allTrue = currTags[0].allTrue();
                            if (allTrue.size() >= 1) {
                                mFirstChip.setText("#" + allTrue.get(0));
                                mFirstChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                                mFirstChip.setVisibility(View.VISIBLE);
                            }
                            if (allTrue.size() >= 2) {
                                mSecondChip.setText("#" + allTrue.get(1));
                                mSecondChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                                mSecondChip.setVisibility(View.VISIBLE);
                            }
                            if (allTrue.size() >= 3) {
                                mThirdChip.setText("#" + allTrue.get(2));
                                mThirdChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                                mThirdChip.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    mLikeCount.setText(String.valueOf(retPost[0].getLike_counts()));
                    FirebaseDatabase.getInstance().getReference()
                            .child(getResources().getString(R.string.db_posts))
                            .child(retPost[0].getRandomID())
                            .child(getResources().getString(R.string.db_like_counts))
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final long likeCount = snapshot.getValue(Long.class);
                            mLikeCount.setText(helperClass.formatLikeCount(likeCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    mLikeBtn.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            mLikeBtn.setLiked(false);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            mLikeBtn.setLiked(false);
                        }
                    });

                    mShare.setVisibility(View.GONE);

                    alertDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Override system method onCreateOptionsMenu which Inflates(replaces)
     * the ToolBar to SearchBar's View
     *
     * @param menu [the "search" button specifically]
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == mNewsFeedItem.getItemId()) {
            if (getSupportActionBar().getTitle() != "News Feed") {
                getSupportActionBar().setTitle("News Feed");
            }
            NewsFeed newsFeed = new NewsFeed();
            newsFeed.setActivityInterface(this);
            setFragment(newsFeed);
        } else if (id == mPostItem.getItemId()) {
            clearFragmentStack();
            if (getSupportActionBar().getTitle() != "My Post") {
                getSupportActionBar().setTitle("My Post");
            }
            MyPost myPost = new MyPost();
            myPost.setActivityInterface(this);
            setFragment(myPost);
        } else if (id == mCollectItem.getItemId()) {
            clearFragmentStack();
            if (getSupportActionBar().getTitle() != "Collect") {
                getSupportActionBar().setTitle("Collect");
            }
            MyCollect myCollect = new MyCollect();
            myCollect.setActivityInterface(this);
            setFragment(myCollect);
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

    private void clearFragmentStack() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_search) {
            Intent SearchPage = new Intent(getApplicationContext(), SearchActivity.class);
            Log.d(TAG, "Going into Search Page");
            startActivity(SearchPage);
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
    private void goToProfilePageActivityHelper(String uid) {
        Intent profilePageIntent = new Intent(getApplicationContext(), UserProfilePage.class);
        profilePageIntent.putExtra(UserProfilePage.EXTRA, uid);
        profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(profilePageIntent);
    }

    /**
     * Replacing R.id.main_container with corresponding fragment from the drawer
     */
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public FragmentManager getActivityFragmentManger() {
        return getSupportFragmentManager();
    }
}