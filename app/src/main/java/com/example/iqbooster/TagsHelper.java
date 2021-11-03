package com.example.iqbooster;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class TagsHelper {

    public TagsHelper() {
    }

    public List<String> textToString(Context context, List<String> currTags) {
        List<String> temp = new ArrayList<>();
        for (String this_tag : currTags) {
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.technology))) {
                temp.add("technology");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.health))) {
                temp.add("health");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.entertainment))) {
                temp.add("entertainment");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.sport))) {
                temp.add("sport");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.travel))) {
                temp.add("travel");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.food))) {
                temp.add("food");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.psychology))) {
                temp.add("psychology");
            }
            if (this_tag.equalsIgnoreCase(context.getResources().getString(R.string.business))) {
                temp.add("business");
            }
        }
        return temp;
    }
}
