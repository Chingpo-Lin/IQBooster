package com.example.iqbooster;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.iqbooster.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class helperClass {

    /**
     * method getRandomColor returns a random color
     *
     * @return a random color
     */
    private static final Random RANDOM = new Random();

    public static String getRandomColor() {
        switch (RANDOM.nextInt(8)) {
            default:
            case 0:
                return "#03A9F4";
            case 1:
                return "#0ca6f9";
            case 2:
                return "#a369ff";
            case 3:
                return "#0e56ff";
            case 4:
                return "#FF6A00";
            case 5:
                return "#ef0276";
            case 6:
                return "#FF0000";
            case 7:
                return "#11c612";
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatLikeCount(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatLikeCount(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatLikeCount(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
