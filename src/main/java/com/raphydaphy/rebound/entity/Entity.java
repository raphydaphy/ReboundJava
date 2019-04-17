package com.raphydaphy.rebound.entity;

public class Entity {
    private float x, y, prevX, prevY, velocityX, velocityY;
    private boolean onGround = false;
    private BoundBox bounds;

    public Entity(float width, float height) {
        setPos(0, 0);
        this.prevX = 0;
        this.prevY = 0;
        this.bounds = new BoundBox(0, 0, width, height);
    }

    public Entity(float x, float y, float width, float height) {
        setPos(x, y);
        this.prevX = x;
        this.prevY = y;
        this.bounds = new BoundBox(0, 0, width, height);
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void addVelocity(float x, float y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    public void update(BoundBox ground) {
        this.prevX = x;
        this.prevY = y;
        this.onGround = bounds.intersects(x, y, ground);
        x += velocityX;
        if (velocityY < 0) {
            if (!onGround && !bounds.intersects(x, y + velocityY, ground)) {
                y += velocityY;
            }
        } else {
            y += velocityY;
        }
        velocityX = 0;
        if (onGround) {
            velocityY = 0;
        } else {
            this.velocityY -= -0.4f;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getPrevX() {
        return prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
}
