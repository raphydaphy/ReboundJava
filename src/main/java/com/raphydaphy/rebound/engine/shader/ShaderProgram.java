package com.raphydaphy.rebound.engine.shader;

import com.raphydaphy.rebound.util.ResourceLocation;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShaderProgram {
    private final int program;
    private final int vertexShader;
    private final int fragmentShader;

    public ShaderProgram(ResourceLocation source) {
        vertexShader = compileShader(new ResourceLocation(source.getNamespace(), source.getResource() + ".vert"), GL30.GL_VERTEX_SHADER);
        fragmentShader = compileShader(new ResourceLocation(source.getNamespace(), source.getResource() + ".frag"), GL30.GL_FRAGMENT_SHADER);
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

    public void bind() {
        GL30.glUseProgram(program);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    private int compileShader(ResourceLocation location, int type) {
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
        return 5;
    }
}
