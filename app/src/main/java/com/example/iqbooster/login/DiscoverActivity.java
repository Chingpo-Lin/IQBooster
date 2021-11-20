package com.example.iqbooster.login;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.PorterDuff;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
    private MaterialCardView mPsych_card;
    private ImageView mPsych_image;
    private ImageView mPsych_select;
    private MaterialCardView mFood_card;
    private ImageView mFood_image;
    private ImageView mFood_select;
    private MaterialCardView mEntertain_card;
    private ImageView mEntertain_image;
    private ImageView mEntertain_select;
    private final String[] mList = {"technology", "sport", "travel", "psychology", "food", "entertainment"};
    private int[] mSelect;
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
        mTech_image = findViewById(R.id.technology_picker_image);
        mSport_image = findViewById(R.id.sport_picker_image);
        mTravel_image = findViewById(R.id.travel_picker_image);
        mPsych_image = findViewById(R.id.psychology_picker_image);
        mFood_image = findViewById(R.id.food_picker_image);
        mEntertain_image = findViewById(R.id.entertainment_picker_image);
        mTech_select = findViewById(R.id.technology_select);
        mSport_select = findViewById(R.id.sport_select);
        mTravel_select = findViewById(R.id.travel_select);
        mPsych_select = findViewById(R.id.psychology_select);
        mFood_select = findViewById(R.id.food_select);
        mEntertain_select = findViewById(R.id.entertainment_select);



        mSelect = new int[6];
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

        mPsych_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(3, mPsych_image, mPsych_select);
            }
        });

        mFood_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(4, mFood_image, mFood_select);
            }
        });

        mEntertain_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTag(5, mEntertain_image, mEntertain_select);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNumber == 0) {
                    String error = "Please Select At Least One Category";
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + error, Snackbar.LENGTH_LONG);
                    sn.show();
                } else {
                    String seletedCategory = "";
                    for (int i = 0; i < mList.length; i++) {
                        if (mSelect[i] == 1) {
                            seletedCategory += mList[i] + ",";
                        }
                    }

                    if (!seletedCategory.isEmpty()) {
                        seletedCategory = seletedCategory.substring(0, seletedCategory.lastIndexOf(","));
                    }
                    Intent goToSuggestionList = new Intent(getApplicationContext(), SuggestionActivity.class);
                    goToSuggestionList.putExtra(SuggestionActivity.EXTRA, seletedCategory);
                    startActivity(goToSuggestionList);
                    finish();
                }
            }
        });

    }

    public void selectTag(int num, ImageView image, ImageView select) {
        if (mNumber <= 3) {
            if (mSelect[num] == 0) {
                if (mNumber == 3) {
                    String error = "Error: You can choose maximum 3 categories";
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                    sn.show();
                } else {
                    select.setVisibility(View.VISIBLE);
                    image.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN);
                    image.setBackgroundResource(R.drawable.select_rounded);
                    mNumber++;
                }
            } else {
                image.setBackgroundResource(0);
                select.setVisibility(View.INVISIBLE);
                image.clearColorFilter();
                mNumber--;
            }
            mSelect[num] = 1 - mSelect[num]; // 0 -> 1, 1 -> 0
        } else {
            String error = "Error: You can choose maximum 3 categories";
            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
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
