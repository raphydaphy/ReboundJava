package com.raphydaphy.rebound.engine.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.util.ResourceName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Font {
    Map<Integer, Character> characters = new HashMap<>();
    private ResourceName atlas;
    private int lineHeight;

    public Font(ResourceName name) {
        this.atlas = name.append(".png");
        this.lineHeight = 30;
        name = name.append(".json");
        InputStream stream = name.getInputStream();
        var source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line);
            }
            stream.close();
        } catch (IOException e) {
            Rebound.getLogger().log(Level.WARNING, "Failed to read font from " + name + "!", e);
        }

        JsonObject json = new JsonParser().parse(source.toString()).getAsJsonObject();

        JsonObject chars = json.getAsJsonObject("chars");
        for (Map.Entry<String, JsonElement> charObject : chars.entrySet()) {
            JsonObject innerChar = charObject.getValue().getAsJsonObject();
            characters.put(Integer.parseInt(charObject.getKey()), new Character(innerChar.get("x").getAsInt(), innerChar.get("y").getAsInt(), innerChar.get("width").getAsInt(), innerChar.get("height").getAsInt(), innerChar.get("xOffset").getAsInt(), innerChar.get("yOffset").getAsInt(), innerChar.get("xAdvance").getAsInt()));
        }

        Rebound.getLogger().info("Initialized font " + name + " with " + characters.size() + " characters");
    }

    public void draw(Renderer renderer, String text, int x, int y, int color) {
        draw(renderer, text, x, y, color, 1);
    }

    public void draw(Renderer renderer, String text, int x, int y, int color, int size, int width) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curWidth = 0;
        for (String word : words) {
            int length = 0;
            for (int i = 0; i < word.length(); i++) {
                int charID = (int)word.charAt(i);
                if (!characters.containsKey(charID)) continue;
                length += characters.get(charID).xAdvance;
            }
            if (curWidth >= width && line.length() > 0) {
                draw(renderer, line.toString(), x, y, color, size);
                line = new StringBuilder();
                curWidth = 0;
                y += lineHeight;
            }
            length += characters.get((int)' ').xAdvance;
            line.append(word);
            line.append(" ");
            curWidth += length;
        }
        draw(renderer, line.toString(), x, y, color, size);
    }
    public void draw(Renderer renderer, String text, int x, int y, int color, int size) {
        Sprite sprite = renderer.getTextureManager().get(atlas);
        for (int i = 0; i < text.length(); i++) {
            int charID = (int)text.charAt(i);
            if (!characters.containsKey(charID)) continue;
            Character character = characters.get(charID);
            draw(renderer, sprite, character, x + character.xOFfset * size, y + character.yOffset * size, character.width * size, character.height * size, color);
            x += character.xAdvance * size;
        }
    }

    private void draw(Renderer renderer, Sprite sprite, Character character, int x, int y, int width, int height, int color) {
        float r = ((color & 0xFF0000) >> 16) / 255f;
        float g = ((color & 0xFF00) >> 8) / 255f;
        float b = (color & 0xFF) / 255f;
        sprite.draw(renderer, x, y, width, height, character.x, character.y, character.width, character.height, r, g, b);
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
