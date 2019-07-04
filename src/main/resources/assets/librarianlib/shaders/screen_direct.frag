#version 120

uniform vec2 displaySize;
uniform sampler2D bgl_RenderedTexture;

void main() {
    vec4 color = texture2D(bgl_RenderedTexture, gl_FragCoord.xy / displaySize);
    gl_FragColor = color * gl_Color;
}
