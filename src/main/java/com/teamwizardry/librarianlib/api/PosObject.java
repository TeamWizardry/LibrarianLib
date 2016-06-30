package com.teamwizardry.librarianlib.api;

import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 6/30/2016.
 */
public class PosObject {

    private double x, y, z;

    public PosObject(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PosObject(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
