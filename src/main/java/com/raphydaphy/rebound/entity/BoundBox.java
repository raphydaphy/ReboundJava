package com.raphydaphy.rebound.entity;

public class BoundBox {
    private float x, y, width, height;

    public BoundBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(BoundBox other) {
        return intersects(0, 0, other);
    }

    public boolean intersects(float x, float y, BoundBox other) {
        return this.x + x + width >= other.getX() && other.getX() + other.getWidth() >= this.x + x && this.y + y + height >= other.getY() && other.getY() + other.getHeight() >= this.y + y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
