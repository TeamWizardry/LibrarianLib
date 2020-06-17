#version 120

uniform int index;
uniform int[2] primitive;
int[2] primitive_expected = int[2](10, 20);
uniform ivec2[2] vector2;
ivec2[2] vector2_expected = ivec2[2](ivec2(10, 20), ivec2(30, 40));
uniform ivec3[2] vector3;
ivec3[2] vector3_expected = ivec3[2](ivec3(10, 20, 30), ivec3(40, 50, 60));
uniform ivec4[2] vector4;
ivec4[2] vector4_expected = ivec4[2](ivec4(10, 20, 30, 40), ivec4(50, 60, 70, 80));

#pragma import <gridutil.glsl>

ivec2 dimensions = ivec2(4, 4);
void main() {
    if(isBorder(dimensions, 32)) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 1, 1)) {
        TEST_BOOL(primitive[index] == primitive_expected[index]);
    } else if(AREA(0, 1, 2, 1)) {
        TEST_VECTOR(vector2[index], vector2_expected[index]);
    } else if(AREA(0, 2, 3, 1)) {
        TEST_VECTOR(vector3[index], vector3_expected[index]);
    } else if(AREA(0, 3, 4, 1)) {
        TEST_VECTOR(vector4[index], vector4_expected[index]);
    }

    TEST_NA;
}
