package com.teamwizardry.librarianlib.math;

public class Vec2 {
	
	public static final Vec2 ZERO = new Vec2(0,0);
	public static final Vec2 ONE = new Vec2(1,1);
	public static final Vec2 X = new Vec2(1,0);
	public static final Vec2 Y = new Vec2(0,1);

	
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
    
    public Vec2 setX(double value) {
    	return new Vec2(value, y);
    }
    
    public Vec2 setY(double value) {
    	return new Vec2(x, value);
    }

    //=============================================================================
	{/* Simple math */}
	//=============================================================================
    
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
    
    //=============================================================================
	{/* Advanced math */}
	//=============================================================================

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
    
    public Vec2 projectOnTo(Vec2 other) {
    	other = other.normalize();
    	return other.mul(this.dot(other));
    }
    
    //=============================================================================
    {/* Static */}
    //=============================================================================
    
    public static Vec2 min(Vec2 a, Vec2 b) {
    	return new Vec2(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }
    
    public static Vec2 max(Vec2 a, Vec2 b) {
    	return new Vec2(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }
    
    //=============================================================================
	{/* Boring object stuff */}
	//=============================================================================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2 other = (Vec2) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
