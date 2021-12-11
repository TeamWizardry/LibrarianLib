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

    float ax = abNormal.x;
    float ay = abNormal.y;
    float bx = bcNormal.x;
    float by = bcNormal.y;

    float u = (ax * ax + ay * ay) / (ax * by - ay * bx);
    return vec2(by - ay, ax - bx) * u;
}

vec4 pixel_corner(vec2 displaySize, vec4 a, vec4 b, vec4 c) {
    // we need the / 2 because screen coordinates are from -1 to +1
    vec2 corner = unit_corner(a.xy / a.w * displaySize, b.xy / b.w * displaySize, c.xy / c.w * displaySize) / (displaySize / 2.);
    return vec4(corner * b.w, 0, 0);
}