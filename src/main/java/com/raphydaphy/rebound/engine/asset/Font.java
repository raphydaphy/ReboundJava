package com.raphydaphy.rebound.engine.asset;

import com.raphydaphy.rebound.util.ResourceName;

import java.util.HashMap;
import java.util.Map;

public class Font {
    Map<String, Character> characters = new HashMap<>();
    private ResourceName name;

    public Font(ResourceName name) {
        this.name = name;
    }

    public void draw(String text, int color) {

    }

    public static class Character {

    }
}
