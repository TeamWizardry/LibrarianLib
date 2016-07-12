package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 12/7/2016.
 */
public class RagdollLine {

    public ArrayList<PointMass2D> masses;
    public PointMass2D origin;
    public List<Link2D> links = new ArrayList<>();
    public int solvePasses = 3;
    public ArrayList<Vec2> points;
    public float stretch = 1, shear = 1, flex = 0.8f;

    public RagdollLine(ArrayList<Vec2> points) {
        this.points = points;
        init();
    }

    public void init() {
        masses = new ArrayList<>();
        links = new ArrayList<>();

        this.origin = new PointMass2D(points.get(0), 10f);

        for (int i = 0; i < points.size(); i++) {
            // Convert points to masses
            // Any point with respect to origin. Will create a relative system to point 0
            masses.add(new PointMass2D(points.get(i).sub(points.get(0)), 10f));

            // Set the two pins
            if (i == 0 || i == points.size())
                masses.get(i).pin = true;
        }
    }

    public void tick() {
        Vec2 gravity = new Vec2(0, -10);

        for (PointMass2D point : masses) {
            if (point.pin) continue;

            point.updatePhysics();
        }

        // Solve them all
        for (int i = 0; i < solvePasses; i++) links.forEach(Link2D::resolve);
    }
}
