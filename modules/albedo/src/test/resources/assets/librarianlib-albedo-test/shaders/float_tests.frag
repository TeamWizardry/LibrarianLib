#version 120

uniform float primitive;
uniform vec2 vector2;
uniform vec3 vector3;
uniform vec4 vector4;

vec4 lines = vec4(0.0, 0.0, 0.0, 1.0);
vec4 success = vec4(0.0, 1.0, 0.0, 1.0);
vec4 failure = vec4(1.0, 0.0, 0.0, 1.0);

vec4 none = vec4(-1.0, -1.0, -1.0, -1.0);

vec4 test(int count, float tex, vec4 expected, vec4 actual) {
    float component = tex * float(count);
    int index = int(component);
    if(component > float(count)/32.0 && mod(component, 1.0) < float(count)/32.0) {
        return lines;
    } else if(vector4[index] == expected[index]) {
        return success;
    } else {
        return failure;
    }
}

void main() {
    vec4 tex = gl_TexCoord[0];
    if(mod(tex.y, 1.0) < 1.0/8.0) {
        gl_FragColor = lines;
    } else if(tex.y <= 1.0) {
        gl_FragColor = test(1, tex.x, vec4(10.0, none.yzw), vec4(primitive, none.yzw));
    } else if(tex.y <= 2.0) {
        gl_FragColor = test(2, tex.x, vec4(10.0, 20.0, none.zw), vec4(vector2, none.zw));
    } else if(tex.y <= 3.0) {
        gl_FragColor = test(3, tex.x, vec4(10.0, 20.0, 30.0, none.w), vec4(vector3, none.w));
    } else if(tex.y <= 4.0) {
        gl_FragColor = test(4, tex.x, vec4(10.0, 20.0, 30.0, 40.0), vector4);
    } else if(tex.y > 4.0) {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    } else {
        gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);
    }
}
