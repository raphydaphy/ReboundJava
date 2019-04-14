package com.raphydaphy.rebound.engine.vertex;

import org.lwjgl.opengl.GL30;

public class VertexArray {
    private final int id;

    public VertexArray() {
        this.id = GL30.glGenVertexArrays();
    }

    public VertexArray bind() {
        GL30.glBindVertexArray(id);
        return this;
    }

    public VertexArray unbind() {
        GL30.glBindVertexArray(0);
        return this;
    }

    public void delete() {
        unbind();
        GL30.glDeleteVertexArrays(id);
    }
}
