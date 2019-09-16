#version 120

uniform sampler2D image;
uniform sampler1D kernel;

uniform vec2 displaySize;
uniform int sigma;
uniform bool horizontal;

void main() {
    vec2 axis;
    if(horizontal) {
        axis = vec2(1.0, 0.0);
    } else {
        axis = vec2(0.0, 1.0);
    }

    vec4 sum = vec4(0.0);
    int radius = 2 * sigma + 2;
//    float totalWeight = 0.0;

    for(int i = 0; i < radius; i++) {
        vec2 pos; vec4 color;
        float weight = texture1D(kernel, i / float(radius)).r * (64.0 / sigma);
//        totalWeight += weight;

        pos = gl_FragCoord.xy - axis * i;
        color = texture2D(image, pos / displaySize);
        color.rgb *= color.a;
        sum += color * weight;

        if(i != 0) {
            pos = gl_FragCoord.xy + axis * i;
            color = texture2D(image, pos / displaySize);
            color.rgb *= color.a;
            sum += color * weight;

//            totalWeight += weight;
        }
    }
    if(sum.a != 0.0) {
        sum.rgb /= sum.a;
//        sum.a /= totalWeight;
    }
    gl_FragColor = sum;
}
