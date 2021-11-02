package com.example.iqbooster.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iqbooster.MainActivity;
import com.example.iqbooster.R;
import com.example.iqbooster.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetUpAccountActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPreferName;
    private EditText mLocation;
    private MaterialButton mContinueBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mUsername = findViewById(R.id.setup_user_name_edit);
        mPreferName = findViewById(R.id.setup_prefer_name_edit);
        mLocation = findViewById(R.id.setup_location_edit);
        mContinueBtn = findViewById(R.id.setup_continue_btn);

        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                !TextUtils.isEmpty(user_input_email)
                String userinput_username = mUsername.getText().toString();
                String useriput_prefreame = mPreferName.getText().toString();
//                String userinput_location = mLocation.getText().toString().trim();
                String userinput_location = "Santa Clara, CA";
                if (!TextUtils.isEmpty(userinput_username.trim()) && !TextUtils.isEmpty(useriput_prefreame.trim())
                        && !TextUtils.isEmpty(userinput_location.trim())) {
                    FirebaseUser mUser = mAuth.getCurrentUser();
                    if (mUser != null) {
                        UserProfileChangeRequest addusername = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userinput_username).build();
                        mUser.updateProfile(addusername).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addUserInfotoDatabase(userinput_username, useriput_prefreame, userinput_location);
                                    goToBubblePickerHelper();
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
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        // NO GO BACK
    }

    /**
     * Helper method which use Intent to go to the Login Page
     */
    private void goToMainActivityHelper() {
        Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToMainActivity);
        finish();
    }

    /**
     * Helper method which use Intent to go to the Bubble Picker
     */
    private void goToBubblePickerHelper() {
        Intent goToDiscoverActivity = new Intent(this, DiscoverActivity.class);
        startActivity(goToDiscoverActivity);
        finish();
    }

    private void addUserInfotoDatabase(String input_username, String input_prefer_name, String input_location) {
        String useremail = mAuth.getCurrentUser().getEmail();
        String uid = mAuth.getCurrentUser().getUid();

        User currUser = new User(input_username, input_prefer_name, useremail, uid,
                input_location, "", "", "", "","");
        mDatabaseReference.child(getApplicationContext().getString(R.string.db_users))
                .child(uid)
                .setValue(currUser);
    }
}