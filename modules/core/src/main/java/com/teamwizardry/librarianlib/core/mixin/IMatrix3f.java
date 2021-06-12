package com.teamwizardry.librarianlib.core.mixin;

import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix3f.class)
public interface IMatrix3f {
    /**
     * Get row 0, column 0
     */
    @Accessor("a00")
    float getM00();

    /**
     * Set row 0, column 0
     */
    @Accessor("a00")
    void setM00(float v);

    /**
     * Get row 0, column 1
     */
    @Accessor("a01")
    float getM01();

    /**
     * Set row 0, column 1
     */
    @Accessor("a01")
    void setM01(float v);

    /**
     * Get row 0, column 2
     */
    @Accessor("a02")
    float getM02();

    /**
     * Set row 0, column 2
     */
    @Accessor("a02")
    void setM02(float v);

    /**
     * Get row 1, column 0
     */
    @Accessor("a10")
    float getM10();

    /**
     * Set row 1, column 0
     */
    @Accessor("a10")
    void setM10(float v);

    /**
     * Get row 1, column 1
     */
    @Accessor("a11")
    float getM11();

    /**
     * Set row 1, column 1
     */
    @Accessor("a11")
    void setM11(float v);

    /**
     * Get row 1, column 2
     */
    @Accessor("a12")
    float getM12();

    /**
     * Set row 1, column 2
     */
    @Accessor("a12")
    void setM12(float v);

    /**
     * Get row 2, column 0
     */
    @Accessor("a20")
    float getM20();

    /**
     * Set row 2, column 0
     */
    @Accessor("a20")
    void setM20(float v);

    /**
     * Get row 2, column 1
     */
    @Accessor("a21")
    float getM21();

    /**
     * Set row 2, column 1
     */
    @Accessor("a21")
    void setM21(float v);

    /**
     * Get row 2, column 2
     */
    @Accessor("a22")
    float getM22();

    /**
     * Set row 2, column 2
     */
    @Accessor("a22")
    void setM22(float v);
}
