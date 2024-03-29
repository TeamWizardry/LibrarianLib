#version 150

in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a < 1./255.) {
        discard;
    }
    fragColor = color;
}
