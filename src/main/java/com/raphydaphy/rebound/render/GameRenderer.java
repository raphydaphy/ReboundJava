package com.raphydaphy.rebound.render;

import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.engine.Window;
import com.raphydaphy.rebound.engine.asset.Font;
import com.raphydaphy.rebound.engine.render.Camera;
import com.raphydaphy.rebound.engine.render.Renderer;
import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.state.GameState;
import com.raphydaphy.rebound.util.ResourceName;
import com.raphydaphy.rebound.util.Utils;
import org.lwjgl.opengl.GL30;

public class GameRenderer {
    private Rebound rebound;
    private Window window;
    private Renderer renderer;
    private ShaderProgram worldShader;
    private ShaderProgram uiShader;
    private Font font;

    public GameRenderer(Rebound rebound) {
        this.rebound = rebound;
        this.window = rebound.getWindow();
        this.renderer = new Renderer(rebound);
        this.renderer.getTextureManager().bind();
        this.worldShader = new ShaderProgram(new ResourceName("shaders/world")).init(window.getWidth(), window.getHeight());
        this.uiShader = new ShaderProgram(new ResourceName("shaders/world")).init(window.getWidth(), window.getHeight());
        this.font = new Font(new ResourceName("fonts/alagard"));
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
    private ResourceName character = new ResourceName("textures/character.png");

    public void render(float deltaTime) {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);


        this.renderer.useProgram(worldShader);
        this.renderer.applyCamera(deltaTime);
        this.renderer.begin();

        if (rebound.getState() == GameState.INGAME) {
            this.renderer.getTextureManager().getSprite(parchment).draw(this.renderer, 250, 150, 4);
            this.renderer.getTextureManager().getSprite(boiler).draw(this.renderer, 40, 26, 96, 240, 0, 0, 24, 60, 4, 0.3f, 1);
            this.renderer.getTextureManager().getSprite(island).draw(this.renderer, 160, 300, 4);
            float playerX = Utils.lerp(rebound.getPlayer().getPrevX(), rebound.getPlayer().getX(), deltaTime);
            float playerY = Utils.lerp(rebound.getPlayer().getPrevY(), rebound.getPlayer().getY(), deltaTime);
            if (rebound.getPlayer().getVelocityX() != 0) {
                this.renderer.getTextureManager().getAnimation(character, 4, 0.25f).draw(this.renderer, playerX, playerY, 2);
            } else {
                this.renderer.getTextureManager().getAnimation(character, 4, 0.25f).drawFrame(this.renderer, playerX, playerY, 2, true, 0);
            }
        }

        this.renderer.draw();

        this.renderer.useProgram(uiShader);
        this.renderer.begin();

        if (rebound.getState() == GameState.MENU) {
            font.draw(this.renderer, "Rebound", 40, 20, 0, 2);
            font.draw(this.renderer, "a super epic game", 42, 70, 0x46494f);
            font.draw(this.renderer, "Made by raphydaphy with moral support from (many people). This paragraph is intentionally long to test line wrapping! Hopefully it doesn't break anything!", window.getWidth() / 2 - 200, window.getHeight() - 200, 0x46494f, 1, 400);
            this.renderer.getTextureManager().getSprite(play).draw(this.renderer, window.getWidth() / 2 - 128, window.getHeight() / 2 - 32);
        } else {
            font.draw(this.renderer, rebound.getFPS() + " FPS", 10, 10, 0x46494f);
            this.renderer.getTextureManager().getSprite(slot).draw(this.renderer, window.getWidth() / 2 - 32, window.getHeight() - 72);
            this.renderer.getTextureManager().getSprite(slot).draw(this.renderer, window.getWidth() / 2 - 100, window.getHeight() - 72);
            this.renderer.getTextureManager().getSprite(slot).draw(this.renderer, window.getWidth() / 2 + 36, window.getHeight() - 72);
            this.renderer.getTextureManager().getSprite(scepter).draw(this.renderer, (int) window.getMouseX() - 8, (int) window.getMouseY() - 8);
        }

        this.renderer.draw();
    }

    public Camera getCamera() {
        return renderer.getCamera();
    }

    public void delete() {
        worldShader.delete();
        uiShader.delete();
        renderer.delete();
    }
}
