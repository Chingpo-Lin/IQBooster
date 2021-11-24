package com.example.iqbooster.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.BuildConfig;
import com.example.iqbooster.R;
import com.example.iqbooster.UserProfilePage;
import com.example.iqbooster.helperClass;
import com.example.iqbooster.login.LoginActivity;
import com.example.iqbooster.model.AdapterPost;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Comment;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.example.iqbooster.notification.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetail extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final String TAG = "PostDetail";

    private ImageView mThumbnail;
    private CircleImageView mProfileImage;
    private TextView mHeadingTitle;
    private TextView mInfo;
    private Chip mFirstChip;
    private Chip mSecondChip;
    private Chip mThirdChip;
    private TextView mSubtitleTextView;
    private TextView mBody;

    private RecyclerView mAllCommentsRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mLinearLayout;

    public EditText mCommentEditText;
    private LikeButton mLikeButton;
    private TextView mLikeCountTextView;
    private LikeButton mCollectButton;
    private ImageView mShareBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataReference;

    String postAuthor = "";
    String parentID = "";
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public FirebaseRecyclerOptions<Comment> option;
    public FirebaseRecyclerAdapter<Comment, CommentsViewHolder> adapter;
    Query firebaseCommentsQuery;

    View v;

    private String mParam1;
    private String mParam2;

    public PostDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PostDetail.
     */
    public static PostDetail newInstance(String param1) {
        PostDetail fragment = new PostDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch (Exception e) {

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_post_detail, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDataReference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, mParam1);

        if (!mParam1.equalsIgnoreCase(ARG_PARAM1)) {
            DatabaseReference currPostRef = mDataReference.child(getContext().getResources().getString(R.string.db_posts)).child(mParam1);

            mThumbnail = v.findViewById(R.id.postdetail_imageView);
            mProfileImage = v.findViewById(R.id.post_heading_circleImageView);
            mHeadingTitle = v.findViewById(R.id.post_heading_title);
            mInfo = v.findViewById(R.id.post_heading_info);

            mFirstChip = v.findViewById(R.id.postdetail_chip1);
            mSecondChip = v.findViewById(R.id.postdetail_chip2);
            mThirdChip = v.findViewById(R.id.postdetail_chip3);

            mSubtitleTextView = v.findViewById(R.id.postdetail_article_text);
            mBody = v.findViewById(R.id.postdetail_article_body);

            mAllCommentsRecyclerView = v.findViewById(R.id.postdetail_comments_recyclerView);
            mLinearLayout = v.findViewById(R.id.detail_linear_layout);

            mCommentEditText = v.findViewById(R.id.layout_bottomBar_EditText);
            mLikeButton = v.findViewById(R.id.like_collect_share_likeButton);
            mLikeCountTextView = v.findViewById(R.id.like_collect_share_likeCount);
            mCollectButton = v.findViewById(R.id.like_collect_share_collect);
            mShareBtn = v.findViewById(R.id.like_collect_share_share);

            // TODO: change to not a single value event if post is editable
            currPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Post currPost = snapshot.getValue(Post.class);
                    mHeadingTitle.setText(currPost.getTitle());
                    postAuthor = currPost.getAuthor();
                    DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference()
                            .child(getContext().getResources().getString(R.string.db_users))
                            .child(postAuthor);
                    postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                AdapterUser postUser = snapshot.getValue(AdapterUser.class);
                                String info = postUser.getName() + " \u22C5 " + currPost.getDate();
                                mInfo.setText(info);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Tags currTags = currPost.getTags();
                    ArrayList<String> allTrue = currTags.allTrue();
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

                    mSubtitleTextView.setText(currPost.getSubTitle());
                    mBody.setText(currPost.getBody());

                    mDataReference
                            .child(getContext().getResources().getString(R.string.db_users))
                            .child(currPost.getAuthor())
                            .child(getContext().getResources().getString(R.string.db_profile_image))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String url = snapshot.getValue(String.class);
                                        if (url != null && !url.isEmpty()) {
                                            RequestOptions requestoptions = new RequestOptions();
                                            Glide.with(getContext())
                                                    .load(url)
                                                    .apply(requestoptions.fitCenter())
                                                    .into(mProfileImage);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profilePageIntent = new Intent(getContext(), UserProfilePage.class);
                            profilePageIntent.putExtra(UserProfilePage.EXTRA, currPost.getAuthor());
                            profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(profilePageIntent);
                        }
                    });

                    try {
                        String thumbnailUrl = currPost.getThumbnail_image();
                        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                            RequestOptions requestoptions = new RequestOptions();
                            Glide.with(getContext())
                                    .load(thumbnailUrl)
                                    .apply(requestoptions.fitCenter())
                                    .into(mThumbnail);
                            mThumbnail.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            currPostRef.child(getContext().getResources().getString(R.string.db_like_counts)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long newCount = snapshot.getValue(Long.class);
                    mLikeCountTextView.setText(helperClass.formatLikeCount(newCount));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            firebaseCommentsQuery = currPostRef.child(getContext().getResources().getString(R.string.db_comments)).orderByChild(getContext().getResources().getString(R.string.db_timestamp));
            firebaseCommentRetrieve(firebaseCommentsQuery);
            mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            mAllCommentsRecyclerView.hasFixedSize();
            mAllCommentsRecyclerView.setLayoutManager(mLayoutManager);
            mAllCommentsRecyclerView.setAdapter(adapter);
            adapter.startListening();

            if (mAuth.getCurrentUser() != null) {
                mCollectButton.setVisibility(View.VISIBLE);
                mCommentEditText.setVisibility(View.VISIBLE);

                DatabaseReference currUserRef = mDataReference.child(getContext().getResources().getString(R.string.db_users)).child(mAuth.getCurrentUser().getUid());
                currUserRef.child(getContext().getResources().getString(R.string.db_like_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.getKey().equalsIgnoreCase(mParam1)) {
                                mLikeButton.setLiked(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                currUserRef.child(getContext().getResources().getString(R.string.db_collect_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.getKey().equalsIgnoreCase(mParam1)) {
                                mCollectButton.setLiked(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                mLikeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        currPostRef.child(getContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long likeCount = snapshot.getValue(Long.class) + 1;
                                if (getContext() != null) {
                                    currPostRef.child(getContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                    currUserRef.child(getContext().getResources().getString(R.string.db_like_posts)).child(mParam1).setValue(new AdapterPost(mParam1, postAuthor));
                                    // send notification to intent user
                                    FirebaseUtil.sendSingleNotification(getContext(), postAuthor, getContext().getResources().getString(R.string.msg_tile), getContext().getResources().getString(R.string.msg_body_like, likeCount), TAG);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        currPostRef.child(getContext().getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long likeCount = snapshot.getValue(Long.class) - 1;
                                if (getContext() != null) {
                                    currPostRef.child(getContext().getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                    currUserRef.child(getContext().getResources().getString(R.string.db_like_posts)).child(mParam1).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                mCollectButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        currUserRef.child(getContext().getResources().getString(R.string.db_collect_posts)).child(mParam1).setValue(new AdapterPost(mParam1, postAuthor));
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        currUserRef.child(getContext().getResources().getString(R.string.db_collect_posts)).child(mParam1).removeValue();
                    }
                });

                mCommentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            hideKeyboard();
                            if (mCommentEditText.getHint().charAt(0) == 'R') {
                                Log.d(TAG, "replying to.,,");
                                if (!mCommentEditText.getText().toString().isEmpty()) {
                                    replyComment(mCommentEditText.getText().toString(), parentID);
                                }
                            } else if (!mCommentEditText.getText().toString().isEmpty()) {
                                submitComment(mCommentEditText.getText().toString());
                            }
                        }
                        return true;
                    }
                });

                mBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Comment
                        if (mCommentEditText.getHint().charAt(0) == 'R') {
                            mCommentEditText.setHint("Comment");
                            mCommentEditText.setText("");
                            mCommentEditText.requestFocus();
                            showKeyboard();
                        }
                    }
                });
            } else {
                mLikeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        mLikeButton.setLiked(false);
                        Intent LoginInActivityIntent = new Intent(getContext(), LoginActivity.class);
                        LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(LoginInActivityIntent);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        mLikeButton.setLiked(false);
                        Intent LoginInActivityIntent = new Intent(getContext(), LoginActivity.class);
                        LoginInActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(LoginInActivityIntent);
                    }
                });
            }

            mShareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmap = getBitMapFromView(mLinearLayout);
                    try {
                        File file = new File(getContext().getExternalCacheDir(), File.separator + "office.jpg");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
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

        return v;
    }

    private void submitComment(String commentText) {
        mCommentEditText.setText("");
        DatabaseReference currPostRef = mDataReference.child(getContext().getResources().getString(R.string.db_posts)).child(mParam1);
        final String randomKey = UUID.randomUUID().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Comment comment = new Comment(
                randomKey,
                mAuth.getCurrentUser().getUid(),
                mAuth.getCurrentUser().getDisplayName(),
                sdf3.format(timestamp).toString(),
                timestamp.getTime(),
                commentText);
        currPostRef.child(getContext().getResources().getString(R.string.db_comments)).child(randomKey).setValue(comment);
    }

    private void replyComment(String commentText, String parentID) {
        mCommentEditText.setText("");
        DatabaseReference currPostCommentRef = mDataReference.child(getContext().getResources().getString(R.string.db_posts)).child(mParam1).child(getContext().getResources().getString(R.string.db_comments));
        final String randomKey = UUID.randomUUID().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Comment comment = new Comment(
                randomKey,
                mAuth.getCurrentUser().getUid(),
                mAuth.getCurrentUser().getDisplayName(),
                sdf3.format(timestamp).toString(),
                timestamp.getTime(),
                commentText);
        currPostCommentRef.child(parentID).child(getContext().getResources().getString(R.string.db_comments_reply)).child(randomKey).setValue(comment);
    }

    private void hideKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mCommentEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void firebaseCommentRetrieve(Query query) {
        option = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(option) {

            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comment model) {
                holder.mCircleImage.setImageResource(R.drawable.avatar);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference
                        .child(getContext().getResources().getString(R.string.db_users))
                        .child(model.getAuthorID())
                        .child(getContext().getResources().getString(R.string.db_profile_image))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String url = snapshot.getValue(String.class);
                                    if (url != null && !url.isEmpty()) {
                                        RequestOptions requestoptions = new RequestOptions();
                                        Glide.with(getContext())
                                                .load(url)
                                                .apply(requestoptions.fitCenter())
                                                .into(holder.mCircleImage);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                holder.mUsername.setText(model.getAuthorDisplayName());
                holder.mInfo.setText(model.getDate());
                holder.mCommentBody.setText(model.getCommentBody());
                holder.mDivider.setVisibility(View.VISIBLE);

                holder.mCommentBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parentID = model.getCommentID();
                        mCommentEditText.requestFocus();
                        mCommentEditText.setHint("Reply to ... " + model.getAuthorDisplayName());
                        showKeyboard();
                    }
                });

                FirebaseRecyclerOptions<Comment> replyOption;
                FirebaseRecyclerAdapter<Comment, CommentsViewHolder> replyAdapter;

                DatabaseReference currPostRef = mDataReference.child(getContext().getResources().getString(R.string.db_posts)).child(mParam1);
                Query replyQuery = currPostRef.child(getContext().getResources().getString(R.string.db_comments))
                        .child(model.getCommentID())
                        .child(getContext().getResources().getString(R.string.db_comments_reply))
                        .orderByChild(getContext().getResources().getString(R.string.db_timestamp));

                replyOption = new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(replyQuery, Comment.class)
                        .build();
                replyAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(replyOption) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comment model) {
                        holder.mCircleImage.setImageResource(R.drawable.avatar);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference
                                .child(getContext().getResources().getString(R.string.db_users))
                                .child(model.getAuthorID())
                                .child(getContext().getResources().getString(R.string.db_profile_image))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String url = snapshot.getValue(String.class);
                                            RequestOptions requestoptions = new RequestOptions();
                                            Glide.with(getContext())
                                                    .load(url)
                                                    .apply(requestoptions.fitCenter())
                                                    .into(holder.mCircleImage);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        holder.mUsername.setText(model.getAuthorDisplayName());
                        holder.mInfo.setText(model.getDate());
                        holder.mCommentBody.setText(model.getCommentBody());
                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.layout_comment, viewGroup, false);
                        return new CommentsViewHolder(view);
                    }
                };

                mLayoutManager = new LinearLayoutManager(getContext());
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                holder.mReplyCommentsRecyclerView.hasFixedSize();
                holder.mReplyCommentsRecyclerView.setLayoutManager(mLayoutManager);
                holder.mReplyCommentsRecyclerView.setAdapter(replyAdapter);
                replyAdapter.startListening();
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_comment, viewGroup, false);
                return new CommentsViewHolder(view);
            }
        };
    }


    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CircleImageView mCircleImage;
        public TextView mUsername;
        public TextView mInfo;
        public TextView mCommentBody;
        public View mDivider;

        public RecyclerView mReplyCommentsRecyclerView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCircleImage = itemView.findViewById(R.id.comment_circleImageView);
            mUsername = itemView.findViewById(R.id.comment_displayName);
            mInfo = itemView.findViewById(R.id.comment_timeStamp);
            mCommentBody = itemView.findViewById(R.id.comment_commentDetail);
            mReplyCommentsRecyclerView = itemView.findViewById(R.id.comment_reply_comment_recyclerView);
            mDivider = itemView.findViewById(R.id.comment_commentdivider);
        }
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap getBitMapFromView(View view) {
        Bitmap returnBitMap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitMap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);
        return returnBitMap;
    }
}