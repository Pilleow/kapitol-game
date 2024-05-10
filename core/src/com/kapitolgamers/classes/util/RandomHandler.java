package com.kapitolgamers.classes.util;

import java.util.Random;

public class RandomHandler {
    public static int getRandomIntBothInclusive(int min, int max) {
        Random random = new Random();
        int v = max - min + 1;
        return (int) (Math.signum(v) * random.nextInt(Math.abs(v)) + min);
    }

    public static int nextInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}
