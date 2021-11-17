package com.example.iqbooster.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.ActivityInterface;
import com.example.iqbooster.R;
import com.example.iqbooster.Screen;
import com.example.iqbooster.UserProfilePage;
import com.example.iqbooster.fragment.PostDetail;
import com.example.iqbooster.model.AdapterPost;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.example.iqbooster.getRandom;
import com.google.android.material.chip.Chip;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    Context mContext;
    private ArrayList<Post> mValue;
    FirebaseAuth mAuth;
    boolean hideTag;
    Screen location;

    private ActivityInterface activityInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CircleImageView mCircleImageView; // post_heading_circleImageView
        public TextView mTitle; // post_heading_title
        public TextView mInfo; // post_heading_info

        public ImageView mThumbnail;  // card_textwithimg_thumbnail
        public TextView mSubtitle; // card_textwithimg_subtitle
        public Chip mFirstChip; // post_heading_tagChip

        public LikeButton mLikeBtn; // like_collect_share_likeButton
        public TextView mLikeCount; // like_collect_share_likeCount
        public LikeButton mCollectBtn; // like_collect_share_collect
        public ImageView mShare; // like_collect_share_share

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mCircleImageView = itemView.findViewById(R.id.post_heading_circleImageView);
            mTitle = itemView.findViewById(R.id.post_heading_title);
            mInfo = itemView.findViewById(R.id.post_heading_info);

            mThumbnail = itemView.findViewById(R.id.card_textwithimg_thumbnail);
            mSubtitle = itemView.findViewById(R.id.card_textwithimg_subtitle);
            mFirstChip = itemView.findViewById(R.id.post_heading_tagChip);

            mLikeBtn = itemView.findViewById(R.id.like_collect_share_likeButton);
            mLikeCount = itemView.findViewById(R.id.like_collect_share_likeCount);
            mCollectBtn = itemView.findViewById(R.id.like_collect_share_collect);
            mShare = itemView.findViewById(R.id.like_collect_share_share);
        }
    }

    public NewsFeedAdapter(Context context, ArrayList<Post> items, FirebaseAuth mAuth, boolean hideTag, Screen location) {
        this.mAuth = mAuth;
        this.mValue = items;
        this.mContext = context;
        this.hideTag = hideTag;
        this.location = location;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_textwithimg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("NewsFeedAdapter: ", mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID());

        FirebaseUser currUser = mAuth.getCurrentUser();
        DatabaseReference currPostRef = FirebaseDatabase.getInstance().getReference().child(mContext.getResources().getString(R.string.db_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID());
        DatabaseReference tagRef = currPostRef.child(mContext.getResources().getString(R.string.db_tags));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(mContext.getResources().getString(R.string.db_users))
                .child(mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor())
                .child(mContext.getResources().getString(R.string.db_profile_image))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String url = snapshot.getValue(String.class);
                            RequestOptions requestoptions = new RequestOptions();
                            Glide.with(mContext)
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
                Intent profilePageIntent = new Intent(mContext, UserProfilePage.class);
                profilePageIntent.putExtra(UserProfilePage.EXTRA, mValue.get(holder.getBindingAdapterPosition()).getAuthor());
                profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(profilePageIntent);
            }
        });


        holder.mTitle.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getTitle());
        // TODO: update title if editable
//        currPostRef.child(mContext.getResources().getString(R.string.db_title)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (holder.getAbsoluteAdapterPosition() != -1) {
//                    final String newTitle = snapshot.getValue(String.class);
//                    if (holder.getAbsoluteAdapterPosition() != -1) {
//                        mValue.get(holder.getAbsoluteAdapterPosition()).setTitle(newTitle);
//                        holder.mTitle.setText(String.valueOf(mValue.get(holder.getAbsoluteAdapterPosition()).getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // TODO: update info if editable
        DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getResources().getString(R.string.db_users))
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
        try {
            String thumbnailUrl = mValue.get(holder.getAbsoluteAdapterPosition()).getThumbnail_image();
            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                RequestOptions requestoptions = new RequestOptions();
                Glide.with(mContext)
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
                int container_id;
                switch (location) {
                    default:
                        container_id = R.id.main_container;
                        break;
                    case PROFILE_PAGE:
                        container_id = R.id.user_profile_container;
                }
                activityInterface.getActivityFragmentManger()
                        .beginTransaction().add(container_id, postDetail.newInstance(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID())).addToBackStack(null).commit();
            }
        });
        // TODO: update subtitle if editable
