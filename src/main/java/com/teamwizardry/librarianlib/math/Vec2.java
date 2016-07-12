package com.teamwizardry.librarianlib.math;

public class Vec2 {

    public final float xf, yf;
    public final int xi, yi;
    public double x, y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
        this.xf = (float) x;
        this.yf = (float) y;
        this.xi = (int) Math.floor(x);
        this.yi = (int) Math.floor(y);
    }

    public Vec2 floor() {
        return new Vec2(Math.floor(x), Math.floor(y));
    }

    public Vec2 ceil() {
        return new Vec2(Math.ceil(x), Math.ceil(y));
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    public Vec2 add(double otherX, double otherY) {
        return new Vec2(x + otherX, y + otherY);
    }

    public Vec2 sub(Vec2 other) {
        return new Vec2(x - other.x, y - other.y);
    }

    public Vec2 sub(double otherX, double otherY) {
        return new Vec2(x - otherX, y - otherY);
    }

    public Vec2 mul(Vec2 other) {
        return new Vec2(x * other.x, y * other.y);
    }

    public Vec2 mul(double otherX, double otherY) {
        return new Vec2(x * otherX, y * otherY);
    }

    public Vec2 mul(double amount) {
        return new Vec2(x * amount, y * amount);
    }

    public double dot(Vec2 point) {
        return (x * point.x) + (y * point.y);
    }

    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }

    public Vec2 normalize() {
        double norm = length();
        return new Vec2(x / norm, y / norm);
    }

    public double squareDist(Vec2 vec) {
        double d0 = vec.x - x;
        double d1 = vec.y - y;
        return d0 * d0 + d1 * d1;
    }
}
