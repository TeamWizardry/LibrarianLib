#version 120

uniform sampler2D layerImage;
uniform sampler2D maskImage;
uniform vec2 displaySize;
uniform float alphaMultiply;
uniform int maskMode;
uniform int renderMode;

// luma function courtesy of https://github.com/hughsk/glsl-luma
float luma(vec4 color) {
    return dot(color.rgb, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec2 uv = gl_FragCoord.xy / displaySize;
    if(renderMode == 2) {
        uv = gl_TexCoord[0].xy;
    }
    vec4 layerColor = texture2D(layerImage, uv);
    if(maskMode != 0) {
        vec4 maskColor = texture2D(maskImage, uv);
        if(maskMode == 1) { // Multiply by alpha
            layerColor.a *= maskColor.a;
        } else if(maskMode == 2) { // Multiply by luma on white
            layerColor.a *= (1. - maskColor.a) + luma(maskColor) * maskColor.a;
        } else if(maskMode == 3) { // Multiply by luma on black
            layerColor.a *= luma(maskColor) * maskColor.a;
        } else if(maskMode == 4) { // Multiply by inverse alpha
            layerColor.a *= 1. - maskColor.a;
        } else if(maskMode == 5) { // Multiply by inverse luma on white
            layerColor.a *= 1. - ((1. - maskColor.a) + luma(maskColor) * maskColor.a);
        } else if(maskMode == 6) { // Multiply by inverse luma on black
            layerColor.a *= 1. - (luma(maskColor) * maskColor.a);
        }
    }
    layerColor.a *= alphaMultiply;
    gl_FragColor = layerColor;
}
