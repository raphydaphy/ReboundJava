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

    private boolean initialized = false;
    private Window window;
    private Renderer renderer;

    private Rebound() {
        init();
        loop();
        cleanup();
    }

    public void onResized(int width, int height) {
        if (initialized) {
            renderer.onResize(width, height);
            GL30.glViewport(0, 0, width, height);
        }
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

        this.window = new Window(this, 720, 720, true, true);
    }

    private void loop() {
        GL.createCapabilities();
        GL30.glClearColor(1, 0, 0, 0);

        VertexArray vao = new VertexArray().bind();
        this.renderer = new Renderer(this);

        var program = new ShaderProgram(new ResourceLocation("shaders/textured"));
        program.init(window.getWidth(), window.getHeight());
        this.renderer.useProgram(program);
        Texture parchment = new Texture(new ResourceLocation("textures/written_parchment.png"));

        initialized = true;

        while (this.window.isOpen()) {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

            parchment.bind();

            this.renderer.begin();
            this.renderer.vertex(0, 0, 0, 0).vertex(1, 0, 1, 0).vertex(1, 1,  1, 1);
            this.renderer.vertex(1, 1, 1, 1).vertex(0, 1, 0, 1).vertex(0, 0, 0, 0);
            this.renderer.draw();

            parchment.unbind();

            this.window.swapBuffers();
            GLFW.glfwPollEvents();
        }

        vao.delete();
        renderer.delete();
    }

    private void cleanup() {
        window.destroy();

        GLFW.glfwTerminate();
        GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null);
        if (errorCallback != null) errorCallback.free();
    }

    public Window getWindow() {
        return window;
    }

    public static Rebound getInstance() {
        return INSTANCE;
    }

    public static void main(String... args) {
        INSTANCE = new Rebound();
    }
}
