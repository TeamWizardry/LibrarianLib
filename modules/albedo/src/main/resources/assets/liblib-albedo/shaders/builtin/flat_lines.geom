#version 150
layout (lines_adjacency) in;
layout (triangle_strip, max_vertices = 8) out;

#include "liblib-albedo:include/lines.glsl"
#include "liblib-albedo:include/pixels.glsl"

float bevelAngle = 45. / 180. * 3.14159;
float bevelLimit = length(corner_offset(vec2(-1, 0), vec2(cos(bevelAngle), sin(bevelAngle))));

in GeometryData
{
    vec4 color;
    float insetWidth;
    float outsetWidth;
} gs_in[];

out FragmentData
{
    vec4 color;
} gs_out;

void emit(vec4 pos, vec4 color) {
    gl_Position = pos;
    gs_out.color = color;
    EmitVertex();
}

// returns t, intersection point is `b + db * t`
float intersect(vec2 a, vec2 da, vec2 b, vec2 db) {
    vec2 norm = vec2(-da.y, da.x);
    if(dot(db, norm) == 0.) {
        return 1.;
    }
    return dot((a - b), norm) / dot(db, norm);
}

void main() {
    vec4 screenScale = vec4(DisplaySize, 1, 1);

    vec4 pos0 = gl_in[0].gl_Position;
    vec4 pos1 = gl_in[1].gl_Position;
    vec4 pos2 = gl_in[2].gl_Position;
    vec4 pos3 = gl_in[3].gl_Position;
    pos0 /= pos0.w;
    pos1 /= pos1.w;
    pos2 /= pos2.w;
    pos3 /= pos3.w;

    vec2 px0 = to_pixels(pos0.xy);
    vec2 px1 = to_pixels(pos1.xy);
    vec2 px2 = to_pixels(pos2.xy);
    vec2 px3 = to_pixels(pos3.xy);

    //                             |    |     |
    //         x-------------------x    |     |
    //        / 2                4  \   |     |
    //       /                       \  |     |
    //      x 1                       \ |     *
    //     / \                         \|    /
    //    /   #-  -  -  -  -  -  -  -  -#   /
    //   /    |\                         \ /
    //  *     | \                       6 x
    //  |     |  \                       /
    //  |     |   \ 3                 5 /
    //  |     |    x-------------------x
    //  |     |    |
    //  |     |    |


    vec2 center = (px1 + px2) / 2;
    vec2 direction = normalize(px2 - px1);
    vec2 normal = vec2(direction.y, -direction.x);

    {
        vec2 inset = normal * -gs_in[1].insetWidth;
        vec2 outset = normal * gs_in[1].outsetWidth;

        vec2 corner = normalize(px1 - px0).yx * vec2(1, -1) + normal;
        if (corner == vec2(0, 0)) {
            corner = normal;
        } else {
            corner = normalize(corner);
            inset = corner * intersect(px1 + inset, direction, px1, corner);
            outset = corner * intersect(px1 + outset, direction, px1, corner);
        }

        emit(vec4(from_pixels(px1 + inset), pos1.z, 1.), gs_in[1].color);
        emit(vec4(from_pixels(px1 + outset), pos1.z, 1.), gs_in[1].color);
    }

    {
        vec2 inset = normal * -gs_in[2].insetWidth;
        vec2 outset = normal * gs_in[2].outsetWidth;

        vec2 corner = normalize(px3 - px2).yx * vec2(1, -1) + normal;
        if (corner == vec2(0, 0)) {
            corner = normal;
        } else {
            corner = normalize(corner);
            inset = corner * intersect(px2 + inset, direction, px2, corner);
            outset = corner * intersect(px2 + outset, direction, px2, corner);
        }

        emit(vec4(from_pixels(px2 + inset), pos2.z, 1.), gs_in[2].color);
        emit(vec4(from_pixels(px2 + outset), pos2.z, 1.), gs_in[2].color);
    }

    EndPrimitive();
}
