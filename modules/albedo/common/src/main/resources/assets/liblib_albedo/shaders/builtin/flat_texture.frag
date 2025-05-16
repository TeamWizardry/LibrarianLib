#version 150

uniform sampler2D Texture;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(Texture, texCoord);
    color *= vertexColor;
    if (color.a < 1./255.) {
        discard;
    }
    fragColor = color;
}
