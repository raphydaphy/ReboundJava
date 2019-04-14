package com.raphydaphy.rebound.engine;

import com.raphydaphy.rebound.Rebound;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private final long id;

    private int width;
    private int height;

    private float mouseX;
    private float mouseY;

    public Window(Rebound rebound, int width, int height, boolean vsync, boolean centered) {
        id = GLFW.glfwCreateWindow(width, height, "Rebound", MemoryUtil.NULL, MemoryUtil.NULL);
        if (id == MemoryUtil.NULL) throw new RuntimeException("Failed to create GLFW window");

        GLFW.glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> rebound.onKey(key, action) );

        GLFW.glfwSetFramebufferSizeCallback(id, (window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            rebound.onResized(newWidth, newHeight);
        });

        GLFW.glfwSetCursorPosCallback(id, (window, mouseX, mouseY) -> {
            this.mouseX = (float) mouseX;
            this.mouseY = (float) mouseY;
        });

        GLFW.glfwSetMouseButtonCallback(id, ((window, button, action, mods) -> rebound.onMouse(button, action, mouseX, mouseY)));

        if (centered) {
            try (var stack = MemoryStack.stackPush()) {
                var pWidth = stack.mallocInt(1);
                var pHeight = stack.mallocInt(1);
                GLFW.glfwGetWindowSize(id, pWidth, pHeight);

                var vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                if (vidMode != null)
                    GLFW.glfwSetWindowPos(id, (vidMode.width() - pWidth.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
            }
        }

        GLFW.glfwMakeContextCurrent(id);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        GLFW.glfwShowWindow(id);

        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public void swapBuffers()
    {
        GLFW.glfwSwapBuffers(id);
    }

    public void close() {
        GLFW.glfwSetWindowShouldClose(id, true);
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(id);
        GLFW.glfwDestroyWindow(id);
    }

    public boolean isOpen()
    {
        return !GLFW.glfwWindowShouldClose(id);
    }
}
