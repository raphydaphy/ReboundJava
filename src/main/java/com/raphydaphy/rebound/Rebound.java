package com.raphydaphy.rebound;

import com.raphydaphy.rebound.engine.Window;
import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

public class Rebound {
    public static String NAMESPACE = "rebound";
    private static Rebound INSTANCE;

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
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        window = new Window(1080, 720, true, true);
    }

    private void loop() {
        GL.createCapabilities();
        GL30.glClearColor(1, 0, 0, 0);

        var program = new ShaderProgram(new ResourceLocation("shaders/basic"));

        // Triangle Test
        int vertexArray = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArray);
        var triangle = new float[]{
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        };
        int vertexBuffer = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, triangle, GL30.GL_STATIC_DRAW);

        while (window.isOpen()) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

            program.bind();

            GL30.glEnableVertexAttribArray(0);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
            GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
            GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);
            GL30.glDisableVertexAttribArray(0);

            program.unbind();

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
        return INSTANCE;
    }

    public static void main(String... args) {
        INSTANCE = new Rebound();
        INSTANCE.run();
    }
}
