package com.example.iqbooster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private TextView mMarquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mMarquee = (TextView) findViewById(R.id.search_activity_marquee);
        mMarquee.setSelected(true);
    }
}