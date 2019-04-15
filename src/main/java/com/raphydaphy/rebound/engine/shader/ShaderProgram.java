package com.raphydaphy.rebound.engine.shader;

import com.raphydaphy.rebound.util.ResourceName;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {
    private final int program;
    private final int vertexShader;
    private final int fragmentShader;

    private int attribOffset = 0;
    private Map<String, Integer> uniforms = new HashMap<>();
    private Map<String, Integer> attributes = new HashMap<>();

    public ShaderProgram(ResourceName source) {
        vertexShader = compileShader(new ResourceName(source.getNamespace(), source.getResourceName() + ".vert"), GL30.GL_VERTEX_SHADER);
        fragmentShader = compileShader(new ResourceName(source.getNamespace(), source.getResourceName() + ".frag"), GL30.GL_FRAGMENT_SHADER);
        program = GL30.glCreateProgram();

        GL30.glAttachShader(program, vertexShader);
        GL30.glAttachShader(program, fragmentShader);
        GL30.glLinkProgram(program);

        try (var stack = MemoryStack.stackPush()) {
            var success = stack.mallocInt(1);
            GL30.glGetProgramiv(program, GL30.GL_COMPILE_STATUS, success);
            if (success.get(0) == GL30.GL_FALSE) {
                var logLength = stack.mallocInt(1);
                GL30.glGetProgramiv(program, GL30.GL_INFO_LOG_LENGTH, logLength);
                System.err.println("Failed to link shader program with source " + source + "! Printing info log... \n" + GL30.glGetProgramInfoLog(program));
            }
        }

        GL30.glDetachShader(program, vertexShader);
        GL30.glDetachShader(program, fragmentShader);
        GL30.glDeleteShader(vertexShader);
        GL30.glDeleteShader(fragmentShader);
    }

    public ShaderProgram init(int width, int height) {
        bind();

        attribute("position", 2);
        attribute("uv", 2);
        attribute("color", 3);
        uniform("model", new Matrix4f());
        uniform("view", new Matrix4f());
        updateProjection(width, height);
        return this;
    }

    public void updateProjection(int width, int height) {
        uniform("projection", new Matrix4f().ortho(0, width, height, 0, -1.0f, 1.0f));
    }

    public int getAttributeLocation(String attribute) {
        bind();
        return attributes.computeIfAbsent(attribute, (name) -> GL30.glGetAttribLocation(program, name));
    }

    public void attribute(String name, int size) {
        int location = getAttributeLocation(name);
        GL30.glEnableVertexAttribArray(location);
        GL30.glVertexAttribPointer(location, size, GL30.GL_FLOAT, false, getVertexSize() << 2, attribOffset);
        attribOffset += size << 2;
    }

    private int getUniformLocation(String uniform) {
        bind();
        return uniforms.computeIfAbsent(uniform, (name) -> GL30.glGetUniformLocation(program, name));
    }

    public void uniform(String name, Matrix4f value) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }

    public void bind() {
        GL30.glUseProgram(program);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    private int compileShader(ResourceName location, int type) {
        var source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(location.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line);
                source.append("\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to read shader from location " + location + "! Printing stack trace...");
            e.printStackTrace();
            return -1;
        }

        var id = GL30.glCreateShader(type);
        GL30.glShaderSource(id, source.toString());
        GL30.glCompileShader(id);

        try (var stack = MemoryStack.stackPush()) {
            var success = stack.mallocInt(1);
            GL30.glGetShaderiv(id, GL30.GL_COMPILE_STATUS, success);
            if (success.get(0) == GL30.GL_FALSE) {
                var logLength = stack.mallocInt(1);
                GL30.glGetShaderiv(id, GL30.GL_INFO_LOG_LENGTH, logLength);
                System.err.println("Failed to compile shader from location " + location + "! Printing info log... \n" + GL30.glGetShaderInfoLog(id));
                GL30.glDeleteShader(id);
                return -1;
            }
        }
        return id;
    }

    public int getVertexSize() {
        return 7;
    }
}
