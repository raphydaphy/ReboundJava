package com.raphydaphy.rebound.engine.asset;

import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.util.ResourceName;

public class Sprite {
    private final ResourceName name;
    private int width, height, offsetX, offsetY;


    public Sprite(ResourceName name, int width, int height, int offsetX, int offsetY) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public ResourceName getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void draw(Renderer renderer, float x, float y) {
        draw(renderer, x, y, renderer.getScale());
    }

    public void draw(Renderer renderer, float x, float y, int scale) {
        draw(renderer, x, y, getWidth() * scale, getHeight() * scale);
    }

    public void draw(Renderer renderer, float x, float y, int width, int height) {
        draw(renderer, x, y, width, height, 0, 0, getWidth(), getHeight());
    }

    public void draw(Renderer renderer, float x, float y, int width, int height, int u, int v, int maxU, int maxV) {
        draw(renderer, x, y, width, height, u, v, maxU, maxV, 1, 1, 1);
    }

    public void draw(Renderer renderer, float x, float y, int width, int height, int u, int v, int maxU, int maxV, float red, float green, float blue) {
        float startX = (float) (offsetX + u) / (float) renderer.getTextureManager().getAtlasWidth();
        float startY = (float) (offsetY + v) / (float) renderer.getTextureManager().getAtlasHeight();
        float endX = startX + (float) maxU / (float) renderer.getTextureManager().getAtlasWidth();
        float endY = startY + (float) maxV / (float) renderer.getTextureManager().getAtlasHeight();
        renderer.vertex(x, y, startX, startY, red, green, blue).vertex(x + width, y, endX, startY, red, green, blue).vertex(x + width, y + height, endX, endY, red, green, blue);
        renderer.vertex(x + width, y + height, endX, endY, red, green, blue).vertex(x, y + height, startX, endY, red, green, blue).vertex(x, y, startX, startY, red, green, blue);
    }
}
