package com.raphydaphy.rebound.render;

import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.engine.Window;
import com.raphydaphy.rebound.engine.render.Camera;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.state.GameState;
import com.raphydaphy.rebound.util.ResourceName;
import org.lwjgl.opengl.GL30;

public class GameRenderer {
    private Rebound rebound;
    private Window window;
    private Renderer renderer;
    private ShaderProgram worldShader;
    private ShaderProgram uiShader;

    public GameRenderer(Rebound rebound) {
        this.rebound = rebound;
        this.window = rebound.getWindow();
        this.renderer = new Renderer(rebound);
        this.renderer.getTextureManager().bind();
        this.worldShader = new ShaderProgram(new ResourceName("shaders/world")).init(window.getWidth(), window.getHeight());
        this.uiShader = new ShaderProgram(new ResourceName("shaders/world")).init(window.getWidth(), window.getHeight());
    }

    public void onResize(int width, int height) {
        worldShader.updateProjection(width, height);
        uiShader.updateProjection(width, height);
    }

    private ResourceName parchment = new ResourceName("textures/parchment.png");
    private ResourceName scepter = new ResourceName("textures/scepter.png");
    private ResourceName boiler = new ResourceName("textures/boiler.png");
    private ResourceName island = new ResourceName("textures/island.png");
    private ResourceName slot = new ResourceName("textures/ui/slot.png");
    private ResourceName play = new ResourceName("textures/ui/play.png");

    public void render(float deltaTime) {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);


        this.renderer.useProgram(worldShader);
        this.renderer.applyCamera(deltaTime);
        this.renderer.begin();

        if (rebound.getState() == GameState.INGAME) {
            this.renderer.getTextureManager().get(parchment).draw(this.renderer, 0, 0);
            this.renderer.getTextureManager().get(boiler).draw(this.renderer, 20, 13, 24, 60, 0, 0, 24, 60, 4, 0.3f, 1, 1);
            this.renderer.getTextureManager().get(island).draw(this.renderer, 80, 30);
        }

        this.renderer.draw();

        this.renderer.useProgram(uiShader);
        this.renderer.begin();

        if (rebound.getState() == GameState.MENU) {
            this.renderer.getTextureManager().get(play).draw(this.renderer, window.getWidth() / 4 - 64, window.getHeight() / 4 - 16, 2);
        } else {
            this.renderer.getTextureManager().get(slot).draw(this.renderer, window.getWidth() / 4 - 16, window.getHeight() / 2 - 36, 2);
            this.renderer.getTextureManager().get(slot).draw(this.renderer, window.getWidth() / 4 - 50, window.getHeight() / 2 - 36, 2);
            this.renderer.getTextureManager().get(slot).draw(this.renderer, window.getWidth() / 4 + 18, window.getHeight() / 2 - 36, 2);
            this.renderer.getTextureManager().get(scepter).draw(this.renderer, (int) window.getMouseX() / renderer.getScale() - 8, (int) window.getMouseY() / renderer.getScale() - 8);
        }

        this.renderer.draw();
    }

    public Camera getCamera() {
        return renderer.getCamera();
    }

    public void delete() {
        renderer.delete();
    }
}