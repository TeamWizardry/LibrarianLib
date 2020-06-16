#version 120

uniform float primitive;
uniform vec2 vector2;
uniform vec3 vector3;
uniform vec4 vector4;

#pragma import <gridutil.glsl>

ivec2 dimensions = ivec2(4, 4);
void main() {
    if(isBorder(dimensions, 32)) {
        TEST_BORDER;
    }
    ivec2 cell = cell(dimensions);
    if(cell.y == 0 && cell.x < 1) {
        TEST_BOOL(primitive == 10.0);
    } else if(cell.y == 1 && cell.x < 2) {
        TEST_BOOL(vector2[cell.x] == vec2(10, 20)[cell.x]);
    } else if(cell.y == 2 && cell.x < 3) {
        TEST_BOOL(vector3[cell.x] == vec3(10, 20, 30)[cell.x]);
    } else if(cell.y == 3 && cell.x < 4) {
        TEST_BOOL(vector4[cell.x] == vec4(10, 20, 30, 40)[cell.x]);
    } else {
        TEST_NA;
    }
}
