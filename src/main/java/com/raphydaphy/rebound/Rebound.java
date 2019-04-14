package com.raphydaphy.rebound;

import com.raphydaphy.rebound.engine.Window;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.engine.render.Texture;
import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.engine.vertex.VertexArray;
import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

public class Rebound {
    public static String NAMESPACE = "rebound";
    private static Rebound INSTANCE;

    private Window window;
    private Renderer renderer;

    private Rebound() {
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

        this.window = new Window(720, 720, true, true);
    }

    private void loop() {
        GL.createCapabilities();
        GL30.glClearColor(1, 0, 0, 0);

        this.renderer = new Renderer();

        this.renderer.useProgram(new ShaderProgram(new ResourceLocation("shaders/textured")));
        Texture parchment = new Texture(new ResourceLocation("textures/written_parchment.png"));

        while (this.window.isOpen()) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

            parchment.bind();
            VertexArray vao = new VertexArray().bind();

            this.renderer.begin();
            this.renderer.vertex(-0.5f, -0.5f, 0, 0, 1).vertex(0.5f, -0.5f, 0, 1, 1).vertex(0.5f, 0.5f, 0, 1, 0);
            this.renderer.vertex(0.5f, 0.5f, 0, 1, 0).vertex(-0.5f, 0.5f, 0, 0, 0).vertex(-0.5f, -0.5f, 0, 0, 1);
            this.renderer.draw();
            vao.delete();
            parchment.unbind();

            this.window.swapBuffers();
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
    }
}
