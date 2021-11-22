package com.example.iqbooster.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.iqbooster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class SignupActivity is the "Sign up" page in the App.
 */

public class SignupActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ImageButton mVisibleImageBtn;
    private ImageButton mInvisibleImageBtn;
    private TextView mLoginTextView;
    private TextView mHaveAccountAlready;
    private ProgressBar mProgressBar;
    private MaterialButton mSignupBtn;
    final String transitionToRight = "NEXT";
    // Firebase stuff
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = findViewById(R.id.signup_email_editText);
        mPasswordEditText = findViewById(R.id.signup_password_editText);
        mHaveAccountAlready = findViewById(R.id.already_have_account_textView);
        mConfirmPasswordEditText = findViewById(R.id.signup_confirm_password_editText);
        mVisibleImageBtn = findViewById(R.id.signup_visible_imageButton);
        mInvisibleImageBtn = findViewById(R.id.signup_invisible_imageButton);
        mLoginTextView = findViewById(R.id.signup_login_textView);
        mProgressBar = findViewById(R.id.signup_progressBar);
        mSignupBtn = findViewById(R.id.signup_signup_button);

        mProgressBar.setVisibility(View.INVISIBLE);
        mInvisibleImageBtn.setVisibility(View.INVISIBLE);

        // when the sign up btn is clicked, sign it up
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set some button visibility
                mSignupBtn.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mHaveAccountAlready.setVisibility(View.INVISIBLE);
                mLoginTextView.setVisibility(View.INVISIBLE);

                // get user's email and password
                String user_input_email = mEmailEditText.getText().toString().trim();
                String user_input_password = mPasswordEditText.getText().toString();
                String user_input_confirm_password = mConfirmPasswordEditText.getText().toString();

                if (!TextUtils.isEmpty(user_input_email) && !TextUtils.isEmpty(user_input_password) && !TextUtils.isEmpty(user_input_confirm_password)) {
                    if (user_input_password.equals(user_input_confirm_password)) {
                        mAuth.createUserWithEmailAndPassword(user_input_email, user_input_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    goToSetUpAccountActivityHelper();
                                } else {
                                    String errormsg = task.getException().getMessage();
                                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + errormsg, Snackbar.LENGTH_LONG);
                                    View view = sn.getView();
                                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                                    tv.setTextColor(Color.parseColor("#FFD700"));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    } else {
                                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }
                                    sn.show();
                                    // set some button visibility
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mSignupBtn.setVisibility(View.VISIBLE);
                                    mHaveAccountAlready.setVisibility(View.VISIBLE);
                                    mLoginTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } else {
                        Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Passwords don't match", Snackbar.LENGTH_LONG);
                        View view = sn.getView();
                        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        sn.show();
                        // set some button visibility
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSignupBtn.setVisibility(View.VISIBLE);
                        mHaveAccountAlready.setVisibility(View.VISIBLE);
                        mLoginTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please fill all fields", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                    // set some button visibility
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSignupBtn.setVisibility(View.VISIBLE);
                    mHaveAccountAlready.setVisibility(View.VISIBLE);
                    mLoginTextView.setVisibility(View.VISIBLE);
                }
           }
        });

        // when the login text is clicked, go back to login in
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginInActivityHelper();
            }
        });

        // showing password when the visible button is pressed
        mVisibleImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mConfirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                mVisibleImageBtn.setVisibility(View.INVISIBLE);
                mInvisibleImageBtn.setVisibility(View.VISIBLE);
            }
        });

        // hiding password when the invisible button is pressed
        mInvisibleImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mConfirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                mVisibleImageBtn.setVisibility(View.VISIBLE);
                mInvisibleImageBtn.setVisibility(View.INVISIBLE);
            }
        });

        mConfirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mSignupBtn.performClick();
                }
                return true;
            }
        });

    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    public void finish(String str) {
        super.finish();
        if (str.equals(transitionToRight)) { overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); }
        else { overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish("");
        goToLoginInActivityHelper();
    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            goToLoginInActivityHelper();
        } else {

        }
    }

    /**
     * Helper method which use Intent to go to the Login Page
     */
    private void goToLoginInActivityHelper() {
        Intent goToLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        goToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLoginActivity);
        finish();
    }

    /**
     * Helper method which use Intent to go to the set up account page
     */
    private void goToSetUpAccountActivityHelper() {
        startActivity(new Intent(getApplicationContext(), SetUpAccountActivity.class));

        finish(transitionToRight);
    }
}
