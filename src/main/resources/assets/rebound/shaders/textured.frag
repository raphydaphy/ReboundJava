#version 330 core
in vec2 UV;
out vec3 color;

uniform sampler2D textureSampler;

void main() {
    vec4 textureSample = texture(textureSampler, UV);
    if (textureSample.a < 0.1) {
        discard;
    }
    color = textureSample.rgb;
}