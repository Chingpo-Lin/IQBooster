package com.example.iqbooster.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iqbooster.R;
import com.example.iqbooster.model.AdapterPost;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.example.iqbooster.getRandom;
import com.google.android.material.chip.Chip;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    Context mContext;
    private ArrayList<Post> mValue;
    FirebaseAuth mAuth;
    boolean hideTag = true;

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

    public NewsFeedAdapter(Context context, ArrayList<Post> items, FirebaseAuth mAuth, boolean hideTag) {
        this.mAuth = mAuth;
        this.mValue = items;
        this.mContext = context;
        this.hideTag = hideTag;
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
        Log.d("NewsFeedAdapter: ", mValue.get(holder.getAdapterPosition()).getRandomID());

        FirebaseUser currUser = mAuth.getCurrentUser();
        DatabaseReference currPostRef = FirebaseDatabase.getInstance().getReference().child(mContext.getResources().getString(R.string.db_posts)).child(mValue.get(holder.getAdapterPosition()).getRandomID());
        DatabaseReference tagRef = currPostRef.child(mContext.getResources().getString(R.string.db_tags));

        // TODO: load profile Image
        holder.mTitle.setText(mValue.get(holder.getAdapterPosition()).getTitle());
        currPostRef.child(mContext.getResources().getString(R.string.db_title)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (holder.getAdapterPosition() != -1) {
                    final String newTitle = snapshot.getValue(String.class);
                    if (holder.getAdapterPosition() != -1) {
                        mValue.get(holder.getAdapterPosition()).setTitle(newTitle);
                        holder.mTitle.setText(String.valueOf(mValue.get(holder.getAdapterPosition()).getTitle()));
//                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // TODO: update info
        holder.mInfo.setText(mValue.get(holder.getAdapterPosition()).getAuthor());

        // TODO: load thumbnail Image based on Post Type, set visibility
        holder.mSubtitle.setText(mValue.get(holder.getAdapterPosition()).getSubTitle());
        currPostRef.child(mContext.getResources().getString(R.string.db_subTitle)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (holder.getAdapterPosition() != -1) {
                    final String newSbuTitle = snapshot.getValue(String.class);
                    if (holder.getAdapterPosition() != -1) {
                        mValue.get(holder.getAdapterPosition()).setTitle(newSbuTitle);
                        holder.mTitle.setText(String.valueOf(mValue.get(holder.getAdapterPosition()).getTitle()));
//                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        holder.mLikeCount.setText(String.valueOf(mValue.get(holder.getAdapterPosition()).getLike_counts()));
        currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (holder.getAdapterPosition() != -1) {
                    final long likeCount = snapshot.getValue(Long.class);
                    if (holder.getAdapterPosition() != -1) {
                        mValue.get(holder.getAdapterPosition()).setLike_counts(likeCount);
                        holder.mLikeCount.setText(String.valueOf(mValue.get(holder.getAdapterPosition()).getLike_counts()));
//                        notifyItemChanged(holder.getAdapterPosition());
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
            holder.mLikeBtn.setLiked(mValue.get(holder.getAdapterPosition()).isLiked());
            holder.mCollectBtn.setLiked(mValue.get(holder.getAdapterPosition()).isCollected());

            currUserRef.child(mContext.getResources().getString(R.string.db_like_post_ids)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Post dsPost = ds.getValue(Post.class);
                        if (holder.getAdapterPosition() != -1 && dsPost.getRandomID().equalsIgnoreCase(mValue.get(holder.getAdapterPosition()).getRandomID())) {
                            mValue.get(holder.getAdapterPosition()).setLiked(true);
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
                            currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                            if (holder.getAdapterPosition() != -1) {
                                AdapterPost adapterPost = new AdapterPost(mValue.get(holder.getAdapterPosition()).getRandomID(), mValue.get(holder.getAdapterPosition()).getAuthor());
                                currUserRef.child(mContext.getResources().getString(R.string.db_like_post_ids)).child(mValue.get(holder.getAdapterPosition()).getRandomID()).setValue(adapterPost);
                                mValue.get(holder.getAdapterPosition()).setLiked(true);
//                                    holder.mLikeBtn.setLiked(true);
//                                    notifyItemChanged(holder.getAdapterPosition());
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
                            currPostRef.child(mContext.getResources().getString(R.string.db_like_counts)).setValue(likeCount);
                            if (holder.getAdapterPosition() != -1) {
                                currUserRef.child(mContext.getResources().getString(R.string.db_like_post_ids)).child(mValue.get(holder.getAdapterPosition()).getRandomID()).removeValue();
                                mValue.get(holder.getAdapterPosition()).setLiked(false);
//                                    holder.mLikeBtn.setLiked(false);
//                                    notifyItemChanged(holder.getAdapterPosition());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            currUserRef.child(mContext.getResources().getString(R.string.db_collect_post_ids)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Post dsPost = ds.getValue(Post.class);
                        if (dsPost.getRandomID().equalsIgnoreCase(mValue.get(holder.getAdapterPosition()).getRandomID())) {
                            mValue.get(holder.getAdapterPosition()).setCollected(true);
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
                    if (holder.getAdapterPosition() != -1) {
                        AdapterPost adapterPost = new AdapterPost(mValue.get(holder.getAdapterPosition()).getRandomID(), mValue.get(holder.getAdapterPosition()).getAuthor());
                        currUserRef.child(mContext.getResources().getString(R.string.db_collect_post_ids)).child(mValue.get(holder.getAdapterPosition()).getRandomID()).setValue(adapterPost);
                        mValue.get(holder.getAdapterPosition()).setCollected(true);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    currUserRef.child(mContext.getResources().getString(R.string.db_collect_post_ids)).child(mValue.get(holder.getAdapterPosition()).getRandomID()).removeValue();
                    if (holder.getAdapterPosition() != -1) {
                        mValue.get(holder.getAdapterPosition()).setCollected(false);
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
    }

    public void notifyIndexChanged(Post post, int at) {
        this.mValue.set(at, post);
        notifyItemChanged(at);
    }

    // TODO: remove maybe?
}
