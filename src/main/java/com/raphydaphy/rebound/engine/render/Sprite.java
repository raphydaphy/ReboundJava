package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.util.ResourceLocation;

public class Sprite {
    private final ResourceLocation name;
    private int width, height, offsetX, offsetY;


    public Sprite(ResourceLocation name, int width, int height, int offsetX, int offsetY) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public ResourceLocation getName() {
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
        float startX = (float) offsetX / (float) renderer.getTextureManager().getAtlasWidth();
        float startY = (float) offsetY / (float) renderer.getTextureManager().getAtlasHeight();
        float endX = startX + (float) width / (float) renderer.getTextureManager().getAtlasWidth();
        float endY = startY + (float) height / (float) renderer.getTextureManager().getAtlasHeight();
        x *= renderer.getScale();
        y *= renderer.getScale();
        renderer.vertex(x, y, startX, startY).vertex(x + width * renderer.getScale(), y, endX, startY).vertex(x + width * renderer.getScale(), y + height * renderer.getScale(), endX, endY);
        renderer.vertex(x + width * renderer.getScale(), y + height * renderer.getScale(), endX, endY).vertex(x, y + height * renderer.getScale(), startX, endY).vertex(x, y, startX, startY);
    }
}
