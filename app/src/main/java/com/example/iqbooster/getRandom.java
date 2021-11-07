package com.example.iqbooster;

import java.util.Random;

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
