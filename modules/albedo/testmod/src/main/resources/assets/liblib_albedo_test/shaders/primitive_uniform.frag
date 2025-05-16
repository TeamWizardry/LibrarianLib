#version 150

uniform float time;

void main() {
    gl_FragColor = vec4(mod(time, 1.0), mod(time + 0.333, 1.0), mod(time + 0.666, 1.0), 1.0);
}
