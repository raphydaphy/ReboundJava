package com.raphydaphy.rebound;

import com.raphydaphy.rebound.asset.Sounds;
import com.raphydaphy.rebound.engine.Timer;
import com.raphydaphy.rebound.engine.Window;
import com.raphydaphy.rebound.engine.asset.Sound;
import com.raphydaphy.rebound.engine.asset.SoundManager;
import com.raphydaphy.rebound.engine.vertex.VertexArray;
import com.raphydaphy.rebound.render.GameRenderer;
import com.raphydaphy.rebound.state.GameState;
import com.raphydaphy.rebound.util.Logging;
import com.raphydaphy.rebound.util.ResourceName;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Rebound {
    public static String NAMESPACE = "rebound";
    private static Rebound INSTANCE;

    private boolean initialized = false;
    private Window window;
    private SoundManager soundManager;
    private GameRenderer renderer;
    private GameState state;
    private Timer timer;

    private void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        Logging.setLevel(Level.INFO);
        GLFW.glfwSetErrorCallback(GLFWErrorCallback.create((error, description) -> {
            Logging.glfw.log(Level.WARNING, GLFWErrorCallback.getDescription(description), new RuntimeException());
        }));
        if (!GLFW.glfwInit()) throw new IllegalStateException("Failed to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        this.window = new Window(this, 1080, 720, true, true);
        this.window.setIcon(new ResourceName("textures/parchment.png"));
        this.timer = new Timer(20);
        this.soundManager = new SoundManager();
    }

    public void onMouse(int button, int action, float mouseX, float mouseY) {
        if (state == GameState.MENU) {
            float centerY = (float) window.getHeight() / 2;
            float centerX = (float) window.getWidth() / 2;
            if (action == GLFW.GLFW_RELEASE && mouseY >= centerY - 32 && mouseY <= centerY + 32 && mouseX >= centerX - 128 && mouseX <= centerX + 128) {
                source.play(Sounds.click);
                setState(GameState.INGAME);
            }
        }
    }

    public void onKey(int key, int action) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_Q) window.close();
            else if (key == GLFW.GLFW_KEY_ESCAPE && state == GameState.INGAME) {
                setState(GameState.MENU);
            }
        }
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        if (state == GameState.MENU) {
            GL30.glClearColor(0.176470f, 0.717647f, 0.717647f, 0);
        } else if (state == GameState.INGAME) {
            GL30.glClearColor(0.725490f, 0.819607f, 0.968627f, 0);
        }
    }

    public void onResized(int width, int height) {
        if (initialized) {
            renderer.onResize(width, height);
            GL30.glViewport(0, 0, width, height);
        }
    }

    private Sound.Source source;

    private void loop() {
        GL.createCapabilities();

        VertexArray vao = new VertexArray().bind();

        renderer = new GameRenderer(this);
        getLogger().info("Initialized Engine!");
        initialized = true;


        Sounds.init();
        source = new Sound.Source();

        float delta;
        float accumulator = 0f;
        float interval = 1f / timer.getTargetTPS();
        float alpha;

        setState(GameState.MENU);

        while (this.window.isOpen()) {
            accumulator += timer.getDeltaTime();

            while (accumulator >= interval)
            {
                update();
                timer.updateTPS();
                accumulator -= interval;
            }

            this.renderer.render(accumulator / interval);
            this.window.swapBuffers();
            GLFW.glfwPollEvents();
            timer.updateFPS();

            timer.update();
        }

        source.delete();

        vao.delete();
        renderer.delete();
    }

    private void update() {
        if (state == GameState.INGAME) {
            float cameraSpeed = 3;
            renderer.getCamera().update();
            if (window.isKeyDown(GLFW.GLFW_KEY_W)) {
                renderer.getCamera().moveBy(0, -cameraSpeed);
            }
            if (window.isKeyDown(GLFW.GLFW_KEY_S)) {
                renderer.getCamera().moveBy(0, cameraSpeed);
            }
            if (window.isKeyDown(GLFW.GLFW_KEY_A)) {
                renderer.getCamera().moveBy(-cameraSpeed, 0);
            }
            if (window.isKeyDown(GLFW.GLFW_KEY_D)) {
                renderer.getCamera().moveBy(cameraSpeed, 0);
            }
        }
    }

    private void cleanup() {
        soundManager.delete();
        window.destroy();

        GLFW.glfwTerminate();
        GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null);
        if (errorCallback != null) errorCallback.free();
    }

    public Window getWindow() {
        return window;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public int getFPS() {
        return timer.getFPS();
    }

    public int getTPS() {
        return timer.getTPS();
    }

    public static Rebound getInstance() {
        return INSTANCE;
    }

    public static Logger getLogger() {
        return Logging.main;
    }

    public static void main(String... args) {
        INSTANCE = new Rebound();
        INSTANCE.run();
    }
}
