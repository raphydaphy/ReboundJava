package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Texture {
    private ResourceLocation name;
    private int width, height, texture;

    public Texture(ResourceLocation path) {
        this.name = path;
        int[] pixels = null;
        try {
            BufferedImage image = ImageIO.read(path.getInputStream());
            this.width = image.getWidth();
            this.height = image.getHeight();
            pixels = new int[this.width * this.height];
            image.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);
        } catch (IOException e) {
            System.err.println("Failed to load texture " + path + "! Printing stack trace...");
            e.printStackTrace();
        }

        if (pixels != null) {
            int[] data = new int[this.width * this.height];
            for (int i = 0; i < this.width * this.height; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);

                data[i] = a << 24 | b << 16 | g << 8 | r;
            }

            this.texture = GL30.glGenTextures();
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.texture);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

            try (var stack = MemoryStack.stackPush()) {
                var buffer = stack.mallocInt(data.length);
                buffer.put(data).flip();
                GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, this.width, this.height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
            }

            GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        }
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.texture);
    }

    public void unbind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }
}
