#version 150

uniform sampler2D LayerImage;
uniform sampler2D MaskImage;
uniform vec2 DisplaySize;
uniform float AlphaMultiply;
uniform int MaskMode;
uniform int RenderMode;

in vec2 texelCoord;

out vec4 fragColor;

// luma function courtesy of https://github.com/hughsk/glsl-luma
float luma(vec4 color) {
    return dot(color.rgb, vec3(0.299, 0.587, 0.114));
}

void main() {
    ivec2 texel = ivec2(gl_FragCoord.xy);
    if (RenderMode == 2) {
        texel = ivec2(texelCoord);
    }
    vec4 layerColor = texelFetch(LayerImage, texel, 0);
    if (MaskMode != 0) {
        vec4 maskColor = texelFetch(MaskImage, texel, 0);
        if (MaskMode == 1) { // Multiply by alpha
            layerColor.a *= maskColor.a;
        } else if (MaskMode == 2) { // Multiply by luma on white
            layerColor.a *= (1. - maskColor.a) + luma(maskColor) * maskColor.a;
        } else if (MaskMode == 3) { // Multiply by luma on black
            layerColor.a *= luma(maskColor) * maskColor.a;
        } else if (MaskMode == 4) { // Multiply by inverse alpha
            layerColor.a *= 1. - maskColor.a;
        } else if (MaskMode == 5) { // Multiply by inverse luma on white
            layerColor.a *= 1. - ((1. - maskColor.a) + luma(maskColor) * maskColor.a);
        } else if (MaskMode == 6) { // Multiply by inverse luma on black
            layerColor.a *= 1. - (luma(maskColor) * maskColor.a);
        }
    }
    layerColor.a *= AlphaMultiply;
    fragColor = layerColor;
}
