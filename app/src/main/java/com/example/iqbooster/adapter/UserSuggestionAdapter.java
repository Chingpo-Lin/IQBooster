package com.example.iqbooster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iqbooster.R;
import com.example.iqbooster.model.AdapterUser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
//        holder.mCircleImageView.setImageResource();
        holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 intent extra: mValue.get(position).getuid();
            }
        });
        // TODO: enable follow + to unfollow icon changes, load user profile image
        holder.mNameTextView.setText(mValue.get(holder.getAdapterPosition()).getName());
        holder.mUsernameTextView.setText(mValue.get(holder.getAdapterPosition()).getUsername());
        holder.mFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton mb = (MaterialButton)v;
                DatabaseReference currUserFollowingRef = FirebaseDatabase.getInstance().getReference().child(mContext.getResources().getString(R.string.db_users)).child(mAuth.getUid()).child(mContext.getResources().getString(R.string.db_following_users));

                if (mb.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.follow))) {
                    mb.setText(mContext.getResources().getString(R.string.following));
                    currUserFollowingRef.child(mValue.get(holder.getAdapterPosition()).getUid()).setValue(mValue.get(holder.getAdapterPosition()));
                } else if (mb.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.following))) {
                    mb.setText(mContext.getResources().getString(R.string.follow));
                    currUserFollowingRef.child(mValue.get(holder.getAdapterPosition()).getUid()).removeValue();
                }
            }
        });
//
//        RequestOptions requestoptions = new RequestOptions();
//        Glide.with(viewHolder.mCircleImage.getContext())
//                .load(getRandom.getRandomUCSDDrawable())
//                .apply(requestoptions.fitCenter())
//                .into(viewHolder.mCircleImage);
    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public void updateList(ArrayList<AdapterUser> newList) {
        this.mValue = newList;
    }
}
