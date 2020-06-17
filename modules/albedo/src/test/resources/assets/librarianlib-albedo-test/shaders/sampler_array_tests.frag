#version 120

uniform int index;
uniform sampler2D[2] sampler1;
uniform sampler2D[2] sampler2;

void main() {
    if(index == 0) {
        if(gl_TexCoord[0].y < 1) {
            gl_FragColor = texture2D(sampler1[0], gl_TexCoord[0].xy);
        } else {
            gl_FragColor = texture2D(sampler2[0], gl_TexCoord[0].xy - vec2(0, 1));
        }
    } else {
        if(gl_TexCoord[0].y < 1) {
            gl_FragColor = texture2D(sampler1[1], gl_TexCoord[0].xy);
        } else {
            gl_FragColor = texture2D(sampler2[1], gl_TexCoord[0].xy - vec2(0, 1));
        }
    }
}
