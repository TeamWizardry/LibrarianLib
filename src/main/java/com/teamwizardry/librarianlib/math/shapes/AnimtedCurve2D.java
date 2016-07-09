package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.math.Vec2;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/7/2016.
 */
public class AnimtedCurve2D {

    /**
     * The two points you want to connect with a jagged line
     */
    private Vec2 point1, point2;
    /**
     * The points that will curve the line
     */
    private Vec2 curve1, curve2;
    /**
     * the amount of points to calculate
     */
    private float pointCount = 50;
    /**
     * The amount to curve the points by. Higher number = bigger curve
     */
    private float curvatureAmount = 50;
    private double shift = 0;
    private double maxShift = 20;
    private boolean add = ThreadLocalRandom.current().nextBoolean();

    public AnimtedCurve2D(Vec2 point1, Vec2 point2, float pointCount, float curvatureAmount, double maxShift) {
        this.point1 = point1;
        this.point2 = point2;
        this.pointCount = pointCount;
        this.curvatureAmount = curvatureAmount;
        this.maxShift = maxShift + ThreadLocalRandom.current().nextDouble(-10, 10);
        add = true;
    }

    public ArrayList<Vec2> getPoints() {
        ArrayList<Vec2> points = new ArrayList<>();

        CardinalDirections direction;

        float angle = (float) Math.toDegrees(Math.atan2(point2.y - point1.y, point2.x - point1.x));
        if (angle < 0) angle += 360;

        // https://upload.wikimedia.org/wikipedia/commons/9/99/Kompas_Sofia.JPG
        // direction of point 2 with respect to point 1
        if (angle <= 290 && angle >= 250) direction = CardinalDirections.WEST;
        else if (angle <= 250 && angle >= 110) direction = CardinalDirections.SOUTH;
        else if (angle <= 110 && angle >= 70) direction = CardinalDirections.EAST;
        else direction = CardinalDirections.NORTH;

        if (direction == CardinalDirections.NORTH) {
            curve1 = point1.add(Math.abs(curvatureAmount + shift), shift);
            curve2 = point2.sub(Math.abs(curvatureAmount - shift), -shift);
        } else if (direction == CardinalDirections.SOUTH) {
            curve1 = point1.sub(Math.abs(curvatureAmount + shift), shift);
            curve2 = point2.add(Math.abs(curvatureAmount - shift), -shift);
        } else if (direction == CardinalDirections.EAST) {
            curve1 = point1.add(shift, Math.abs(curvatureAmount + shift));
            curve2 = point2.sub(-shift, Math.abs(curvatureAmount - shift));
        } else {
            curve1 = point1.sub(shift, Math.abs(curvatureAmount + shift));
            curve2 = point2.add(-shift, Math.abs(curvatureAmount - shift));
        }

        // shift
        if (shift >= maxShift - 1) add = false;
        if (shift <= -maxShift + 1) add = true;

        if (add) shift += (maxShift - Math.abs(shift)) / ThreadLocalRandom.current().nextDouble(30, 80);
        else shift -= Math.abs(maxShift - Math.abs(shift)) / ThreadLocalRandom.current().nextDouble(30, 80);

        // FORMULA: B(t) = (1-t)**3 p0 + 3(1 - t)**2 t P1 + 3(1-t)t**2 P2 + t**3 P3
        for (float i = 0; i < 1; i += 1 / pointCount) {
            double x = (1 - i) * (1 - i) * (1 - i) * point1.x + 3 * (1 - i) * (1 - i) * i * curve1.x + 3 * (1 - i) * i * i * curve2.x + i * i * i * point2.x;
            double y = (1 - i) * (1 - i) * (1 - i) * point1.y + 3 * (1 - i) * (1 - i) * i * curve1.y + 3 * (1 - i) * i * i * curve2.y + i * i * i * point2.y;
            points.add(new Vec2(x, y));
        }

        return points;
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, 1);

        GL11.glEnable(GL11.GL_LINE_STRIP);
        GL11.glLineWidth(2);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (Vec2 point : getPoints())
            GL11.glVertex2d(point.x, point.y);

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private enum CardinalDirections {
        NORTH, SOUTH, EAST, WEST, SOUTH_EAST_EAST, SOUTH_WEST_EAST, SOUTH_WEST_WEST, SOUTH_EAST_WEST, NORTH_EAST_EAST, NORTH_EAST_WEST, NORTH_WEST_EAST, NORTH_WEST_WEST
    }
}
