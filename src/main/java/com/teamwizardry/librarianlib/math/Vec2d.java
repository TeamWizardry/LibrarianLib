package com.teamwizardry.librarianlib.math;

public class Vec2d {
	
	public static final Vec2d ZERO = new Vec2d(0,0);
	
	public static final Vec2d ONE = new Vec2d(1,1);
	public static final Vec2d X = new Vec2d(1,0);
	public static final Vec2d Y = new Vec2d(0,1);
	
	public static final Vec2d NEG_ONE = new Vec2d(-1,-1);
	public static final Vec2d NEG_X = new Vec2d(-1,0);
	public static final Vec2d NEG_Y = new Vec2d(0,-1);
	
    public final float xf, yf;
    public final int xi, yi;
    public double x, y;

    //=============================================================================
	{/* Simple math */}

    //=============================================================================
	{/* Advanced math */}

    //=============================================================================
    {/* Static */}
    
    //=============================================================================
	{/* Boring object stuff */}
    
    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
        this.xf = (float) x;
        this.yf = (float) y;
        this.xi = (int) Math.floor(x);
        this.yi = (int) Math.floor(y);
    }

    public static Vec2d min(Vec2d a, Vec2d b) {
    	return new Vec2d(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }
	//=============================================================================
    
    public static Vec2d max(Vec2d a, Vec2d b) {
    	return new Vec2d(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }

    public Vec2d floor() {
        return new Vec2d(Math.floor(x), Math.floor(y));
    }

    public Vec2d ceil() {
        return new Vec2d(Math.ceil(x), Math.ceil(y));
    }

    public Vec2d setX(double value) {
    	return new Vec2d(value, y);
    }

    public Vec2d setY(double value) {
    	return new Vec2d(x, value);
    }

    public Vec2d add(Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }

    public Vec2d add(double otherX, double otherY) {
        return new Vec2d(x + otherX, y + otherY);
    }
    
    public Vec2d sub(Vec2d other) {
        return new Vec2d(x - other.x, y - other.y);
    }
	//=============================================================================

    public Vec2d sub(double otherX, double otherY) {
        return new Vec2d(x - otherX, y - otherY);
    }

    public Vec2d mul(Vec2d other) {
        return new Vec2d(x * other.x, y * other.y);
    }

    public Vec2d mul(double otherX, double otherY) {
        return new Vec2d(x * otherX, y * otherY);
    }

    public Vec2d mul(double amount) {
        return new Vec2d(x * amount, y * amount);
    }
    
    public double dot(Vec2d point) {
        return (x * point.x) + (y * point.y);
    }
    
    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }
    //=============================================================================
    
    public Vec2d normalize() {
        double norm = length();
        return new Vec2d(x / norm, y / norm);
    }
    
    public double squareDist(Vec2d vec) {
        double d0 = vec.x - x;
        double d1 = vec.y - y;
        return d0 * d0 + d1 * d1;
    }
    
    public Vec2d projectOnTo(Vec2d other) {
    	other = other.normalize();
    	return other.mul(this.dot(other));
    }
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
		Vec2d other = (Vec2d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		return Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
