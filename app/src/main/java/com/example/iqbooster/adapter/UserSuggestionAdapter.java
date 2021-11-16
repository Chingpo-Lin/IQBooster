package com.example.iqbooster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.iqbooster.R;
import com.example.iqbooster.UserProfilePage;
import com.example.iqbooster.model.AdapterUser;
import com.example.iqbooster.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSuggestionAdapter extends RecyclerView.Adapter<UserSuggestionAdapter.ViewHolder> {
    FirebaseAuth mAuth;
    Context mContext;
    private ArrayList<AdapterUser> mValue;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CircleImageView mCircleImageView;
        public TextView mNameTextView;
        public TextView mUsernameTextView;
        public MaterialButton mFollowBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mCircleImageView = itemView.findViewById(R.id.card_user_avatar);
            mNameTextView = itemView.findViewById(R.id.card_user_realName);
            mUsernameTextView = itemView.findViewById(R.id.card_user_username);
            mFollowBtn = itemView.findViewById(R.id.card_user_follow);
        }
    }

    public UserSuggestionAdapter(Context context, ArrayList<AdapterUser> items, FirebaseAuth mAuth) {
        this.mAuth = mAuth;
        this.mContext = context;
        this.mValue = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(mContext.getResources().getString(R.string.db_users))
                .child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid())
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
                profilePageIntent.putExtra(UserProfilePage.EXTRA, mValue.get(holder.getAbsoluteAdapterPosition()).getUid());
                profilePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(profilePageIntent);
            }
        });

        holder.mNameTextView.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getName());
        holder.mUsernameTextView.setText(mValue.get(holder.getAbsoluteAdapterPosition()).getUsername());

        DatabaseReference userRef = databaseReference.child(mContext.getResources().getString(R.string.db_users));

        if (mAuth.getCurrentUser() != null) {
            DatabaseReference currUserFollowingRef = userRef.child(mAuth.getUid()).child(mContext.getResources().getString(R.string.db_following_users));
            DatabaseReference otherFollowerRef = userRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).child(mContext.getResources().getString(R.string.db_followers_users));
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
                            holder.mFollowBtn.setText(mContext.getResources().getString(R.string.following));
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

                    if (mb.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.follow))) {
                        mb.setText(mContext.getResources().getString(R.string.following));
                        currUserFollowingRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).setValue(mValue.get(holder.getAbsoluteAdapterPosition()));
                        otherFollowerRef.child(mAuth.getUid()).setValue(adapteruser[0]);

                    } else if (mb.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.following))) {
                        mb.setText(mContext.getResources().getString(R.string.follow));
                        currUserFollowingRef.child(mValue.get(holder.getAbsoluteAdapterPosition()).getUid()).removeValue();
                        otherFollowerRef.child(mAuth.getUid()).removeValue();
                    }
                }
            });
        } else {
            holder.mFollowBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public void updateList(ArrayList<AdapterUser> newList) {
        this.mValue = newList;
    }

    public void push_back(AdapterUser adapterUser) {
        this.mValue.add(adapterUser);
        notifyItemInserted(this.mValue.size()-1);
    }

    public void remove(String UID) {
        int idx = 0;
        for (AdapterUser user : mValue) {
            if (user.getUid().equalsIgnoreCase(UID)) {
                break;
            }
            ++idx;
        }
        mValue.remove(idx);
        notifyItemRemoved(idx);
    }
}
