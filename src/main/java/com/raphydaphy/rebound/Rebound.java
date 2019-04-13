package com.raphydaphy.rebound;

import com.raphydaphy.rebound.engine.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Rebound {
    private static Rebound instance;

    private Window window;

    private void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new IllegalStateException("Failed to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = new Window(1080, 720, true, true);
    }

    private void loop() {
        GL.createCapabilities();
        GL11.glClearColor(1, 0, 0, 0);

        while (window.isOpen()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            window.swapBuffers();
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        window.destroy();

        GLFW.glfwTerminate();
        GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null);
        if (errorCallback != null) errorCallback.free();
    }

    public static Rebound getInstance() {
        return instance;
    }

    public static void main(String... args) {
        instance = new Rebound();
        instance.run();
    }
}
