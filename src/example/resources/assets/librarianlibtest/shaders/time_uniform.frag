#version 120
#define PI 3.141592

uniform float time;
uniform sampler2D tex;

void main() {
    vec2 offset = vec2(sin(mod(time, PI*2)), cos(mod(time, PI*2))) * 0.25;
    vec2 texcoord = vec2(gl_TexCoord[0]) + offset;

    vec4 color = texture2D(tex, texcoord);

    float r = color.r * gl_Color.r;
    float g = color.g * gl_Color.g;
    float b = color.b * gl_Color.b;
    float a = color.a * gl_Color.a;

    gl_FragColor = vec4(r, g, b, a);
}
