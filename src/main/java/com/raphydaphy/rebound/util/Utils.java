package com.raphydaphy.rebound.util;

public class Utils {
    public static float lerp(float previous, float current, float delta) {
        return (1 - delta) * previous + delta * current;
    }
}
