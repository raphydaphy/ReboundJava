package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.engine.render.Renderer;

public class RenderHelper {

    public static void texturedRect(Renderer renderer, int x, int y, int width, int height) {
        width *= 16;
        height *= 16;
        renderer.vertex(x, y, 0, 0).vertex(x + width, y, 1, 0).vertex(x + width, y + height, 1, 1);
        renderer.vertex(x + width, y + height, 1, 1).vertex(x, y + height, 0, 1).vertex(x, y, 0, 0);
    }
}
