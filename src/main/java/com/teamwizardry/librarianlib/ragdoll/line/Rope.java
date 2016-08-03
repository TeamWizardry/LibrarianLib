package com.teamwizardry.librarianlib.ragdoll.line;

import java.util.ArrayList;
import java.util.List;

import com.teamwizardry.librarianlib.math.Vec2d;

public class Rope {

	public PointMass2D start, end;
	public PointMass2D[] points;
	public List<Link2D> stretchLinks;
	public List<Link2D> bendLinks;
	public int solvePasses = 3;
	public int middles;
	
	public Rope(int middles, double length, float stretch, float bend, Vec2d begin, Vec2d end) {
		points = new PointMass2D[middles+2];
		this.middles = middles;
		float mass = 0.1f;
		Vec2d offsetPer = end.sub(begin).mul(1.0/(middles+1));
		Vec2d currentPos = begin;
		for (int i = 0; i < points.length; i++) {
			points[i] = new PointMass2D(currentPos, mass);
			currentPos = currentPos.add(offsetPer);
		}
		
		this.start = points[0];
		this.end = points[points.length-1];
		
		stretchLinks = new ArrayList<>();
		bendLinks = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			if(i+1 < points.length) {
				Link2D link = new Link2D(points[i], points[i+1], 1.0f-stretch);
//				link.pinA = i < (points.length/2-1);
//				link.pinB = i > points.length/2;;
				stretchLinks.add(link);
			}
			
			if(i+2 < points.length)
				bendLinks.add(new Link2D(points[i], points[i+2], 1.0f-bend));
		}
	}
	
	public void tick() {
		Vec2d gravity = new Vec2d(0, 1);

        for (PointMass2D point : points) {
            if(point.pin)
				continue;
			point.pos = point.pos.add(gravity).add(point.pos.sub(point.prevPos));
		}
		
		for (int i = 0; i < solvePasses; i++) {
			for (Link2D link : stretchLinks) {
				link.resolve();
			}
			for (Link2D link : bendLinks) {
				link.resolve();
			}
		}

        for (PointMass2D point : points) {
			point.prevPos = point.pos;
		}
	}
	
}
