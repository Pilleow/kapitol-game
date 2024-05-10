package com.kapitolgamers.classes.util;

import java.util.Random;

public class RandomHandler {
    public static int getRandomIntBothInclusive(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static int nextInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}
