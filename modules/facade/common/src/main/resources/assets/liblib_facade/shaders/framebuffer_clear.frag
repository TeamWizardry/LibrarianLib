#version 150

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    gl_FragDepth = 1;
}
