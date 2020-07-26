#version 120

uniform float primitive;
float primitive_expected = 10;
uniform vec2 vector2;
vec2 vector2_expected = vec2(10, 20);
uniform vec3 vector3;
vec3 vector3_expected = vec3(10, 20, 30);
uniform vec4 vector4;
vec4 vector4_expected = vec4(10, 20, 30, 40);

#pragma import <gridutil.glsl>

ivec2 dimensions = ivec2(4, 4);
void main() {
    if(isBorder(dimensions, 32)) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 1, 1)) {
        TEST_BOOL(primitive == primitive_expected);
    } else if(AREA(0, 1, 2, 1)) {
        TEST_VECTOR(vector2, vector2_expected);
    } else if(AREA(0, 2, 3, 1)) {
        TEST_VECTOR(vector3, vector3_expected);
    } else if(AREA(0, 3, 4, 1)) {
        TEST_VECTOR(vector4, vector4_expected);
    }

    TEST_NA;
}
