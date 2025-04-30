#version 150
// based on https://math.stackexchange.com/a/984851

vec2 corner_offset(vec2 a, vec2 b) {
    float u = (a.x * a.x + a.y * a.y) / (a.x * b.y - a.y * b.x);
    return vec2(b.y - a.y, a.x - b.x) * u;
}

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

    return corner_offset(abNormal, bcNormal);
}
