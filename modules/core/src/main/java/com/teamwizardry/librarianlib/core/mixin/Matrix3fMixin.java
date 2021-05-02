package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f;
import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * I can't use accessors because kotlin won't recognize `geta00` with the lowercase `a`. Why mixin does this I don't
 * know.
 */
@Mixin(Matrix3f.class)
class Matrix3fMixin implements IMatrix3f {
    @Shadow protected float a00;
    @Shadow protected float a01;
    @Shadow protected float a02;
    @Shadow protected float a10;
    @Shadow protected float a11;
    @Shadow protected float a12;
    @Shadow protected float a20;
    @Shadow protected float a21;
    @Shadow protected float a22;

    public float getM00() { return a00; }
    public void setM00(float v) { a00 = v; }
    public float getM01() { return a01; }
    public void setM01(float v) { a01 = v; }
    public float getM02() { return a02; }
    public void setM02(float v) { a02 = v; }
    public float getM10() { return a10; }
    public void setM10(float v) { a10 = v; }
    public float getM11() { return a11; }
    public void setM11(float v) { a11 = v; }
    public float getM12() { return a12; }
    public void setM12(float v) { a12 = v; }
    public float getM20() { return a20; }
    public void setM20(float v) { a20 = v; }
    public float getM21() { return a21; }
    public void setM21(float v) { a21 = v; }
    public float getM22() { return a22; }
    public void setM22(float v) { a22 = v; }
}
