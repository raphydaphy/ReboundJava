package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.engine.vertex.VertexArray;
import com.raphydaphy.rebound.engine.vertex.VertexBuffer;
import org.lwjgl.opengl.GL30;

public class RenderHelper {
    private static float[] verts = new float[]{
            -0.5f, -0.5f, 0,
            0.5f, -0.5f, 0,
            0.5f, 0.5f, 0,
            0.5f, 0.5f, 0,
            -0.5f, 0.5f, 0,
            -0.5f, -0.5f, 0
    };
    private static float[] uvs = new float[]{
            0, 1,
            1, 1,
            1, 0,
            1, 0,
            0, 0,
            0, 1
    };

    public static void texturedRect(int x, int y, int width, int height) {
        // Setup
        VertexArray vao = new VertexArray().bind();
        var vertBuffer = new VertexBuffer(GL30.GL_ARRAY_BUFFER).bind().upload(verts).unbind();
        var texBuffer = new VertexBuffer(GL30.GL_ARRAY_BUFFER).bind().upload(uvs).unbind();

        // Render
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        vertBuffer.bind();
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);

        texBuffer.bind();
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);

        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        // Cleanup
        vertBuffer.delete();
        texBuffer.delete();
        vao.delete();
    }
}
