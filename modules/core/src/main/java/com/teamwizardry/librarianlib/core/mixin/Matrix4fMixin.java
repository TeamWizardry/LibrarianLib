package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix4f;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * I can't use accessors because kotlin won't recognize `geta00` with the lowercase `a`. Why mixin does this I don't
 * know.
 */
@Mixin(Matrix4f.class)
public class Matrix4fMixin implements IMatrix4f {
    @Shadow protected float a00;
    @Shadow protected float a01;
    @Shadow protected float a02;
    @Shadow protected float a03;
    @Shadow protected float a10;
    @Shadow protected float a11;
    @Shadow protected float a12;
    @Shadow protected float a13;
    @Shadow protected float a20;
    @Shadow protected float a21;
    @Shadow protected float a22;
    @Shadow protected float a23;
    @Shadow protected float a30;
    @Shadow protected float a31;
    @Shadow protected float a32;
    @Shadow protected float a33;

    public float getM00() { return a00; }
    public void setM00(float v) { a00 = v; }
    public float getM01() { return a01; }
    public void setM01(float v) { a01 = v; }
    public float getM02() { return a02; }
    public void setM02(float v) { a02 = v; }
    public float getM03() { return a03; }
    public void setM03(float v) { a03 = v; }
    public float getM10() { return a10; }
    public void setM10(float v) { a10 = v; }
    public float getM11() { return a11; }
    public void setM11(float v) { a11 = v; }
    public float getM12() { return a12; }
    public void setM12(float v) { a12 = v; }
    public float getM13() { return a13; }
    public void setM13(float v) { a13 = v; }
    public float getM20() { return a20; }
    public void setM20(float v) { a20 = v; }
    public float getM21() { return a21; }
    public void setM21(float v) { a21 = v; }
    public float getM22() { return a22; }
    public void setM22(float v) { a22 = v; }
    public float getM23() { return a23; }
    public void setM23(float v) { a23 = v; }
    public float getM30() { return a30; }
    public void setM30(float v) { a30 = v; }
    public float getM31() { return a31; }
    public void setM31(float v) { a31 = v; }
    public float getM32() { return a32; }
    public void setM32(float v) { a32 = v; }
    public float getM33() { return a33; }
    public void setM33(float v) { a33 = v; }
}
