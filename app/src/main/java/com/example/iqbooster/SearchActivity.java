package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.adapter.NewsFeedAdapter;
import com.example.iqbooster.adapter.UserSuggestionAdapter;
import com.example.iqbooster.fragment.PostDetail;
import com.example.iqbooster.login.LoginActivity;
import com.example.iqbooster.model.AdapterPost;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private TextView mMarquee;
    private AppCompatAutoCompleteTextView mAutoComplete;

    private MaterialButton mSearchBtn;
    private ChipGroup mTagsChipGroup;

    private static FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mPostsRef;
    private DatabaseReference mUsersRef;

    boolean initialize = false;
    String[] autoCompleteTags;

    public int LIMIT = 10;
    private RecyclerView mUsersRecyclerView;
    private RecyclerView mPostsRecyclerView;

    FirebaseRecyclerAdapter<AdapterUser, UserSuggestionAdapter.ViewHolder> firebaseRecyclerAdapter4Users;
    FirebaseRecyclerAdapter<Post, NewsFeedAdapter.ViewHolder> firebaseRecyclerAdapter4Posts;

    ArrayList<AdapterUser> tagSearchPotentialUsers = new ArrayList<>();
    ArrayList<Post> tagSearchPotentialPosts = new ArrayList<>();
    firebaseTagSearchRecyclerAdapter4Users mTagSearch4UsersAdapter;
    firebaseTagSearchRecyclerAdapter4Posts mTagSearch4PostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mMarquee = (TextView) findViewById(R.id.search_activity_marquee);
        mAutoComplete = (AppCompatAutoCompleteTextView) findViewById(R.id.search_activity_autoCompleteTextView);
        mAutoComplete.setInputType(InputType.TYPE_CLASS_TEXT);
        mUsersRecyclerView = (RecyclerView) findViewById(R.id.search_activity_usersRecyclerView);
        mPostsRecyclerView = (RecyclerView) findViewById(R.id.search_activity_postsRecyclerView);
        mSearchBtn = (MaterialButton) findViewById(R.id.search_activity_searchBtn);
        mTagsChipGroup = (ChipGroup) findViewById(R.id.search_activity_chipGroup);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mPostsRef = mDatabaseRef.child(getResources().getString(R.string.db_posts));
        mUsersRef = mDatabaseRef.child(getResources().getString(R.string.db_users));

        mUsersRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mUsersRecyclerView.setLayoutManager(layoutManager);

        mPostsRecyclerView.hasFixedSize();
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMarquee.setSelected(true);

        autoCompleteTags = getApplicationContext().getResources().getStringArray(R.array.all_tags_with_hash);

        mAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((!mAutoComplete.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) || (!mAutoComplete.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_ACTION_NEXT)) {
                    if (mTagsChipGroup.getChildCount() > 0) {
                        mTagsChipGroup.removeAllViews();
                        return true;
                    }
                    String search = mAutoComplete.getText().toString().trim();
                    firebaseTextSearch(search);
                } else if ((mAutoComplete.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) || (mAutoComplete.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_ACTION_NEXT)) {
                    // adding multiple tags all in once by adding a space in between
                    searchByTag();

                }
                return true;
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAutoComplete.getText().toString().trim().startsWith("#")) {
                    // adding multiple tags all in once by adding a space in between
                    searchByTag();
                } else {
                    if ((mTagsChipGroup.getChildCount() > 0 && !mAutoComplete.getText().toString().trim().isEmpty()) || (mTagsChipGroup.getChildCount() > 0 && mAutoComplete.getText().toString().equals(" "))) {
                        mTagsChipGroup.removeAllViews();
                        return;
                    }
                    String search = mAutoComplete.getText().toString().trim();
                    firebaseTextSearch(search);
                }
            }
        });

        if (!initialize) {
            mSearchBtn.performClick();
            initialize = true;
        }

        mAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().startsWith("#")) {
                    mSearchBtn.performClick();
                }
            }
        });

        mTagSearch4UsersAdapter = new firebaseTagSearchRecyclerAdapter4Users(getApplicationContext(), tagSearchPotentialUsers);
        mTagSearch4PostsAdapter = new firebaseTagSearchRecyclerAdapter4Posts(getApplicationContext(), tagSearchPotentialPosts);
        setUpFirebaseTagSearch();

        final String [] testing = getApplicationContext().getResources().getStringArray(R.array.all_tags_with_hash);
        final ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testing);
        mAutoComplete.setAdapter(autoFillAdapter);
        mAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAutoComplete.setText(null);
                String selected = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "adding chip" + selected);
                addChipToGroup(selected, mTagsChipGroup);
            }
        });
    }

    private void addChipToGroup(String tagsName, final ChipGroup chipGroup) {
        boolean existed = false;
        if (mTagsChipGroup.getChildCount() > 0) {
            for (int i = 0; i < mTagsChipGroup.getChildCount(); i++) {
                if (((Chip) mTagsChipGroup.getChildAt(i)).getText().toString().equals(tagsName)) {
                    existed = true;
//                    mUsersRecyclerView.setAdapter(tagsAdapter);
//                    mPostsRecyclerView.setAdapter(tagsAdapter);
                }
            }
        }

        if (!existed) {
            final Chip chip = new Chip(this);
            chip.setText(tagsName);
            chip.setClickable(true);
            chip.setCloseIconVisible(true);
            chip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
            chipGroup.addView(chip);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                }
            });
        }
    }

    private void searchByTag() {
        String[] t = mAutoComplete.getText().toString().toLowerCase().trim().split(" ");
        mAutoComplete.setText(null);
        for (String a : t) {
            if (Arrays.asList(autoCompleteTags).contains(a)) {
                addChipToGroup(a, mTagsChipGroup);
            } else {
                Snackbar sn = Snackbar.make(findViewById(android.R.id.content), a + " does not exist", Snackbar.LENGTH_LONG);
                View view = sn.getView();
                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                tv.setTextColor(Color.parseColor("#FFD700"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                sn.show();
                continue;
            }
        }
    }

    void firebaseTextSearch(String search) {
        Query firebaseQuery4Users = mUsersRef.orderByChild(getResources().getString(R.string.db_username)).startAt(search).endAt(search + "\uf88f");
        Query firebaseQuery4Posts = mPostsRef.orderByChild(getResources().getString(R.string.db_title)).startAt(search.toLowerCase()).endAt(search.toLowerCase() + "\uf88f");

        FirebaseRecyclerOptions searchOption4Users = new FirebaseRecyclerOptions.Builder<AdapterUser>()
                .setQuery(firebaseQuery4Users, AdapterUser.class).build();
        FirebaseRecyclerOptions searchOption4Posts = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(firebaseQuery4Posts, Post.class).build();

        firebaseRecyclerAdapter4Users = new FirebaseRecyclerAdapter<AdapterUser, UserSuggestionAdapter.ViewHolder>(searchOption4Users) {
            @Override
            protected void onBindViewHolder(@NonNull UserSuggestionAdapter.ViewHolder holder, int position, @NonNull AdapterUser model) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference
                        .child(getResources().getString(R.string.db_users))
                        .child(model.getUid())
                        .child(getResources().getString(R.string.db_profile_image))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String url = snapshot.getValue(String.class);
                            RequestOptions requestoptions = new RequestOptions();
                            Glide.with(getApplicationContext())
                                    .load(url)
                                    .apply(requestoptions.fitCenter())
                                    .into(holder.mCircleImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profilePageIntent = new Intent(getApplicationContext(), UserProfilePage.class);
                        profilePageIntent.putExtra(UserProfilePage.EXTRA, model.getUid());
                        profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(profilePageIntent);
                    }
                });
                holder.mNameTextView.setText(model.getName());
                holder.mUsernameTextView.setText(model.getUsername());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_users));

                if (mAuth.getCurrentUser() != null) {
                    DatabaseReference currUserFollowingRef = userRef.child(mAuth.getUid()).child(getApplicationContext().getResources().getString(R.string.db_following_users));
                    DatabaseReference otherFollowerRef = userRef.child(model.getUid()).child(getApplicationContext().getResources().getString(R.string.db_followers_users));
                    final AdapterUser[] adapteruser = new AdapterUser[1];
                    userRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            adapteruser[0] = snapshot.getValue(AdapterUser.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (model.getUid().equals(mAuth.getUid())) {
                        holder.mFollowBtn.setVisibility(View.INVISIBLE);
                    }
                    currUserFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                AdapterUser dsUser = ds.getValue(AdapterUser.class);
                                if (holder.getAbsoluteAdapterPosition() != -1 && dsUser.getUid().equalsIgnoreCase(model.getUid())) {
                                    model.customCTF(true);
                                    holder.mFollowBtn.setText(getApplicationContext().getResources().getString(R.string.following));
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.mFollowBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MaterialButton mb = (MaterialButton) v;

                            if (mb.getText().toString().equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.follow))) {
                                mb.setText(getApplicationContext().getResources().getString(R.string.following));
                                currUserFollowingRef.child(model.getUid()).setValue(model);
                                otherFollowerRef.child(mAuth.getUid()).setValue(adapteruser[0]);

                            } else if (mb.getText().toString().equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.following))) {
                                mb.setText(getApplicationContext().getResources().getString(R.string.follow));
                                currUserFollowingRef.child(model.getUid()).removeValue();
                                otherFollowerRef.child(mAuth.getUid()).removeValue();
                            }
                        }
                    });
                } else {
                    holder.mFollowBtn.setVisibility(View.INVISIBLE);
                }
            }

            @NonNull
            @Override
            public UserSuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_user_wrapcontent, parent, false);
                return new UserSuggestionAdapter.ViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return Math.min(super.getItemCount(), LIMIT);
            }
        };

        firebaseRecyclerAdapter4Posts = new FirebaseRecyclerAdapter<Post, NewsFeedAdapter.ViewHolder>(searchOption4Posts) {
            @Override
            protected void onBindViewHolder(@NonNull NewsFeedAdapter.ViewHolder holder, int position, @NonNull Post model) {
                FirebaseUser currUser = mAuth.getCurrentUser();
                DatabaseReference currPostRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_posts)).child(model.getRandomID());
                DatabaseReference tagRef = currPostRef.child(getApplicationContext().getResources().getString(R.string.db_tags));

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference
                        .child(getResources().getString(R.string.db_users))
                        .child(model.getAuthor())
                        .child(getResources().getString(R.string.db_profile_image))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String url = snapshot.getValue(String.class);
                                    RequestOptions requestoptions = new RequestOptions();
                                    Glide.with(getApplicationContext())
                                            .load(url)
                                            .apply(requestoptions.fitCenter())
                                            .into(holder.mCircleImageView);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profilePageIntent = new Intent(getApplicationContext(), UserProfilePage.class);
                        profilePageIntent.putExtra(UserProfilePage.EXTRA, model.getAuthor());
                        profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(profilePageIntent);
                    }
                });


                holder.mTitle.setText(model.getTitle());
                // TODO: update title if editable

