package com.raphydaphy.rebound.engine;

import org.lwjgl.glfw.GLFW;

public class Timer {
    private int targetTPS;

    private double lastLoopTime;
    private float timeCount;

    private int fps;
    private int fpsCount;

    private int tps;
    private int tpsCount;

    public Timer(int targetTPS) {
        this.targetTPS = targetTPS;
        lastLoopTime = GLFW.glfwGetTime();
    }

    public float getDeltaTime() {
        double time = getTime();
        float delta = (float) (time - lastLoopTime);
        lastLoopTime = time;
        timeCount += delta;
        return delta;
    }

    public double getTime() {
        return GLFW.glfwGetTime();
    }

    public int getTargetTPS() {
        return targetTPS;
    }

    public void updateFPS() {
        fpsCount++;
    }

    public void updateTPS() {
        tpsCount++;
    }

    public void update() {
        if (timeCount > 1f) {
            fps = fpsCount;
            fpsCount = 0;

            tps = tpsCount;
            tpsCount = 0;

            timeCount -= 1f;
        }
    }

    public int getFPS() {
        return fps > 0 ? fps : fpsCount;
    }

    public int getTPS() {
        return tps > 0 ? tps : tpsCount;
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }
}
