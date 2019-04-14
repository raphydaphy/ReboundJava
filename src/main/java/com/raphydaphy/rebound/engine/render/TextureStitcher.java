package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class TextureStitcher {
    private List<ResourceLocation> toStitch = new ArrayList<>();
    private int atlasID = 0;
    private int atlasWidth = 0;
    private int atlasHeight = 0;

    public Map<ResourceLocation, Sprite> stitch(boolean save) {
        List<StitchPos> stitchPositions = new ArrayList<>();

        int widest = 0;
        int lowestRowHeight = 0;
        int endX = 0;
        int endY = 0;

        // Calculate Positions
        for (ResourceLocation name : this.toStitch) {
            try {
                InputStream stream = name.getInputStream();
                BufferedImage tex = ImageIO.read(stream);
                stream.close();
                int x = 0;
                int y = 0;

                while (true) {
                    int index = this.getPos(x, y, tex.getWidth() + 2, tex.getHeight() + 2, stitchPositions);
                    if (index != -1) {
                        StitchPos pos = stitchPositions.get(index);

                        int height = pos.tex.getHeight() + 2;
                        if (lowestRowHeight == 0 || height < lowestRowHeight) {
                            lowestRowHeight = height;
                        }

                        x += pos.tex.getWidth() + 2;
                        if (x + tex.getWidth() >= Math.max(1024, widest)) {
                            x = 0;
                            y += lowestRowHeight;
                            lowestRowHeight = 0;
                        }
                    } else {
                        break;
                    }
                }

                stitchPositions.add(new StitchPos(tex, name, x + 1, y + 1));

                widest = Math.max(widest, tex.getWidth());
                endX = Math.max(endX, x + tex.getWidth());
                endY = Math.max(endY, y + tex.getHeight());
            } catch (Exception e) {
                System.err.println("Failed to stitch texture with name " + name + "! Printing stack trace...");
                e.printStackTrace();
            }
        }

        // Create Atlas
        BufferedImage buffer = new BufferedImage(endX + 2, endY + 2, BufferedImage.TYPE_4BYTE_ABGR);
        Map<ResourceLocation, Sprite> atlas = new HashMap<>();
        for (StitchPos pos : stitchPositions) {
            try {
                int width = pos.tex.getWidth();
                int height = pos.tex.getHeight();

                int[] pixels = new int[width * height];
                pos.tex.getRGB(0, 0, width, height, pixels, 0, width);
                buffer.setRGB(pos.x, pos.y, width, height, pixels, 0, width);
                Sprite sprite = new Sprite(pos.name, width, height, pos.x, pos.y);
                atlas.put(pos.name, sprite);
            } catch (Exception e) {
                System.err.println("Failed to stitch " + pos.name + "! Printing stack trace...");
                e.printStackTrace();
            }
        }

        // Convert to the right format
        this.atlasWidth = buffer.getWidth();
        this.atlasHeight = buffer.getHeight();
        var pixels = new int[this.atlasWidth * this.atlasHeight];
        buffer.getRGB(0, 0, atlasWidth, this.atlasHeight, pixels, 0, this.atlasWidth);
        var data = BufferUtils.createIntBuffer(buffer.getWidth() * buffer.getHeight());
        for (int i = 0; i < this.atlasWidth * this.atlasHeight; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data.put(i, a << 24 | b << 16 | g << 8 | r);
        }

        // Upload
        data.flip();
        this.atlasID = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.atlasID);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, this.atlasWidth, this.atlasHeight, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, data);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

        // Save to disk
        if (save) {
            try {
                ImageIO.write(buffer, "png", new File("rebound/spritesheet.png"));
            } catch (IOException e) {
                System.err.println("Failed to save sprite sheet! Printing stack trace...");
                e.printStackTrace();
            }
        }

        return atlas;
    }

    public int getAtlasID() {
        return atlasID;
    }

    public int getAtlasWidth() {
        return atlasWidth;
    }

    public int getAtlasHeight() {
        return atlasHeight;
    }

    private int getPos(int x, int y, int width, int height, List<StitchPos> positions) {
        for (int i = 0; i < positions.size(); i++) {
            StitchPos position = positions.get(i);
            if (x <= position.x + position.tex.getWidth() && x + width >= position.x && y <= position.y + position.tex.getHeight() && y + height >= position.y) {
                return i;
            }
        }
        return -1;
    }

    public void load(ResourceLocation name) {
        toStitch.add(name);
    }

    public void loadAll(ResourceLocation dir) {
        String dirString = "assets/" + dir.getNamespace() + "/" + dir.getResource();
        try {
            URL url = ClassLoader.getSystemClassLoader().getResource(dirString);
            if (url == null) {
                System.err.println("Failed to access directory for texture stitching with name " + dir + "!");
                return;
            }
            URI uri = url.toURI();
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                loadAll(dir, fileSystem.getPath(dirString));
            } else {
                loadAll(dir, Paths.get(uri));
            }
        } catch (Exception e) {
            System.err.println("Failed to load textures from directory " + dir + "! Printing stack trace...");
            e.printStackTrace();
        }
    }

    private void loadAll(ResourceLocation dir, Path path) throws IOException {
        Files.walk(path, 1).forEach((fPath) -> {
            if (Files.isDirectory(fPath)) {
                if (!fPath.equals(path)) {
                    try {
                        loadAll(dir.append("/" + fPath.getName(path.getNameCount()).toString()), fPath);
                    } catch (IOException e) {
                        System.err.println("Failed to load nested textures from " + fPath + "! Printing stack trace...");
                    }
                }
            } else {
                String name = fPath.getName(path.getNameCount()).toString();
                if (name.endsWith("png")) {
                    ResourceLocation loc = dir.append("/" + name);
                    load(loc);
                }
            }
        });
    }

    private static class StitchPos {
        final BufferedImage tex;
        final ResourceLocation name;
        final int x;
        final int y;

        StitchPos(BufferedImage tex, ResourceLocation name, int x, int y) {
            this.tex = tex;
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
}
