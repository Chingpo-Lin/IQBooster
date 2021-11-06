package com.example.iqbooster.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.iqbooster.R;
import com.example.iqbooster.model.Post;
import com.example.iqbooster.model.Tags;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostCreation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostCreation extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View v;

    private final String TAG = "PostCreation";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseFirestore mFireStore;
    private DatabaseReference mUsersRef;
    private DatabaseReference mPostsRef;

    private ConstraintLayout mAddImageConstraint;
    private TextView mPostTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private TextView mBodyTextView;
    private ImageButton mCancelBtn;

    private Chip mTechnology, mSport, mTravel, mFood, mPsychology, mHealth, mBusiness, mEntertainment;
    private Tags tags;
    private ChipGroup mChipGroup;
    private int checkCount;

    private final int MAX_CHIPS = 3;
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostCreation() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostCreation.
     */
    // TODO: Rename and change types and number of parameters
    public static PostCreation newInstance(String param1, String param2) {
        PostCreation fragment = new PostCreation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_post_creation, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        mUsersRef = mDatabase.getReference().child(getContext().getResources().getString(R.string.db_users));
        mPostsRef = mDatabase.getReference().child(getContext().getResources().getString(R.string.db_posts));

        tags = new Tags();
        mUsersRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(getContext().getResources().getString(R.string.db_tags))) {
                    tags = snapshot.child(getContext().getResources().getString(R.string.db_tags)).getValue(Tags.class);
                } else {
                    tags = new Tags();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkCount = 0;

        mAddImageConstraint = v.findViewById(R.id.post_creation_add_image);
        mAddImageConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPostTextView = v.findViewById(R.id.post_creation_post_textView);
        mPostTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTitleTextView = v.findViewById(R.id.post_creation_title);
        mSubTitleTextView = v.findViewById(R.id.post_creation_subTitle);
        mBodyTextView = v.findViewById(R.id.post_creation_body);

        mChipGroup = v.findViewById(R.id.post_creation_chipGroup);

        mTechnology = v.findViewById(R.id.post_creation_technology);
        chipOnClickListener(mTechnology);
        mSport = v.findViewById(R.id.post_creation_sport);
        chipOnClickListener(mSport);
        mTravel = v.findViewById(R.id.post_creation_travel);
        chipOnClickListener(mTravel);
        mFood = v.findViewById(R.id.post_creation_food);
        chipOnClickListener(mFood);
        mPsychology = v.findViewById(R.id.post_creation_psychology);
        chipOnClickListener(mPsychology);
        mHealth = v.findViewById(R.id.post_creation_health);
        chipOnClickListener(mHealth);
        mBusiness = v.findViewById(R.id.post_creation_business);
        chipOnClickListener(mBusiness);
        mEntertainment = v.findViewById(R.id.post_creation_entertainment);
        chipOnClickListener(mEntertainment);

        mCancelBtn = v.findViewById(R.id.post_creation_cancel_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        mPostTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBodyTextView.getText().toString().isEmpty() || mTitleTextView.getText().toString().trim().isEmpty() || mSubTitleTextView.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Make Sure All Fields Are Filled", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    return;
                }
                if (mTitleTextView.getText().toString().trim().equals(".") || mTitleTextView.getText().toString().trim().equals("#") || mTitleTextView.getText().toString().trim().equals("$")
                        || mTitleTextView.getText().toString().trim().equals("[") || mTitleTextView.getText().toString().trim().equals("]")) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Invalid Symbol", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    return;
                }
                if (mSubTitleTextView.getText().toString().trim().equals(".") || mSubTitleTextView.getText().toString().trim().equals("#") || mSubTitleTextView.getText().toString().trim().equals("$")
                        || mSubTitleTextView.getText().toString().trim().equals("[") || mSubTitleTextView.getText().toString().trim().equals("]")) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Invalid Symbol", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    return;
                }
                if (checkCount == 0) {
                    //Toast.makeText(getApplicationContext(), "Please Select At Least One Tag.", Toast.LENGTH_LONG).show();
                    Snackbar sn = Snackbar.make(getActivity().findViewById(android.R.id.content),  "Please Select At Least One Tag", Snackbar.LENGTH_LONG);
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
                }
                createPost();

                hideKeyboard();
                // TODO: remove after post can be retrieved
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    private void hideKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void createPost() {
        final String randomKey = UUID.randomUUID().toString();
        final String currUserUID = mAuth.getCurrentUser().getUid();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        mPostsRef.child(randomKey).setValue(new Post(mTitleTextView.getText().toString(),
                mSubTitleTextView.getText().toString(),
                mBodyTextView.getText().toString(), currUserUID,
                sdf3.format(timestamp).toString(), timestamp.getTime())
                );
        mPostsRef.child(randomKey).child(getContext().getResources().getString(R.string.db_tags)).setValue(tags);
        mUsersRef.child(currUserUID).child(getContext().getResources().getString(R.string.db_my_post_id)).child(randomKey).setValue(randomKey);
        mUsersRef.child(currUserUID).child(getContext().getResources().getString(R.string.db_tags)).setValue(tags);
    }

    private void chipOnClickListener(Chip chip) {
        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    List<Integer> ids = mChipGroup.getCheckedChipIds();
                    if (ids.size() > MAX_CHIPS) {
                        Log.d("PostCreation", "reaching maximum # of tags");
                        chip.setChecked(false);

                        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Maximum 3 Tag Selections Reached", Snackbar.LENGTH_SHORT);
                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        snackbar.show();
                        return;
                    }
                }

                String tagName = buttonView.getText().toString().substring(1).toLowerCase();
                Log.d("PostCreation", "checking tag: " + tagName);
                switch (tagName) {
                    default:
                    case "technology":
                        if (isChecked) {
                            tags.setTechnology(true);
                            ++checkCount;
                        } else {
                            tags.setTechnology(false);
                            --checkCount;
                        }
                        break;
                    case "sport":
                        if (isChecked) {
                            tags.setSport(true);
                            ++checkCount;
                        } else {
                            tags.setSport(false);
                            --checkCount;
                        }
                        break;
                    case "food":
                        if (isChecked) {
                            tags.setFood(true);
                            ++checkCount;
                        } else {
                            tags.setFood(false);
                            --checkCount;
                        }
                        break;
                    case "psychology":
                        if (isChecked) {
                            tags.setPsychology(true);
                            ++checkCount;
                        } else {
                            tags.setPsychology(false);
                            --checkCount;
                        }
                        break;
                    case "health":
                        if (isChecked) {
                            tags.setHealth(true);
                            ++checkCount;
                        } else {
                            tags.setHealth(false);
                            --checkCount;
                        }
                        break;
                    case "business":
                        if (isChecked) {
                            tags.setBusiness(true);
                            ++checkCount;
                        } else {
                            tags.setBusiness(false);
                            --checkCount;
                        }
                        break;
                    case "entertainment":
                        if (isChecked) {
                            tags.setEntertainment(true);
                            ++checkCount;
                        } else {
                            tags.setEntertainment(false);
                            --checkCount;
                        }
                        break;
                }
            }
        });
    }

}