//                currPostRef.child(getApplicationContext().getResources().getString(R.string.db_title)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (holder.getAbsoluteAdapterPosition() != -1) {
//                            final String newTitle = snapshot.getValue(String.class);
//                            if (holder.getAbsoluteAdapterPosition() != -1) {
//                                model.setTitle(newTitle);
//                                holder.mTitle.setText(String.valueOf(model.getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                // TODO: update info
                DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference()
                        .child(getResources().getString(R.string.db_users))
                        .child(model.getAuthor());
                postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            AdapterUser postUser = snapshot.getValue(AdapterUser.class);
                            String info = postUser.getName()  + " \u22C5 " + model.getDate();
                            holder.mInfo.setText(info);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // TODO: update thumbnail if editable
                holder.mThumbnail.setVisibility(View.GONE);
                try {
                    String thumbnailUrl = model.getThumbnail_image();
                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        RequestOptions requestoptions = new RequestOptions();
                        Glide.with(getApplicationContext())
                                .load(thumbnailUrl)
                                .apply(requestoptions.fitCenter())
                                .into(holder.mThumbnail);
                        holder.mThumbnail.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {

                }

                holder.mSubtitle.setText(model.getSubTitle());
                holder.mSubtitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostDetail postDetail = new PostDetail();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.search_activity_container, postDetail.newInstance(model.getRandomID())).addToBackStack(null).commit();
                    }
                });
                // TODO: update subtitle if editable

