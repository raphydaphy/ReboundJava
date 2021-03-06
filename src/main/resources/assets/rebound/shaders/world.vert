#version 330 core
in vec2 position;
in vec2 uv;
in vec3 color;

out vec2 passUV;
out vec3 passColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    passUV = uv;
    passColor = color;

    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1, 1);
}