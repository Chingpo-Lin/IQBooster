package com.example.iqbooster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private TextView mMarquee;
    private AppCompatAutoCompleteTextView mAutoComplete;
    private RecyclerView mUsersRecyclerView;
    private RecyclerView mPostsRecyclerView;
    private MaterialButton mSearchBtn;
    private ChipGroup mTagsChipGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mPostsRef;
    private DatabaseReference mUsersRef;

    private ArrayList<String> potentialUsers = new ArrayList<>();
    private ArrayList<String> potentialPosts = new ArrayList<>();

    boolean initialize = false;
    String[] autoCompleteTags;

    public int LIMIT = 10;
    FirebaseRecyclerAdapter<AdapterUser, UserSuggestionAdapter.ViewHolder> firebaseRecyclerAdapter4Users;
    FirebaseRecyclerAdapter<Post, NewsFeedAdapter.ViewHolder> firebaseRecyclerAdapter4Posts;

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
        for (String x : autoCompleteTags) {
            Log.d("xxxxxxx", x);
        }
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
            chip.setTextColor(Color.parseColor(getRandom.getRandomColor()));
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
                holder.mInfo.setText(model.getAuthor());

                // TODO: update thumbnail if editable
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


                final Tags[] currTags = new Tags[1];
                tagRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currTags[0] = snapshot.getValue(Tags.class);
                        ArrayList<String> allTrue = currTags[0].allTrue();
                        if (!allTrue.isEmpty()) {
                            holder.mFirstChip.setText("#" + allTrue.get(0));
                            holder.mFirstChip.setTextColor(Color.parseColor(getRandom.getRandomColor()));
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
                                holder.mLikeCount.setText(String.valueOf(model.getLike_counts()));
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
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            holder.mLikeBtn.setLiked(false);
                        }
                    });
                }

                // TODO: implement Share Btn, last edit
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