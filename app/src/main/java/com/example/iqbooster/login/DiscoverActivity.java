package com.example.iqbooster.login;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.PorterDuff;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iqbooster.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;



public class DiscoverActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverActivity";

    private Button mNextButton;
    private MaterialCardView mTech_card;
    private ImageView mTech_image;
    private ImageView mTech_select;
    private MaterialCardView mSport_card;
    private ImageView mSport_image;
    private ImageView mSport_select;
    private MaterialCardView mTravel_card;
    private ImageView mTravel_image;
    private ImageView mTravel_select;
    private MaterialCardView mFood_card;
    private ImageView mFood_image;
    private ImageView mFood_select;
    private MaterialCardView mPsych_card;
    private ImageView mPsych_image;
    private ImageView mPsych_select;
    private MaterialCardView mHealth_card;
    private ImageView mHealth_image;
    private ImageView mHealth_select;
    private MaterialCardView mBusiness_card;
    private ImageView mBusiness_image;
    private ImageView mBusiness_select;
    private MaterialCardView mEntertain_card;
    private ImageView mEntertain_image;
    private ImageView mEntertain_select;

    private boolean[] mSelect;
    private int mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        mTech_card = findViewById(R.id.technology_picker);
        mSport_card = findViewById(R.id.sport_picker);
        mTravel_card = findViewById(R.id.travel_picker);
        mPsych_card = findViewById(R.id.psychology_picker);
        mFood_card = findViewById(R.id.food_picker);
        mEntertain_card = findViewById(R.id.entertainment_picker);
        mHealth_card = findViewById(R.id.health_picker);
        mBusiness_card = findViewById(R.id.business_picker);
        mTech_image = findViewById(R.id.technology_picker_image);
        mSport_image = findViewById(R.id.sport_picker_image);
        mTravel_image = findViewById(R.id.travel_picker_image);
        mPsych_image = findViewById(R.id.psychology_picker_image);
        mFood_image = findViewById(R.id.food_picker_image);
        mEntertain_image = findViewById(R.id.entertainment_picker_image);
        mHealth_image = findViewById(R.id.health_picker_image);
        mBusiness_image = findViewById(R.id.business_picker_image);
        mTech_select = findViewById(R.id.technology_select);
        mSport_select = findViewById(R.id.sport_select);
        mTravel_select = findViewById(R.id.travel_select);
        mPsych_select = findViewById(R.id.psychology_select);
        mFood_select = findViewById(R.id.food_select);
        mEntertain_select = findViewById(R.id.entertainment_select);
        mHealth_select = findViewById(R.id.health_select);
        mBusiness_select = findViewById(R.id.business_select);

        mSelect = new boolean[8]; // initial all false
        // tech0 -> sport1 -> travel2 -> food3 -> psych4 -> health5 -> business6 -> entertainment7
        final String[] mList = getResources().getStringArray(R.array.all_tags);
        mNumber = 0;

        mNextButton = findViewById(R.id.picker_next_button);

        mTech_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(0, mTech_image, mTech_select);
            }
        });

        mSport_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(1,mSport_image, mSport_select);
            }
        });

        mTravel_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(2, mTravel_image, mTravel_select);
            }
        });

        mFood_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(3, mFood_image, mFood_select);
            }
        });

        mPsych_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(4, mPsych_image, mPsych_select);
            }
        });

        mHealth_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(5, mHealth_image, mHealth_select);
            }
        });

        mBusiness_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(6, mBusiness_image, mBusiness_select);
            }
        });

        mEntertain_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(7, mEntertain_image, mEntertain_select);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNumber == 0) {
                    String error = "please select at least one tag";
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                    sn.show();
                } else {
                    String selectedTags = "";
                    for (int i = 0; i < mList.length; i++) {
                        if (mSelect[i]) {
                            selectedTags += mList[i] + ",";
                        }
                    }

                    if (!selectedTags.isEmpty()) {
                        selectedTags = selectedTags.substring(0, selectedTags.lastIndexOf(","));
                    }
                    Log.d(TAG, selectedTags);
                    Intent goToSuggestionList = new Intent(getApplicationContext(), SuggestionActivity.class);
                    goToSuggestionList.putExtra(SuggestionActivity.EXTRA, selectedTags);
                    startActivity(goToSuggestionList);
                    finish();
                }
            }
        });

    }

    public void selectTag(int num, ImageView image, ImageView select) {
        if (mNumber <= 3) {
            if (!mSelect[num]) {
                if (mNumber == 3) {
                    String error = "You can only choose maximum of 3 tags";
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                    return;
                } else {
                    select.setVisibility(View.VISIBLE);
                    image.setColorFilter(Color.parseColor("#FF6200EE"), PorterDuff.Mode.LIGHTEN);
                    mNumber++;
                }
            } else {
                select.setVisibility(View.INVISIBLE);
                image.clearColorFilter();
                mNumber--;
            }
            mSelect[num] = !mSelect[num]; // 0 -> 1, 1 -> 0
        } else {
            String error = "You can choose maximum 3 tags";
            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
            View view = sn.getView();
            TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.parseColor("#FFD700"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            sn.show();
        }
    }

    @Override
    public void onBackPressed() {
        // NO GO BACK IS ALLOWED!
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
