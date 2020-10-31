#version 120

uniform int primitive;
int primitive_expected = 10;
uniform ivec2 vector2;
ivec2 vector2_expected = ivec2(10, 20);
uniform ivec3 vector3;
ivec3 vector3_expected = ivec3(10, 20, 30);
uniform ivec4 vector4;
ivec4 vector4_expected = ivec4(10, 20, 30, 40);

#pragma include <gridutil.glsl>

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