//        currPostRef.child(mContext.getResources().getString(R.string.db_subTitle)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (holder.getAbsoluteAdapterPosition() != -1) {
//                    final String newSbuTitle = snapshot.getValue(String.class);
//                    if (holder.getAbsoluteAdapterPosition() != -1) {
//                        mValue.get(holder.getAbsoluteAdapterPosition()).setTitle(newSbuTitle);
//                        holder.mSubtitle.setText(String.valueOf(mValue.get(holder.getAbsoluteAdapterPosition()).getTitle()));
////                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        if (!hideTag) {
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
        } else {
            holder.mFirstChip.setVisibility(View.GONE);
        }

        holder.mLikeCount.setText(String.valueOf(mValue.get(holder.getAbsoluteAdapterPosition()).getLike_counts()));
        currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (holder.getAbsoluteAdapterPosition() != -1) {
                    final long likeCount = snapshot.getValue(Long.class);
                    if (holder.getAbsoluteAdapterPosition() != -1) {
                        mValue.get(holder.getAbsoluteAdapterPosition()).setLike_counts(likeCount);
                        holder.mLikeCount.setText(String.valueOf(mValue.get(holder.getAbsoluteAdapterPosition()).getLike_counts()));
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
            DatabaseReference currUserRef = FirebaseDatabase.getInstance().getReference().child(mContext.getResources().getString(R.string.db_users)).child(currUser.getUid());
            holder.mCollectBtn.setVisibility(View.VISIBLE);
            holder.mLikeBtn.setLiked(mValue.get(holder.getAbsoluteAdapterPosition()).isLiked());
            holder.mCollectBtn.setLiked(mValue.get(holder.getAbsoluteAdapterPosition()).isCollected());

            currUserRef.child(mContext.getResources().getString(R.string.db_like_posts)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long likeCount = snapshot.getValue(Long.class) + 1;
                            if (mContext != null) {
                                currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                if (holder.getAbsoluteAdapterPosition() != -1) {
                                    AdapterPost adapterPost = new AdapterPost(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID(), mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
                                    currUserRef.child(mContext.getResources().getString(R.string.db_like_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).setValue(adapterPost);
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
                    currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long likeCount = snapshot.getValue(Long.class) - 1;
                            if (mContext != null) {
                                currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                                if (holder.getAbsoluteAdapterPosition() != -1) {
                                    currUserRef.child(mContext.getResources().getString(R.string.db_like_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).removeValue();
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

            currUserRef.child(mContext.getResources().getString(R.string.db_collect_posts)).addValueEventListener(new ValueEventListener() {
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
                        currUserRef.child(mContext.getResources().getString(R.string.db_collect_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).setValue(adapterPost);
                        mValue.get(holder.getAbsoluteAdapterPosition()).setCollected(true);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    if (holder.getAbsoluteAdapterPosition() != -1) {
                        currUserRef.child(mContext.getResources().getString(R.string.db_collect_posts)).child(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID()).removeValue();
                        mValue.get(holder.getAbsoluteAdapterPosition()).setCollected(false);

                        if (location == Screen.MY_COLLECT) {
                            int currPosition = holder.getAbsoluteAdapterPosition();

                            String titleName = mValue.get(holder.getAbsoluteAdapterPosition()).getTitle();
                            final AdapterPost deletedItem = new AdapterPost(mValue.get(holder.getAbsoluteAdapterPosition()).getRandomID(),
                                    mValue.get(holder.getAbsoluteAdapterPosition()).getAuthor());
                            final Post deletedPost = mValue.get(holder.getAbsoluteAdapterPosition());

                            mValue.remove(currPosition);
                            notifyItemRemoved(currPosition);

                            Activity activity = (Activity) mContext;
                            Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),  "\"" + titleName + "\" has removed from your Collect", Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    currUserRef.child(mContext.getResources().getString(R.string.db_collect_posts)).child(deletedItem.getRandomID()).setValue(deletedItem);
                                    mValue.add(currPosition, deletedPost);
                                    notifyItemInserted(currPosition);
                                    mValue.get(currPosition).setCollected(true);
                                    holder.mCollectBtn.setLiked(true);
                                }
                            });
                            snackbar.setActionTextColor(Color.parseColor("#FFD700"));
                            snackbar.show();
                        }
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

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public void updateList(ArrayList<Post> posts) {
        this.mValue = posts;
    }

    public void push_back(Post post) {
        this.mValue.add(post);
        notifyItemInserted(mValue.size()-1);
    }

    public void push_front(Post post) {
        this.mValue.add(0, post);
        notifyItemInserted(0);
    }

    public void remove(String RID) {
        int idx = 0;
        for (Post post : mValue) {
            if (post.getRandomID().equalsIgnoreCase(RID)) {
                break;
            }
            ++idx;
        }
        mValue.remove(idx);
        notifyItemRemoved(idx);
    }

    public void changeChild(String RID, Post changedPost) {
        int idx = 0;
        for (Post post : mValue) {
            if (post.getRandomID().equalsIgnoreCase(RID)) {
                break;
            }
            ++idx;
        }
        mValue.set(idx, changedPost);
        notifyItemChanged(idx);
    }

    public void setActivityInterface(ActivityInterface activityInterface) {
        this.activityInterface = activityInterface;
    }
}
