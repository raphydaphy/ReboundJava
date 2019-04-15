package com.raphydaphy.rebound.engine.asset;

import com.raphydaphy.rebound.util.ResourceName;
import org.lwjgl.opengl.GL30;

import java.util.Map;

public class TextureManager {
    public static final ResourceName missing = new ResourceName("textures/missing.png");

    private final Map<ResourceName, Sprite> atlas;
    private int atlasID, atlasWidth, atlasHeight;

    public TextureManager(TextureStitcher stitcher) {
        stitcher.load(missing);
        this.atlas = stitcher.stitch(true);
        this.atlasID = stitcher.getAtlasID();
        this.atlasWidth = stitcher.getAtlasWidth();
        this.atlasHeight = stitcher.getAtlasHeight();
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, atlasID);
    }

    public Sprite get(ResourceName name) {
        if (atlas.containsKey(name)) return atlas.get(name);
        return atlas.get(missing);
    }

    public int getAtlasWidth() {
        return atlasWidth;
    }

    public int getAtlasHeight() {
        return atlasHeight;
    }
}
