package com.example.iqbooster.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.iqbooster.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //Animation fadeOut = new AlphaAnimation(1, 0);
        //fadeOut.setInterpolator(new AccelerateInterpolator());
        //fadeOut.setStartOffset(500);
        //fadeOut.setDuration(1800);

        //ImageView imageView = findViewById(R.id.open_page);

        //imageView.setAnimation(fadeOut);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}