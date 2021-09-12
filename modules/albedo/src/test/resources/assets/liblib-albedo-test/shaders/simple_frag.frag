#version 150

void main() {
    gl_FragColor = vec4(gl_FragCoord.xy / 500, 0., 1.);
}
