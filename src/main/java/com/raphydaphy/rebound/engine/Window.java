package com.raphydaphy.rebound.engine;

import com.google.common.io.ByteStreams;
import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.util.ResourceName;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

public class Window {
    private final long id;

    private int width;
    private int height;

    private float mouseX;
    private float mouseY;

    public Window(Rebound rebound, int width, int height, boolean vsync, boolean centered) {
        id = GLFW.glfwCreateWindow(width, height, "Rebound", MemoryUtil.NULL, MemoryUtil.NULL);
        if (id == MemoryUtil.NULL) throw new RuntimeException("Failed to create GLFW window");

        GLFW.glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> rebound.onKey(key, action));

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

    public void setIcon(ResourceName iconName) {
        ByteBuffer rawImageBuffer;
        int width, height;

        byte[] rawData = null;
        try (InputStream stream = iconName.getInputStream()) {
            rawData = ByteStreams.toByteArray(stream);
        } catch (IOException e) {
            Rebound.getLogger().log(Level.WARNING, "Failed to set icon to " + iconName + "!", e);
        }
        if (rawData == null) return;
        var bufferedData = BufferUtils.createByteBuffer(rawData.length);
        bufferedData.put(rawData);
        bufferedData.flip();
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            rawImageBuffer = STBImage.stbi_load_from_memory(bufferedData, widthBuffer, heightBuffer, stack.mallocInt(1), 4);
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
        }
        if (rawImageBuffer == null) {
            Rebound.getLogger().log(Level.WARNING, "Failed to load window icon " + iconName + "! Using default icon.");
            return;
        }

        GLFWImage glfwImage = GLFWImage.malloc();
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
        glfwImage.set(width, height, rawImageBuffer);
        imageBuffer.put(0, glfwImage);
        GLFW.glfwSetWindowIcon(id, imageBuffer);
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

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(id);
    }

    public boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(id, key) == GLFW.GLFW_PRESS;
    }

    public void close() {
        GLFW.glfwSetWindowShouldClose(id, true);
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(id);
        GLFW.glfwDestroyWindow(id);
    }

    public boolean isOpen() {
        return !GLFW.glfwWindowShouldClose(id);
    }
}