//                currPostRef.child(getApplicationContext().getResources().getString(R.string.db_subTitle)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (holder.getAbsoluteAdapterPosition() != -1) {
//                            final String newSbuTitle = snapshot.getValue(String.class);
//                            if (holder.getAbsoluteAdapterPosition() != -1) {
//                                model.setTitle(newSbuTitle);
//                                holder.mSubtitle.setText(String.valueOf(model.getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                holder.mFirstChip.setVisibility(View.GONE);
                holder.mSecondChip.setVisibility(View.GONE);
                holder.mThirdChip.setVisibility(View.GONE);
                final Tags[] currTags = new Tags[1];
                tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currTags[0] = snapshot.getValue(Tags.class);
                        ArrayList<String> allTrue = currTags[0].allTrue();
                        if (allTrue.size() >= 1) {
                            holder.mFirstChip.setText("#" + allTrue.get(0));
                            holder.mFirstChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                            holder.mFirstChip.setVisibility(View.VISIBLE);
                        }
                        if (allTrue.size() >= 2) {
                            holder.mSecondChip.setText("#" + allTrue.get(1));
                            holder.mSecondChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                            holder.mSecondChip.setVisibility(View.VISIBLE);
                        }
                        if (allTrue.size() >= 3) {
                            holder.mThirdChip.setText("#" + allTrue.get(2));
                            holder.mThirdChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                            holder.mThirdChip.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.mLikeCount.setText(String.valueOf(model.getLike_counts()));
                currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (holder.getAbsoluteAdapterPosition() != -1) {
                            final long likeCount = snapshot.getValue(Long.class);
                            if (holder.getAbsoluteAdapterPosition() != -1) {
                                model.setLike_counts(likeCount);
                                holder.mLikeCount.setText(helperClass.formatLikeCount(model.getLike_counts()));
//                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // if user is log in
                if (currUser != null) {
                    DatabaseReference currUserRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_users)).child(currUser.getUid());
                    holder.mCollectBtn.setVisibility(View.VISIBLE);
                    holder.mLikeBtn.setLiked(model.isLiked());
                    holder.mCollectBtn.setLiked(model.isCollected());

                    currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Post dsPost = ds.getValue(Post.class);
                                if (holder.getAbsoluteAdapterPosition() != -1 && dsPost.getRandomID().equalsIgnoreCase(model.getRandomID())) {
                                    model.setLiked(true);
                                    holder.mLikeBtn.setLiked(true);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    holder.mLikeBtn.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long likeCount = snapshot.getValue(Long.class) + 1;
                                    if (getApplicationContext() != null) {
                                        currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                        if (holder.getAbsoluteAdapterPosition() != -1) {
                                            AdapterPost adapterPost = new AdapterPost(model.getRandomID(), model.getAuthor());
                                            currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).child(model.getRandomID()).setValue(adapterPost);
                                            model.setLiked(true);
//                                    holder.mLikeBtn.setLiked(true);
//                                    notifyItemChanged(holder.getAbsoluteAdapterPosition());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long likeCount = snapshot.getValue(Long.class) - 1;
                                    if (getApplicationContext() != null) {
                                        currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                        if (holder.getAbsoluteAdapterPosition() != -1) {
                                            currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).child(model.getRandomID()).removeValue();
                                            model.setLiked(false);
//                                    holder.mLikeBtn.setLiked(false);
//                                    notifyItemChanged(holder.getAbsoluteAdapterPosition());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });

                    currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Post dsPost = ds.getValue(Post.class);
                                if (holder.getAbsoluteAdapterPosition() != -1 && dsPost.getRandomID().equalsIgnoreCase(model.getRandomID())) {
                                    model.setCollected(true);
                                    holder.mCollectBtn.setLiked(true);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.mCollectBtn.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            if (holder.getAbsoluteAdapterPosition() != -1) {
                                AdapterPost adapterPost = new AdapterPost(model.getRandomID(), model.getAuthor());
                                currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).child(model.getRandomID()).setValue(adapterPost);
                                model.setCollected(true);
                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            if (holder.getAbsoluteAdapterPosition() != -1) {
                                currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).child(model.getRandomID()).removeValue();
                                model.setCollected(false);
                            }
                        }
                    });
                } else {
                    holder.mLikeBtn.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            holder.mLikeBtn.setLiked(false);
                            Intent LoginInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(LoginInActivityIntent);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            holder.mLikeBtn.setLiked(false);
                            Intent LoginInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(LoginInActivityIntent);
                        }
                    });
                }

                holder.mShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = getBitMapFromView(holder.cardView);
                        try {
                            File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "office.jpg");
                            FileOutputStream fOut = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            file.setReadable(true, false);
                            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                            intent.putExtra(Intent.EXTRA_TEXT, "Hey, I found this interesting article on IQBooster. Take a look!");
                            intent.setType("image/jpg");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            //mContext.startActivity(Intent.createChooser(intent, "Share the article to"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public NewsFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_textwithimg, parent, false);
                return new NewsFeedAdapter.ViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return Math.min(super.getItemCount(), LIMIT);
            }
        };

        mUsersRecyclerView.setAdapter(firebaseRecyclerAdapter4Users);
        mPostsRecyclerView.setAdapter(firebaseRecyclerAdapter4Posts);
        firebaseRecyclerAdapter4Users.startListening();
        firebaseRecyclerAdapter4Posts.startListening();
    }

    void setUpFirebaseTagSearch() {
        mTagsChipGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                FirebaseTagSearchHelper();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (mTagsChipGroup.getChildCount() == 0) {
                    String search = mAutoComplete.getText().toString().trim();
                    firebaseTextSearch(search);
                } else {
                    FirebaseTagSearchHelper();
                }
            }
        });
    }

    void FirebaseTagSearchHelper() {
        Log.d(TAG, "new child added...");
        String selectedTag = "";
        if (mTagsChipGroup.getChildCount() > 0) {
            for (int i = 0; i < mTagsChipGroup.getChildCount(); i++) {
                String temp = ((Chip) mTagsChipGroup.getChildAt(i)).getText().toString().toLowerCase().substring(1) + ",";
                Log.d(TAG, "new child added... " + temp);
                selectedTag = selectedTag + temp;
            }
            if (!selectedTag.isEmpty()) {
                selectedTag = selectedTag.substring(0, selectedTag.lastIndexOf(","));
            }
            Log.d(TAG, "new child added... " + selectedTag);

            final String[] split = selectedTag.split(",");

            mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tagSearchPotentialUsers.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.hasChild(getResources().getString(R.string.db_tags))) {
                            Tags tag = ds.child(getResources().getString(R.string.db_tags)).getValue(Tags.class);
                            int matchedCount = 0;
                            ArrayList<String> temp = tag.allTrue();
                            for (String i : split) {
                                if (temp.contains(i)) {
                                    Log.d(TAG, "checking if " + ds.getKey() + " contains " + i);
                                    matchedCount++;
                                }
                            }
                            if (matchedCount == mTagsChipGroup.getChildCount()) {
                                AdapterUser dsUser = ds.getValue(AdapterUser.class);
                                tagSearchPotentialUsers.add(dsUser);
                            }
                        }
                    }
                    mTagSearch4UsersAdapter.updateList(tagSearchPotentialUsers);
                    mUsersRecyclerView.setAdapter(mTagSearch4UsersAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tagSearchPotentialPosts.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.hasChild(getResources().getString(R.string.db_tags))) {
                            Tags tag = ds.child(getResources().getString(R.string.db_tags)).getValue(Tags.class);
                            int matchedCount = 0;
                            ArrayList<String> temp = tag.allTrue();
                            for (String i : split) {
                                if (temp.contains(i)) {
                                    Log.d(TAG, "checking if " + ds.getKey() + " contains " + i);
                                    matchedCount++;
                                }
                            }
                            if (matchedCount == mTagsChipGroup.getChildCount()) {
                                Post dsPost = ds.getValue(Post.class);
                                tagSearchPotentialPosts.add(dsPost);
                            }
                        }
                    }
                    mTagSearch4PostsAdapter.updateList(tagSearchPotentialPosts);
                    mPostsRecyclerView.setAdapter(mTagSearch4PostsAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public class firebaseTagSearchRecyclerAdapter4Users extends RecyclerView.Adapter<UserSuggestionAdapter.ViewHolder> {
        Context mContext;
        private ArrayList<AdapterUser> mValue;

        public firebaseTagSearchRecyclerAdapter4Users(Context mContext, ArrayList<AdapterUser> mValue) {
            this.mContext = mContext;
            this.mValue = mValue;
        }

        @Override
        public void onBindViewHolder(@NonNull UserSuggestionAdapter.ViewHolder holder, int position) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference
                    .child(getResources().getString(R.string.db_users))
                    .child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid())
                    .child(getResources().getString(R.string.db_profile_image))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String url = snapshot.getValue(String.class);
                                RequestOptions requestoptions = new RequestOptions();
                                Glide.with(getApplicationContext())
                                        .load(url)
                                        .apply(requestoptions.fitCenter())
                                        .into(holder.mCircleImageView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profilePageIntent = new Intent(getApplicationContext(), UserProfilePage.class);
                    profilePageIntent.putExtra(UserProfilePage.EXTRA, mValue.get(holder.getAbsoluteAdapterPosition()).getUid());
                    profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(profilePageIntent);
                }
            });
            holder.mNameTextView.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getName());
            holder.mUsernameTextView.setText("@" + mValue.get(holder.getAbsoluteAdapterPosition()).getUsername());

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_users));

            if (mAuth.getCurrentUser() != null) {
                DatabaseReference currUserFollowingRef = userRef.child(mAuth.getUid()).child(getApplicationContext().getResources().getString(R.string.db_following_users));
                DatabaseReference otherFollowerRef = userRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).child(getApplicationContext().getResources().getString(R.string.db_followers_users));
                final AdapterUser[] adapteruser = new AdapterUser[1];
                userRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adapteruser[0] = snapshot.getValue(AdapterUser.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (mValue.get(holder.getAbsoluteAdapterPosition()).getUid().equals(mAuth.getUid())) {
                    holder.mFollowBtn.setVisibility(View.INVISIBLE);
                }
                currUserFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            AdapterUser dsUser = ds.getValue(AdapterUser.class);
                            if (holder.getAbsoluteAdapterPosition() != -1 && dsUser.getUid().equalsIgnoreCase(mValue.get(holder.getAbsoluteAdapterPosition()).getUid())) {
                                mValue.get(holder.getAbsoluteAdapterPosition()).customCTF(true);
                                holder.mFollowBtn.setText(getApplicationContext().getResources().getString(R.string.following));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.mFollowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialButton mb = (MaterialButton) v;

                        if (mb.getText().toString().equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.follow))) {
                            mb.setText(getApplicationContext().getResources().getString(R.string.following));
                            currUserFollowingRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).setValue(mValue.get(holder.getAbsoluteAdapterPosition()));
                            otherFollowerRef.child(mAuth.getUid()).setValue(adapteruser[0]);

                        } else if (mb.getText().toString().equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.following))) {
                            mb.setText(getApplicationContext().getResources().getString(R.string.follow));
                            currUserFollowingRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).removeValue();
                            otherFollowerRef.child(mAuth.getUid()).removeValue();
                        }
                    }
                });
            } else {
                holder.mFollowBtn.setVisibility(View.INVISIBLE);
            }
        }

        @NonNull
        @Override
        public UserSuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_user_wrapcontent, parent, false);
            return new UserSuggestionAdapter.ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return Math.min(mValue.size(), LIMIT);
        }

        public void updateList(ArrayList<AdapterUser> users) {
            this.mValue = users;
            notifyDataSetChanged();
        }
    }

    public class firebaseTagSearchRecyclerAdapter4Posts extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
        Context mContext;
        private ArrayList<Post> mValue;

        public firebaseTagSearchRecyclerAdapter4Posts(Context mContext, ArrayList<Post> mValue) {
            this.mContext = mContext;
            this.mValue = mValue;
        }

        @Override
        public void onBindViewHolder(@NonNull NewsFeedAdapter.ViewHolder holder, int position) {
            FirebaseUser currUser = mAuth.getCurrentUser();
            DatabaseReference currPostRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID());
            DatabaseReference tagRef = currPostRef.child(getApplicationContext().getResources().getString(R.string.db_tags));

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference
                    .child(getResources().getString(R.string.db_users))
                    .child(mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor())
                    .child(getResources().getString(R.string.db_profile_image))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String url = snapshot.getValue(String.class);
                                RequestOptions requestoptions = new RequestOptions();
                                Glide.with(getApplicationContext())
                                        .load(url)
                                        .apply(requestoptions.fitCenter())
                                        .into(holder.mCircleImageView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profilePageIntent = new Intent(getApplicationContext(), UserProfilePage.class);
                    profilePageIntent.putExtra(UserProfilePage.EXTRA, mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
                    profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(profilePageIntent);
                }
            });


            holder.mTitle.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getTitle());
            // TODO: update title if editable

