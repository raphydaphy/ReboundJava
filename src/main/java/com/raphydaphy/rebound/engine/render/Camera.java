package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.util.Utils;
import org.joml.Matrix4f;

public class Camera {
    private float x, y, prevX, prevY;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
    }

    public void update() {
        prevX = x;
        prevY = y;
    }

    public void moveBy(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(ShaderProgram program, float deltaTime) {
        program.uniform("view", new Matrix4f().translate(Utils.lerp(prevX, x, deltaTime), Utils.lerp(prevY, y, deltaTime), 0));
    }
}
