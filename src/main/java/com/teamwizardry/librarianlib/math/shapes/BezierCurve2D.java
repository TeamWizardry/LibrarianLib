package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.math.Vec2;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * Created by Saad on 15/7/2016.
 */
public class BezierCurve2D {
    /**
     * The two points you want to connect with a jagged line
     */
    private Vec2 startPoint, endPoint;

    /**
     * The points that will curve the line
     */
    private Vec2 startControlPoint, endControlPoint;

    public BezierCurve2D(Vec2 startPoint, Vec2 endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public ArrayList<Vec2> getPoints() {
        ArrayList<Vec2> points = new ArrayList<>();

        Vec2 midpoint = startPoint.sub(endPoint).mul(1.0 / 2.0);

        startControlPoint = startPoint.sub(midpoint.x, 0);
        endControlPoint = endPoint.add(midpoint.x, 0);

        // FORMULA: B(t) = (1-t)**3 p0 + 3(1 - t)**2 t P1 + 3(1-t)t**2 P2 + t**3 P3
        float pointCount = 50;
        for (float i = 0; i < 1; i += 1 / pointCount) {
            double x = (1 - i) * (1 - i) * (1 - i) * startPoint.x + 3 * (1 - i) * (1 - i) * i * startControlPoint.x + 3 * (1 - i) * i * i * endControlPoint.x + i * i * i * endPoint.x;
            double y = (1 - i) * (1 - i) * (1 - i) * startPoint.y + 3 * (1 - i) * (1 - i) * i * startControlPoint.y + 3 * (1 - i) * i * i * endControlPoint.y + i * i * i * endPoint.y;
            points.add(new Vec2(x, y));
        }

        return points;
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, 1);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_STRIP);
        GL11.glLineWidth(2);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (Vec2 point : getPoints()) GL11.glVertex2d(point.x, point.y);

        GL11.glEnd();
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public Vec2 getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Vec2 startPoint) {
        this.startPoint = startPoint;
    }

    public Vec2 getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Vec2 endPoint) {
        this.endPoint = endPoint;
    }
}