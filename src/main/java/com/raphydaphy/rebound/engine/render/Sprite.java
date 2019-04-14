package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.util.ResourceLocation;

public class Sprite {
    private final ResourceLocation name;
    private int width, height, offsetX, offsetY, atlasWidth, atlasHeight;

    public Sprite(ResourceLocation name, int offsetX, int offsetY) {
        this.name = name;
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

    public void setSize(int width, int height, int atlasWidth, int atlasHeight) {
        this.width = width;
        this.height = height;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
    }

    public void draw(Renderer renderer, int x, int y) {
        float startX = (float) offsetX / (float) atlasWidth;
        float startY = (float) offsetY / (float) atlasHeight;
        float endX = startX + (float) width / (float) atlasWidth;
        float endY = startY + (float) height / (float) atlasHeight;
        x *= renderer.getScale();
        y *= renderer.getScale();
        renderer.vertex(x, y, startX, startY).vertex(x + width * renderer.getScale(), y, endX, startY).vertex(x + width * renderer.getScale(), y + height * renderer.getScale(), endX, endY);
        renderer.vertex(x + width * renderer.getScale(), y + height * renderer.getScale(), endX, endY).vertex(x, y + height * renderer.getScale(), startX, endY).vertex(x, y, startX, startY);
    }
}
