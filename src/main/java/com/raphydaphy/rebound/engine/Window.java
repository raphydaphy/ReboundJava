package com.raphydaphy.rebound.engine;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private final long id;

    public Window(int width, int height, boolean vsync, boolean centered) {
        id = GLFW.glfwCreateWindow(width, height, "Rebound", MemoryUtil.NULL, MemoryUtil.NULL);
        if (id == MemoryUtil.NULL) throw new RuntimeException("Failed to create GLFW window");

        GLFW.glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(window, true);
        });

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
    }

    public void swapBuffers()
    {
        GLFW.glfwSwapBuffers(id);
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