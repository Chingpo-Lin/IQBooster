package com.example.iqbooster.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iqbooster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private Button mNextBtr;
    private ProgressBar mProgressBar;
    private TextView mLoginTextView;
    private TextView mHaveAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mEditEmail = findViewById(R.id.forget_password_email_editText);
        mProgressBar = findViewById(R.id.forget_password_progressBar);
        mNextBtr = findViewById(R.id.forget_password_button);
        mLoginTextView = findViewById(R.id.forget_password_login_textView);
        mHaveAccount = findViewById(R.id.forget_password_already_have_account_textView);

        mProgressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mNextBtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = mEditEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(user_email)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mLoginTextView.setVisibility(View.INVISIBLE);
                    mNextBtr.setVisibility(View.INVISIBLE);
                    mHaveAccount.setVisibility(View.INVISIBLE);

                    mAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                goToLoginInActivityHelper();
                                Toast.makeText(ForgetActivity.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                            } else {
                                String ErrorMsg = task.getException().getMessage();
                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + ErrorMsg, Snackbar.LENGTH_LONG);
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
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mLoginTextView.setVisibility(View.VISIBLE);
                            mNextBtr.setVisibility(View.VISIBLE);
                            mHaveAccount.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Enter an Email", Snackbar.LENGTH_LONG);
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
        });
    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed() {
        goToLoginInActivityHelper();
    }

    private void goToLoginInActivityHelper() {
        Intent goToLoginInActivity = new Intent(getApplicationContext(), LoginActivity.class);
        goToLoginInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLoginInActivity);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}