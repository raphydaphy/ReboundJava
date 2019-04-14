#version 330 core
in vec2 passUV;

out vec3 color;

uniform sampler2D textureSampler;

void main() {
    vec4 textureSample = texture(textureSampler, passUV);
    if (textureSample.a < 0.001) {
        discard;
    }
    color = textureSample.rgb;
}