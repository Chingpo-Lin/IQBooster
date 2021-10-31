package com.example.iqbooster.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.iqbooster.R;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ImageButton mVisibleImageBtn;
    private ImageButton mInvisibleImageBtn;
    private TextView mLoginTextView;
    private ProgressBar mProgressBar;
    private MaterialButton mSignupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailEditText = findViewById(R.id.signup_email_editText);
        mPasswordEditText = findViewById(R.id.signup_password_editText);
        mConfirmPasswordEditText = findViewById(R.id.signup_confirm_password_editText);
        mVisibleImageBtn = findViewById(R.id.signup_visible_imageButton);
        mInvisibleImageBtn = findViewById(R.id.signup_invisible_imageButton);
        mLoginTextView = findViewById(R.id.signup_login_textView);
        mProgressBar = findViewById(R.id.signup_progressBar);
        mSignupBtn = findViewById(R.id.signup_signup_button);



    }
}