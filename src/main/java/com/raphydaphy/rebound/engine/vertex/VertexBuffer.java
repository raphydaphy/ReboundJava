package com.raphydaphy.rebound.engine.vertex;

import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class VertexBuffer {
    private final int type;
    private final int id;

    public VertexBuffer(int type) {
        this.type = type;
        this.id = GL30.glGenBuffers();
    }

    public VertexBuffer bind() {
        GL30.glBindBuffer(type, id);
        return this;
    }

    public VertexBuffer unbind() {
        GL30.glBindBuffer(type, 0);
        return this;
    }

    public VertexBuffer upload(long data) {
        GL30.glBufferData(type, data, GL30.GL_STATIC_DRAW);
        return this;
    }

    public VertexBuffer upload(float[] data) {
        GL30.glBufferData(type, data, GL30.GL_STATIC_DRAW);
        return this;
    }

    public VertexBuffer upload(FloatBuffer data) {
        GL30.glBufferData(type, data, GL30.GL_STATIC_DRAW);
        return this;
    }

    public void delete() {
        GL30.glDeleteBuffers(id);
    }
}
