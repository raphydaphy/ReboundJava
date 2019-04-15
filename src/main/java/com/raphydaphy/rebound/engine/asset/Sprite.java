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

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Renderer renderer, int x, int y) {
        draw(renderer, x, y, renderer.getScale());
    }

    public void draw(Renderer renderer, int x, int y, int scale) {
        draw(renderer, x, y, getWidth(), getHeight(), 0, 0, getWidth(), getHeight(), scale);
    }

    public void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int maxU, int maxV, int scale) {
        float startX = (float) (offsetX + u) / (float) renderer.getTextureManager().getAtlasWidth();
        float startY = (float) (offsetY + v) / (float) renderer.getTextureManager().getAtlasHeight();
        float endX = startX + (float) maxU / (float) renderer.getTextureManager().getAtlasWidth();
        float endY = startY + (float) maxV / (float) renderer.getTextureManager().getAtlasHeight();
        x *= scale;
        y *= scale;
        renderer.vertex(x, y, startX, startY).vertex(x + width * scale, y, endX, startY).vertex(x + width * scale, y + height * scale, endX, endY);
        renderer.vertex(x + width * scale, y + height * scale, endX, endY).vertex(x, y + height * scale, startX, endY).vertex(x, y, startX, startY);
    }
}
