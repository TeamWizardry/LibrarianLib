package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements IMatrix4f {
    @Shadow protected float m00;
    @Shadow protected float m01;
    @Shadow protected float m02;
    @Shadow protected float m03;
    @Shadow protected float m10;
    @Shadow protected float m11;
    @Shadow protected float m12;
    @Shadow protected float m13;
    @Shadow protected float m20;
    @Shadow protected float m21;
    @Shadow protected float m22;
    @Shadow protected float m23;
    @Shadow protected float m30;
    @Shadow protected float m31;
    @Shadow protected float m32;
    @Shadow protected float m33;

    public float getM00() { return m00; }
    public void setM00(float v) { m00 = v; }
    public float getM01() { return m01; }
    public void setM01(float v) { m01 = v; }
    public float getM02() { return m02; }
    public void setM02(float v) { m02 = v; }
    public float getM03() { return m03; }
    public void setM03(float v) { m03 = v; }
    public float getM10() { return m10; }
    public void setM10(float v) { m10 = v; }
    public float getM11() { return m11; }
    public void setM11(float v) { m11 = v; }
    public float getM12() { return m12; }
    public void setM12(float v) { m12 = v; }
    public float getM13() { return m13; }
    public void setM13(float v) { m13 = v; }
    public float getM20() { return m20; }
    public void setM20(float v) { m20 = v; }
    public float getM21() { return m21; }
    public void setM21(float v) { m21 = v; }
    public float getM22() { return m22; }
    public void setM22(float v) { m22 = v; }
    public float getM23() { return m23; }
    public void setM23(float v) { m23 = v; }
    public float getM30() { return m30; }
    public void setM30(float v) { m30 = v; }
    public float getM31() { return m31; }
    public void setM31(float v) { m31 = v; }
    public float getM32() { return m32; }
    public void setM32(float v) { m32 = v; }
    public float getM33() { return m33; }
    public void setM33(float v) { m33 = v; }
}
