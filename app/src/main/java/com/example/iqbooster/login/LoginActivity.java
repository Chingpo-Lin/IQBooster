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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.iqbooster.MainActivity;
import com.example.iqbooster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPassword;
    private MaterialButton mLoginBtn;
    private TextView mForgotPasswordTextView;
    private ImageButton mVisiblePasswordImageBtn;
    private ImageButton mInvisiblePasswordImageBtn;
    private ProgressBar mProgressBar;
    private TextView mSignupTextView;
    private TextView mAsGuestTextView;
    private TextView mNoAccountTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEditEmail = findViewById(R.id.login_edit_email);
        mEditPassword = findViewById(R.id.login_edit_password);
        mLoginBtn = findViewById(R.id.login_login_btn);
        mForgotPasswordTextView = findViewById(R.id.forgot_passoword_textView);
        mVisiblePasswordImageBtn = findViewById(R.id.login_visible_imageButton);
        mInvisiblePasswordImageBtn = findViewById(R.id.login_invisible_imageButton);
        mProgressBar = findViewById(R.id.login_progressBar);
        mSignupTextView = findViewById(R.id.login_signup_textView);
        mAsGuestTextView = findViewById(R.id.guest_textView);
        mNoAccountTextView = findViewById(R.id.noAccount_textView);

        mProgressBar.setVisibility(View.INVISIBLE);

        mInvisiblePasswordImageBtn.setVisibility(View.INVISIBLE);
        mInvisiblePasswordImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mInvisiblePasswordImageBtn.setVisibility(View.INVISIBLE);
                mVisiblePasswordImageBtn.setVisibility(View.VISIBLE);
            }
        });

        mVisiblePasswordImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mVisiblePasswordImageBtn.setVisibility(View.INVISIBLE);
                mInvisiblePasswordImageBtn.setVisibility(View.VISIBLE);
            }
        });

        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        mAsGuestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivityHelper();
            }
        });

        mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mLoginBtn.performClick();
                }
                return true;
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginBtn.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mSignupTextView.setVisibility(View.INVISIBLE);
                mAsGuestTextView.setVisibility(View.INVISIBLE);
                mNoAccountTextView.setVisibility(View.INVISIBLE);

                // get user's email and password
                String user_input_email = mEditEmail.getText().toString().trim();
                String user_input_password = mEditPassword.getText().toString();

                if (!TextUtils.isEmpty(user_input_email) && !TextUtils.isEmpty(user_input_password)) {
                    mAuth.signInWithEmailAndPassword(user_input_email, user_input_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                goToMainActivityHelper();
                            } else {
                                String loginError = task.getException().getMessage();
                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + loginError, Snackbar.LENGTH_LONG);
                                View view = sn.getView();
                                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                                tv.setTextColor(Color.parseColor("#FFD700"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                } else {
                                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                                }
                                sn.show();

                                mProgressBar.setVisibility(View.INVISIBLE);
                                mLoginBtn.setVisibility(View.VISIBLE);
                                mSignupTextView.setVisibility(View.VISIBLE);
                                mAsGuestTextView.setVisibility(View.VISIBLE);
                                mNoAccountTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please enter an Email or Password", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoginBtn.setVisibility(View.VISIBLE);
                    mSignupTextView.setVisibility(View.VISIBLE);
                    mAsGuestTextView.setVisibility(View.VISIBLE);
                    mNoAccountTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Helper method which use Intent to go back to the Main Page (Timeline)
     */
    private void goToMainActivityHelper() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}