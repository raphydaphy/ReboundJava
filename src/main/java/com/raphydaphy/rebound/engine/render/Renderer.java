package com.raphydaphy.rebound.engine.render;

import com.raphydaphy.rebound.engine.shader.ShaderProgram;
import com.raphydaphy.rebound.engine.vertex.VertexBuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Renderer {
    private static int maxVerts = 65534;

    private FloatBuffer buffer;
    private ShaderProgram program;
    private VertexBuffer vbo;
    private boolean drawing = false;
    private int verts = 0;
    private int components = 0;

    public Renderer() {
        this.buffer = MemoryUtil.memAllocFloat(maxVerts);
        this.vbo = new VertexBuffer(GL30.GL_ARRAY_BUFFER);
    }

    public void useProgram(ShaderProgram program) {
        if (drawing) {
            System.err.println("Tried to change shader program mid-render!");
            return;
        }
        this.program = program;
    }

    public Renderer vertex(float x, float y, float z, float u, float v) {
        return with(x).with(y).with(z).with(u).with(v);
    }

    public Renderer with(float value) {
        if (!drawing) {
            System.err.println("Tried to add vertex data when not drawing!");
            return this;
        }
        buffer.put(value);
        components++;

        if (components >= program.getVertexSize()) {
            components = 0;
            verts++;
        }
        return this;
    }

    public void begin() {
        if (drawing) {
            System.err.println("Tried to restart rendering mid-render!");
            return;
        }
        drawing = true;
        verts = 0;
        components = 0;
        buffer.clear();
    }

    public void draw() {
        if (!drawing) {
            System.err.println("Tried to stop rendering which hadn't started!");
            return;
        }
        drawing = false;

        if (verts > 0) {
            buffer.flip();
            program.bind();
            GL30.glEnableVertexAttribArray(0);
            //GL30.glEnableVertexAttribArray(1);

            vbo.bind().upload(buffer);

            GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 2, 0);
           // GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 3, 3);
            GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, verts);

            vbo.unbind();

            GL30.glDisableVertexAttribArray(0);
           // GL30.glDisableVertexAttribArray(1);

            program.unbind();
            buffer.clear();
        }
    }
}
