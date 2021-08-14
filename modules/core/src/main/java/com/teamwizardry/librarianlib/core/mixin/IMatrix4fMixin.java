package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.IMatrix4f;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public abstract class IMatrix4fMixin implements IMatrix4f {
    @Accessor("a00") public abstract float getM00();
    @Accessor("a00") public abstract void setM00(float v);
    @Accessor("a01") public abstract float getM01();
    @Accessor("a01") public abstract void setM01(float v);
    @Accessor("a02") public abstract float getM02();
    @Accessor("a02") public abstract void setM02(float v);
    @Accessor("a03") public abstract float getM03();
    @Accessor("a03") public abstract void setM03(float v);
    @Accessor("a10") public abstract float getM10();
    @Accessor("a10") public abstract void setM10(float v);
    @Accessor("a11") public abstract float getM11();
    @Accessor("a11") public abstract void setM11(float v);
    @Accessor("a12") public abstract float getM12();
    @Accessor("a12") public abstract void setM12(float v);
    @Accessor("a13") public abstract float getM13();
    @Accessor("a13") public abstract void setM13(float v);
    @Accessor("a20") public abstract float getM20();
    @Accessor("a20") public abstract void setM20(float v);
    @Accessor("a21") public abstract float getM21();
    @Accessor("a21") public abstract void setM21(float v);
    @Accessor("a22") public abstract float getM22();
    @Accessor("a22") public abstract void setM22(float v);
    @Accessor("a23") public abstract float getM23();
    @Accessor("a23") public abstract void setM23(float v);
    @Accessor("a30") public abstract float getM30();
    @Accessor("a30") public abstract void setM30(float v);
    @Accessor("a31") public abstract float getM31();
    @Accessor("a31") public abstract void setM31(float v);
    @Accessor("a32") public abstract float getM32();
    @Accessor("a32") public abstract void setM32(float v);
    @Accessor("a33") public abstract float getM33();
    @Accessor("a33") public abstract void setM33(float v);

    @Override
    public float transformX(float x, float y, float z) {
        return getM00() * x + getM01() * y + getM02() * z + getM03() * 1;
    }

    @Override
    public float transformY(float x, float y, float z) {
        return getM10() * x + getM11() * y + getM12() * z + getM13() * 1;
    }

    @Override
    public float transformZ(float x, float y, float z) {
        return getM20() * x + getM21() * y + getM22() * z + getM23() * 1;
    }

    @Override
    public float transformDeltaX(float x, float y, float z) {
        return getM00() * x + getM01() * y + getM02() * z;
    }

    @Override
    public float transformDeltaY(float x, float y, float z) {
        return getM10() * x + getM11() * y + getM12() * z;
    }

    @Override
    public float transformDeltaZ(float x, float y, float z) {
        return getM20() * x + getM21() * y + getM22() * z;
    }
}
