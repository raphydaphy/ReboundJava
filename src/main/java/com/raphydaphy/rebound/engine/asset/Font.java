package com.raphydaphy.rebound.engine.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raphydaphy.rebound.util.ResourceName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Font {
    Map<String, Character> characters = new HashMap<>();
    private ResourceName name;

    public Font(ResourceName name) {
        this.name = name;

        InputStream stream = name.getInputStream();
        var source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read font from " + name + "! Printing stack trace...");
            e.printStackTrace();
        }

        JsonObject json = new JsonParser().parse(source.toString()).getAsJsonObject();

        JsonObject chars = json.getAsJsonObject("chars");
        for (Map.Entry<String, JsonElement> charObject : chars.entrySet()) {
            // TODO: parse
        }
    }

    public void draw(String text, int color) {

    }

    public static class Character {

    }
}
