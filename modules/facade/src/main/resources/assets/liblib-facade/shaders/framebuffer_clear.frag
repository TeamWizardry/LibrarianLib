#version 120

out fragColor;

void main() {
    fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    gl_FragDepth = 1;
}
