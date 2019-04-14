package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation.. assumes that all textures are 16x16
 */
public class TextureManager {
    private static final ResourceLocation missing = new ResourceLocation("textures/missing.png");

    private Map<ResourceLocation, Sprite> sprites = new HashMap<>();
    private int textureID = 0;
    private int width = 256;
    private int height = 256;
    private int curX = 0;
    private int curY = 0;

    public TextureManager() {
        addTexture(missing);
    }

    public void addTexture(ResourceLocation name) {
        sprites.put(name, new Sprite(name, curX * 16, curY * 16));
        curX++;
        if (curX >= 16) {
            curX = 0;
            curY++;
        }

        if (curY >= height) {
            System.err.println("Too many textures added ! I haven't programmed this path yet!");
        }
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
    }

    public void stitch(boolean save) {
        BufferedImage spriteSheet = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        // Generate
        for (Map.Entry<ResourceLocation, Sprite> sprite : sprites.entrySet()) {
            try {
                BufferedImage tex = ImageIO.read(sprite.getKey().getInputStream());
                sprite.getValue().setSize(tex.getWidth(), tex.getHeight(), this.width, this.height);
                int[] pixels = new int[16 * 16];
                tex.getRGB(0, 0, 16, 16, pixels, 0, 16);
                spriteSheet.setRGB(sprite.getValue().getOffsetX(), sprite.getValue().getOffsetY(), 16, 16, pixels, 0, 16);
                tex.flush();
            } catch (IOException e) {
                System.err.println("Failed to read texture " + sprite.getKey() + "! Printing stack trace...");
                e.printStackTrace();
            }
        }

        // Upload
        int[] pixels = new int[this.width * this.height];
        spriteSheet.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);
        int[] data = new int[this.width * this.height];
        for (int i = 0; i < this.width * this.height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }
        this.textureID = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.textureID);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

        var buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data).flip();
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, this.width, this.height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

        // Save to disk
        if (save) {
            try {
                ImageIO.write(spriteSheet, "png", new File("rebound/spritesheet.png"));
            } catch (IOException e) {
                System.err.println("Failed to save sprite sheet! Printing stack trace...");
                e.printStackTrace();
            }
        }
    }

    public Sprite get(ResourceLocation name) {
        if (sprites.containsKey(name)) return sprites.get(name);
        return sprites.get(missing);
    }
}
