#version 150
// based on https://math.stackexchange.com/a/984851

vec2 unit_corner(vec2 a, vec2 b, vec2 c) {
    vec2 ab = b - a;
    vec2 abNormal = normalize(vec2(ab.y, -ab.x));
    vec2 bc = c - b;
    vec2 bcNormal = normalize(vec2(bc.y, -bc.x));
    if(abNormal == vec2(0)) {
        return bcNormal;
    }
    if(bcNormal == vec2(0)) {
        return abNormal;
    }

    float u = dot(abNormal * abNormal, vec2(1)) / dot(abNormal.xy * bcNormal.yx * vec2(1, -1), vec2(1));
    return u * vec2(bcNormal.y - abNormal.y, abNormal.x - bcNormal.x);
}

vec4 get_corner(vec2 displaySize, vec4 a, vec4 b, vec4 c, float offset) {
    // no idea why the * 2 is necessary here
    return vec4(b.xy + unit_corner(a.xy, b.xy, c.xy) / displaySize * offset * 2., b.zw);
}