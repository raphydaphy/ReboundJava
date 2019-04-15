#version 330 core
in vec3 passColor;
in vec2 passUV;

out vec3 glColor;

uniform sampler2D textureSampler;

void main() {
    vec4 textureSample = texture(textureSampler, passUV);
    if (textureSample.a < 0.001) {
        discard;
    }
    glColor = passColor * textureSample.rgb;
}