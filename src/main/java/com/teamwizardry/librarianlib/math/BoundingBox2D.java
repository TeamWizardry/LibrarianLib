package com.teamwizardry.librarianlib.math;

public class BoundingBox2D {
	public final Vec2 min, max;
	
	public BoundingBox2D(Vec2 min, Vec2 max) {
		this.min = min;
		this.max = max;
	}
	
	public BoundingBox2D(double minX, double minY, double maxX, double maxY) {
		this(new Vec2(minX, minY), new Vec2(maxX, maxY));
	}

	public BoundingBox2D union(BoundingBox2D other) {
		return new BoundingBox2D(
			Math.min(min.x, other.min.x),
			Math.min(min.y, other.min.y),
			Math.max(max.x, other.max.x),
			Math.max(max.y, other.max.y)
		);
	}
	
	public double height() {
		return max.y - min.y;
	}
	
	public double width() {
		return max.x - min.x;
	}
	
	public float heightF() {
		return max.yf - min.yf;
	}
	
	public float widthF() {
		return max.xf - min.xf;
	}
	
	public int heightI() {
		return max.yi - min.yi;
	}
	
	public int widthI() {
		return max.xi - min.xi;
	}
}
