package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 12/7/2016.
 */
public class RagdollLine {

    public PointMass2D[][] masses;
    public List<Link2D> links = new ArrayList<>();
    public List<Link2D> hardLinks = new ArrayList<>();
    public int solvePasses = 3;
    public Vec2[] top;
    public int height;
    public Vec2 size;
    public float stretch = 1, shear = 1, flex = 0.8f;

    public RagdollLine(Vec2[] top, int height, Vec2 size) {
        this.top = top;
        this.height = height;
        this.size = size;

        init();
    }

    public void init() {
        masses = new PointMass2D[height][top.length];
        links = new ArrayList<>();

        for (int i = 0; i < height; i++) {

            for (int j = 0; j < top.length; j++) {
                masses[i][j] = new PointMass2D(top[j].add(size.mul(i)), 0.1f);
                if (i == 0)
                    masses[i][j].pin = true;
            }

        }

        for (int x = 0; x < masses.length; x++) {
            for (int z = 0; z < masses[x].length; z++) {

                if (x + 1 < masses.length)
                    hardLinks.add(new HardLink2D(masses[x][z], masses[x + 1][z], 1));

                if (x + 1 < masses.length)
                    links.add(new Link2D(masses[x][z], masses[x + 1][z], stretch));
                if (z + 1 < masses[x].length && x != 0)
                    links.add(new Link2D(masses[x][z], masses[x][z + 1], stretch));

                if (x + 1 < masses.length && z + 1 < masses[x].length)
                    links.add(new Link2D(masses[x][z], masses[x + 1][z + 1], shear));
                if (x + 1 < masses.length && z - 1 >= 0)
                    links.add(new Link2D(masses[x][z], masses[x + 1][z - 1], shear));

                if (x + 2 < masses.length) {
                    float dist = (float) (
                            masses[x][z].pos.sub(masses[x + 1][z].pos).length() +
                                    masses[x + 1][z].pos.sub(masses[x + 2][z].pos).length()
                    ); // even if initialized bent, try to keep flat.
                    links.add(new Link2D(masses[x][z], masses[x + 2][z], dist, flex));
                }
                if (z + 2 < masses[x].length) {
                    float dist = (float) (
                            masses[x][z].pos.sub(masses[x][z + 1].pos).length() +
                                    masses[x][z + 1].pos.sub(masses[x][z + 2].pos).length()
                    ); // even if initialized bent, try to keep flat.
                    links.add(new Link2D(masses[x][z], masses[x][z + 2], dist, flex));
                }
            }
        }
    }

    public void tick() {
        Vec2 gravity = new Vec2(0, -0.1);

        for (PointMass2D[] column : masses) {
            for (PointMass2D point : column) {
                if (point.pin)
                    continue;
                point.origPos = point.pos;
                point.pos = point.pos.add(gravity).add(point.pos.sub(point.prevPos));
            }
        }

        for (int i = 0; i < solvePasses; i++) {
            for (Link2D link : links) {
                link.resolve();
            }
        }

        for (Link2D link : hardLinks) {
            link.resolve();
        }
    }

    Vec2 min(Vec2 a, Vec2 b) {
        if (a == null && b == null)
            return null;
        if (a != null && b == null)
            return a;
        if (a == null && b != null)
            return b;

        if (b.squareDist(new Vec2(0.0, 0.0)) < a.squareDist(new Vec2(0.0, 0.0)))
            return b;
        return a;
    }
}
