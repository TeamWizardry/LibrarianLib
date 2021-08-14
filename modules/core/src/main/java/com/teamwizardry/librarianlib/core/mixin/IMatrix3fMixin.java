package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix3f;
import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix3f.class)
public abstract class IMatrix3fMixin implements IMatrix3f {
    @Accessor("a00") @Override public abstract float getM00();
    @Accessor("a00") @Override public abstract void setM00(float v);
    @Accessor("a01") @Override public abstract float getM01();
    @Accessor("a01") @Override public abstract void setM01(float v);
    @Accessor("a02") @Override public abstract float getM02();
    @Accessor("a02") @Override public abstract void setM02(float v);
    @Accessor("a10") @Override public abstract float getM10();
    @Accessor("a10") @Override public abstract void setM10(float v);
    @Accessor("a11") @Override public abstract float getM11();
    @Accessor("a11") @Override public abstract void setM11(float v);
    @Accessor("a12") @Override public abstract float getM12();
    @Accessor("a12") @Override public abstract void setM12(float v);
    @Accessor("a20") @Override public abstract float getM20();
    @Accessor("a20") @Override public abstract void setM20(float v);
    @Accessor("a21") @Override public abstract float getM21();
    @Accessor("a21") @Override public abstract void setM21(float v);
    @Accessor("a22") @Override public abstract float getM22();
    @Accessor("a22") @Override public abstract void setM22(float v);

    @Override
    public float transformX(float x, float y, float z) {
        return getM00() * x + getM01() * y + getM02() * z;
    }

    @Override
    public float transformY(float x, float y, float z) {
        return getM10() * x + getM11() * y + getM12() * z;
    }

    @Override
    public float transformZ(float x, float y, float z) {
        return getM20() * x + getM21() * y + getM22() * z;
    }

    @Override
    public float transformX(float x, float y) {
        return getM00() * x + getM01() * y + getM02() * 1;
    }

    @Override
    public float transformY(float x, float y) {
        return getM10() * x + getM11() * y + getM12() * 1;
    }
}
