package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f;
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public class Matrix3fMixin implements IMatrix3f {
    @Shadow protected float m00;
    @Shadow protected float m01;
    @Shadow protected float m02;
    @Shadow protected float m10;
    @Shadow protected float m11;
    @Shadow protected float m12;
    @Shadow protected float m20;
    @Shadow protected float m21;
    @Shadow protected float m22;

    public float getM00() { return m00; }
    public void setM00(float v) { m00 = v; }
    public float getM01() { return m01; }
    public void setM01(float v) { m01 = v; }
    public float getM02() { return m02; }
    public void setM02(float v) { m02 = v; }
    public float getM10() { return m10; }
    public void setM10(float v) { m10 = v; }
    public float getM11() { return m11; }
    public void setM11(float v) { m11 = v; }
    public float getM12() { return m12; }
    public void setM12(float v) { m12 = v; }
    public float getM20() { return m20; }
    public void setM20(float v) { m20 = v; }
    public float getM21() { return m21; }
    public void setM21(float v) { m21 = v; }
    public float getM22() { return m22; }
    public void setM22(float v) { m22 = v; }
}
