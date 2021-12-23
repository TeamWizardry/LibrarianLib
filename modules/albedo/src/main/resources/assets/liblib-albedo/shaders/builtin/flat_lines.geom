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

// https://stackoverflow.com/a/2932601/1541907
// params: A start, A direction, B start, B direction
// returns: vec2(tA, tB)
vec2 intersect2(vec2 as, vec2 ad, vec2 bs, vec2 bd) {
    vec2 dd = bs - as;
//    float det = dot(vec2(bd.x, -bd.y), ad.yx);
    float det = bd.x * ad.y - bd.y * ad.x;
    return vec2(
    (dd.y * bd.x - dd.x * bd.y) / det,
    (dd.y * ad.x - dd.x * ad.y) / det
    );
}
// params: A start, A direction, B start, B direction
// returns: tA
float intersect1(vec2 as, vec2 ad, vec2 bs, vec2 bd) {
    vec2 dd = bs - as;
    float det = bd.x * ad.y - bd.y * ad.x;
    return (dd.y * bd.x - dd.x * bd.y) / det;
}

// the perpendicular vector 90 degrees clockwise
vec2 cw(vec2 v) {
    return vec2(v.y, -v.x);
}
// the perpendicular vector 90 degrees counter-clockwise
vec2 ccw(vec2 v) {
    return vec2(-v.y, v.x);
}

/**
 * all coordinates have the vertex at 0,0
 *
 *    x
 *     \___._________,
 *    / \  |
 *   /   \ |
 *  /     \|
 * /       *---------
 *        /
 *       /
 *
 * [corner] is the normalized vector from '*' to 'x'
 * [offset] is the offset distance from '*' to '.' (may be negative)
 * [edge] is the vector from '*' to '.'
 * [direction] is the vector from ',' to '.' (normalization not needed)
 * [length] is the length of the line segment
 */
vec4 bevel_test(vec2 corner, float offset, vec2 edge, vec2 direction, float length) {
    float bevelDistance = offset * 1.5;
    vec2 cornerIntersections = intersect2(edge, direction, vec2(0), corner);
    cornerIntersections.x = max(-length, cornerIntersections.x);

    // dot is positive if the angle is > 90 degrees - https://stackoverflow.com/a/49535408/1541907
    // if the offset is negative and the vectors are the same direction, we're an inner corner, so no bevel
    // if the offset is positive and the vectors are opposite directions, we're an inner corner, so no bevel
    if(sign(offset) != sign(dot(corner, direction))) {
        vec2 point = edge + direction * cornerIntersections.x;
        return vec4(point, point);
    }

    vec2 bevelDirection = cw(corner);
    float bevelIntersection = intersect1(edge, direction, corner * bevelDistance, bevelDirection);

    vec2 intersectionPoint = edge + direction * max(0.0, min(bevelIntersection, cornerIntersections.x));
    return vec4(
        intersectionPoint,
        abs(bevelDistance) < abs(cornerIntersections.y) ? corner * bevelDistance : intersectionPoint
    );
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
    float length = length(px2 - px1);
    vec2 direction = (px2 - px1) / length;
    vec2 normal = cw(direction);

    {
        vec2 inset = normal * gs_in[1].insetWidth;
        vec2 outset = normal * gs_in[1].outsetWidth;

        vec2 corner = normalize(cw(px1 - px0)) + normal;

        if (corner == vec2(0, 0)) {
            emit(vec4(from_pixels(px1 + inset), pos1.z, 1.), vec4(1, 0, 1, 1));
            emit(vec4(from_pixels(px1 + outset), pos1.z, 1.), vec4(0, 1, 1, 1));
        } else {
            corner = normalize(corner);

            vec4 outsetBevel = bevel_test(corner, gs_in[1].outsetWidth, outset, -direction, length);
            vec4 insetBevel = bevel_test(corner, gs_in[1].insetWidth, inset, -direction, length);

            emit(vec4(from_pixels(px1 + insetBevel.zw), pos1.z, 1.), vec4(1, 0, 0, 1));
            emit(vec4(from_pixels(px1 + outsetBevel.zw), pos1.z, 1.), vec4(0, 1, 0, 1));
            emit(vec4(from_pixels(px1 + insetBevel.xy), pos1.z, 1.), vec4(1, 0, 1, 1));
            emit(vec4(from_pixels(px1 + outsetBevel.xy), pos1.z, 1.), vec4(0, 1, 1, 1));
//            float outsetBevelHit = intersect(outset, -direction, outsetBevelPoint, bevelPlane);
//            if(outsetBevelHit > 0) {
//                emit(vec4(from_pixels(px1), pos1.z, 1.), vec4(1, 0, 0, 1));
//                emit(vec4(from_pixels(px1 + outsetBevelPoint), pos1.z, 1.), vec4(0, 1, 0, 1));
//                outset = outsetBevelPoint + bevelPlane * outsetBevelHit;
//            } else {
//                outset = corner * intersect(outset, -direction, vec2(0), corner);
//            }
        }

        // gs_in[1].color
    }

    {
        vec2 inset = normal * gs_in[2].insetWidth;
        vec2 outset = normal * gs_in[2].outsetWidth;

        vec2 corner = normalize(px3 - px2).yx * vec2(1, -1) + normal;
        if (corner == vec2(0, 0)) {
            corner = normal;
        } else {
            corner = normalize(corner);
            inset = corner * intersect(px2 + inset, direction, px2, corner);
            outset = corner * intersect(px2 + outset, direction, px2, corner);
        }

        emit(vec4(from_pixels(px2 + inset), pos2.z, 1.), vec4(0, 0, 0, 1));
        emit(vec4(from_pixels(px2 + outset), pos2.z, 1.), vec4(0, 0, 0, 1));
//        emit(vec4(from_pixels(px2 + outset), pos2.z, 1.), gs_in[2].color);
    }

    EndPrimitive();
}