//                currPostRef.child(getApplicationContext().getResources().getString(R.string.db_title)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (holder.getAbsoluteAdapterPosition() != -1) {
//                            final String newTitle = snapshot.getValue(String.class);
//                            if (holder.getAbsoluteAdapterPosition() != -1) {
//                                model.setTitle(newTitle);
//                                holder.mTitle.setText(String.valueOf(model.getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            // TODO: update info
            DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference()
                    .child(getResources().getString(R.string.db_users))
                    .child(mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
            postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        AdapterUser postUser = snapshot.getValue(AdapterUser.class);
                        String info = postUser.getName()  + " \u22C5 " + mValue.get(holder.getAbsoluteAdapterPosition()).getDate();
                        holder.mInfo.setText(info);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // TODO: update thumbnail if editable
            holder.mThumbnail.setVisibility(View.GONE);
            try {
                String thumbnailUrl = mValue.get(holder.getAbsoluteAdapterPosition()).getThumbnail_image();
                if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                    RequestOptions requestoptions = new RequestOptions();
                    Glide.with(getApplicationContext())
                            .load(thumbnailUrl)
                            .apply(requestoptions.fitCenter())
                            .into(holder.mThumbnail);
                    holder.mThumbnail.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {

            }

            holder.mSubtitle.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getSubTitle());
            holder.mSubtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostDetail postDetail = new PostDetail();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.search_activity_container, postDetail.newInstance(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID())).addToBackStack(null).commit();
                }
            });
            // TODO: update subtitle if editable

