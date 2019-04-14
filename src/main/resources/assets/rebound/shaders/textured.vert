#version 330 core
in vec2 position;
in vec2 uv;

out vec2 passUV;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    passUV = uv;

    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1, 1);
}