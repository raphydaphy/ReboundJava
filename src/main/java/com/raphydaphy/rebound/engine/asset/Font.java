package com.raphydaphy.rebound.engine.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.util.ResourceName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Font {
    Map<Integer, Character> characters = new HashMap<>();
    private ResourceName atlas;

    public Font(ResourceName name) {
        this.atlas = name.append(".png");
        name = name.append(".json");
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
            JsonObject innerChar = charObject.getValue().getAsJsonObject();
            characters.put(Integer.parseInt(charObject.getKey()), new Character(innerChar.get("x").getAsInt(), innerChar.get("y").getAsInt(), innerChar.get("width").getAsInt(), innerChar.get("height").getAsInt(), innerChar.get("xOffset").getAsInt(), innerChar.get("yOffset").getAsInt(), innerChar.get("xAdvance").getAsInt()));
        }
    }

    public void draw(Renderer renderer, String text, int x, int y, int color) {
        draw(renderer, text, x, y, color, 1);
    }

    public void draw(Renderer renderer, String text, int x, int y, int color, int size) {
        Sprite sprite = renderer.getTextureManager().get(atlas);
        for (int i = 0; i < text.length(); i++) {
            int charID = (int)text.charAt(i);
            if (!characters.containsKey(charID)) continue;
            Character character = characters.get(charID);
            draw(renderer, sprite, character, x / size + character.xOFfset, y / size + character.yOffset, color, size);
            x += character.xAdvance * size;
        }
    }

    private void draw(Renderer renderer, Sprite sprite, Character character, int x, int y, int color, int scale) {
        float r = ((color & 0xFF0000) >> 16) / 255f;
        float g = ((color & 0xFF00) >> 8) / 255f;
        float b = (color & 0xFF) / 255f;
        sprite.draw(renderer, x, y, character.width, character.height, character.x, character.y, character.width, character.height, scale, r, g, b);
    }

    public static class Character {
        public final int x, y, width, height, xOFfset, yOffset, xAdvance;

        public Character(int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xOFfset = xOffset;
            this.yOffset = yOffset;
            this.xAdvance = xAdvance;
        }
    }
}