//                currPostRef.child(getApplicationContext().getResources().getString(R.string.db_subTitle)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (holder.getAbsoluteAdapterPosition() != -1) {
//                            final String newSbuTitle = snapshot.getValue(String.class);
//                            if (holder.getAbsoluteAdapterPosition() != -1) {
//                                model.setTitle(newSbuTitle);
//                                holder.mSubtitle.setText(String.valueOf(model.getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            holder.mFirstChip.setVisibility(View.GONE);
            holder.mSecondChip.setVisibility(View.GONE);
            holder.mThirdChip.setVisibility(View.GONE);
            final Tags[] currTags = new Tags[1];
            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currTags[0] = snapshot.getValue(Tags.class);
                    ArrayList<String> allTrue = currTags[0].allTrue();
                    if (allTrue.size() >= 1) {
                        holder.mFirstChip.setText("#" + allTrue.get(0));
                        holder.mFirstChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                        holder.mFirstChip.setVisibility(View.VISIBLE);
                    }
                    if (allTrue.size() >= 2) {
                        holder.mSecondChip.setText("#" + allTrue.get(1));
                        holder.mSecondChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                        holder.mSecondChip.setVisibility(View.VISIBLE);
                    }
                    if (allTrue.size() >= 3) {
                        holder.mThirdChip.setText("#" + allTrue.get(2));
                        holder.mThirdChip.setTextColor(Color.parseColor(helperClass.getRandomColor()));
                        holder.mThirdChip.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            holder.mLikeCount.setText(String.valueOf(mValue.get(holder.getAbsoluteAdapterPosition()).getLike_counts()));
            currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (holder.getAbsoluteAdapterPosition() != -1) {
                        final long likeCount = snapshot.getValue(Long.class);
                        if (holder.getAbsoluteAdapterPosition() != -1) {
                            mValue.get(holder.getAbsoluteAdapterPosition()).setLike_counts(likeCount);
                            holder.mLikeCount.setText(helperClass.formatLikeCount(mValue.get(holder.getAbsoluteAdapterPosition()).getLike_counts()));
//                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // if user is log in
            if (currUser != null) {
                DatabaseReference currUserRef = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.db_users)).child(currUser.getUid());
                holder.mCollectBtn.setVisibility(View.VISIBLE);
                holder.mLikeBtn.setLiked(mValue.get(holder.getAbsoluteAdapterPosition()).isLiked());
                holder.mCollectBtn.setLiked(mValue.get(holder.getAbsoluteAdapterPosition()).isCollected());

                currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Post dsPost = ds.getValue(Post.class);
                            if (holder.getAbsoluteAdapterPosition() != -1 && dsPost.getRandomID().equalsIgnoreCase(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID())) {
                                mValue.get(holder.getAbsoluteAdapterPosition()).setLiked(true);
                                holder.mLikeBtn.setLiked(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.mLikeBtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long likeCount = snapshot.getValue(Long.class) + 1;
                                if (getApplicationContext() != null) {
                                    currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                    if (holder.getAbsoluteAdapterPosition() != -1) {
                                        AdapterPost adapterPost = new AdapterPost(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID(), mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
                                        currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).setValue(adapterPost);
                                        mValue.get(holder.getAbsoluteAdapterPosition()).setLiked(true);
//                                    holder.mLikeBtn.setLiked(true);
//                                    notifyItemChanged(holder.getAbsoluteAdapterPosition());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long likeCount = snapshot.getValue(Long.class) - 1;
                                if (getApplicationContext() != null) {
                                    currPostRef.child(getApplicationContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                    if (holder.getAbsoluteAdapterPosition() != -1) {
                                        currUserRef.child(getApplicationContext().getResources().getString(R.string.db_like_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).removeValue();
                                        mValue.get(holder.getAbsoluteAdapterPosition()).setLiked(false);
//                                    holder.mLikeBtn.setLiked(false);
//                                    notifyItemChanged(holder.getAbsoluteAdapterPosition());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Post dsPost = ds.getValue(Post.class);
                            if (holder.getAbsoluteAdapterPosition() != -1 && dsPost.getRandomID().equalsIgnoreCase(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID())) {
                                mValue.get(holder.getAbsoluteAdapterPosition()).setCollected(true);
                                holder.mCollectBtn.setLiked(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.mCollectBtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        if (holder.getAbsoluteAdapterPosition() != -1) {
                            AdapterPost adapterPost = new AdapterPost(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID(), mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
                            currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).setValue(adapterPost);
                            mValue.get(holder.getAbsoluteAdapterPosition()).setCollected(true);
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        if (holder.getAbsoluteAdapterPosition() != -1) {
                            currUserRef.child(getApplicationContext().getResources().getString(R.string.db_collect_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).removeValue();
                            mValue.get(holder.getAbsoluteAdapterPosition()).setCollected(false);
                        }
                    }
                });
            } else {
                holder.mLikeBtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        holder.mLikeBtn.setLiked(false);
                        Intent LoginInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(LoginInActivityIntent);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        holder.mLikeBtn.setLiked(false);
                        Intent LoginInActivityIntent = new Intent(mContext, LoginActivity.class);
                        LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(LoginInActivityIntent);
                    }
                });
            }

            holder.mShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmap = getBitMapFromView(holder.cardView);
                    try {
                        File file = new File(mContext.getApplicationContext().getExternalCacheDir(), File.separator + "office.jpg");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        Uri photoURI = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        intent.putExtra(Intent.EXTRA_TEXT, "Hey, I found this interesting article on IQBooster. Take a look!");
                        intent.setType("image/jpg");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //mContext.startActivity(Intent.createChooser(intent, "Share the article to"));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @NonNull
        @Override
        public NewsFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_textwithimg, parent, false);
            return new NewsFeedAdapter.ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return Math.min(mValue.size(), LIMIT);
        }

        public void updateList(ArrayList<Post> posts) {
            this.mValue = posts;
            notifyDataSetChanged();
        }
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap getBitMapFromView(View view){
        Bitmap returnBitMap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitMap);
        Drawable bgDrawable = view.getBackground();
        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);
        return returnBitMap;
    }

    @Override
    protected void onStop() {
//        firebaseRecyclerAdapter4Users.stopListening();
//        firebaseRecyclerAdapter4Posts.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
//        firebaseRecyclerAdapter4Users.startListening();
//        firebaseRecyclerAdapter4Posts.startListening();
        super.onResume();
    }
}