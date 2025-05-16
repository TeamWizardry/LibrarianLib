#version 150

uniform sampler2D sampler1;
uniform sampler2D sampler2;

void main() {
    if(gl_TexCoord[0].y < 1) {
        gl_FragColor = texture2D(sampler1, gl_TexCoord[0].xy);
//        gl_FragColor = vec4(gl_TexCoord[0].xy, 0, 1);
    } else {
        gl_FragColor = texture2D(sampler2, gl_TexCoord[0].xy - vec2(0, 1));
//        gl_FragColor = vec4(gl_TexCoord[0].xy - vec2(0, 1), 0, 1);
    }
}
