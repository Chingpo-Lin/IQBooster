package com.example.iqbooster;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.iqbooster.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class getRandom {

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
}
