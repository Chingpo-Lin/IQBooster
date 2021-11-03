package com.example.iqbooster.login;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.iqbooster.R;
import com.google.android.material.snackbar.Snackbar;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class DiscoverActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverActivity";

    private BubblePicker picker;
    private Button mNextButton;

    Typeface type;

    int count = 0;
    String seletedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        final String[] titles = getResources().getStringArray(R.array.all_tags);
        final TypedArray colors = getResources().obtainTypedArray(R.array.bubblepicker_colors);
        final TypedArray images = getResources().obtainTypedArray(R.array.bubblepicker_images);

        picker = findViewById(R.id.picker);
        picker.setMaxSelectedCount(3);

        mNextButton = findViewById(R.id.bubblepicker_next_button);

        type = ResourcesCompat.getFont(getApplicationContext(), R.font.googlesans);

        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(titles[position]);
                item.setColor(colors.getColor((position * 2) % 8, 0));
                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                item.setTypeface(type);
                item.setTextColor(ContextCompat.getColor(DiscoverActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(DiscoverActivity.this, images.getResourceId(position, 0)));
                return item;
            }
        });

        colors.recycle();
        images.recycle();
        picker.setBubbleSize(100);
        mNextButton.setText(getApplicationContext().getString(R.string.next));

        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {
                count++;
                if (count > 0) {
                    mNextButton.setText(getApplicationContext().getString(R.string.next));
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {
                count--;
                if(count == 0) {
                    mNextButton.setText(getApplicationContext().getString(R.string.next));
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PickerItem> allItems = picker.getSelectedItems();
                if (allItems.isEmpty()) {
                    String error = "Please Select At Least One Bubble";
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + error, Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                } else {
                    for (PickerItem i : allItems) {
                        String temp = i.getTitle().toLowerCase() + ",";
                        seletedCategory = seletedCategory + temp;
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

    @Override
    public void onBackPressed() {
        // NO GO BACK IS ALLOWED!
    }

    @Override
    protected void onResume() {
        super.onResume();
        picker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        picker.onPause();
    }
}
