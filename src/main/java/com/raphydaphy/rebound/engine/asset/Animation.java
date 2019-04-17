package com.raphydaphy.rebound.engine.asset;

import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.util.ResourceName;

public class Animation {
    private Sprite sprite;
    private int frameWidth, frameHeight, totalFrames;
    private float speed;

    public Animation(Sprite sprite, int totalFrames, float speed) {
        this.sprite = sprite;
        this.frameWidth = sprite.getWidth() / totalFrames;
        this.frameHeight = sprite.getHeight();
        this.totalFrames = totalFrames;
        this.speed = speed;
    }

    public void draw(Renderer renderer, float x, float y) {
        draw(renderer, x, y, renderer.getScale());
    }

    public void drawFrame(Renderer renderer, float x, float y, int frame) {
        sprite.draw(renderer, x, y, frameWidth, frameHeight, frame * frameWidth, 0, frameWidth, frameHeight, 1, 1, 1);
    }

    public void draw(Renderer renderer, float x, float y, int scale) {
        draw(renderer, x, y, frameWidth * scale, frameHeight * scale);
    }

    public void drawFrame(Renderer renderer, float x, float y, int scale, boolean flipHorizontal, int frame) {
        sprite.draw(renderer, x, y, frameWidth * scale, frameHeight * scale, frame * frameWidth, 0, frameWidth, frameHeight, 1, 1, 1);
    }

    public void draw(Renderer renderer, float x, float y, int width, int height) {
        draw(renderer, x, y, width, height, 0, 0, frameWidth, frameHeight);
    }

    public void draw(Renderer renderer, float x, float y, int width, int height, int u, int v, int maxU, int maxV) {
        draw(renderer, x, y, width, height, u, v, maxU, maxV, 1, 1, 1);
    }

    public void draw(Renderer renderer, float x, float y, int width, int height, int u, int v, int maxU, int maxV, float red, float green, float blue) {
        int frame = (int) ((long) (Rebound.getInstance().getTotalTicks() * speed) % totalFrames);
        sprite.draw(renderer, x, y, width, height, u + frame * frameWidth, v, maxU, maxV, red, green, blue);
    }
}
