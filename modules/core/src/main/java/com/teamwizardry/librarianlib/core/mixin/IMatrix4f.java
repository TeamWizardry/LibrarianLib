package com.teamwizardry.librarianlib.core.mixin;

import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface IMatrix4f {
    /** Get row 0, column 0 */
    @Accessor("a00") float getM00();
    /** Set row 0, column 0 */
    @Accessor("a00") void setM00(float v);
    /** Get row 0, column 1 */
    @Accessor("a01") float getM01();
    /** Set row 0, column 1 */
    @Accessor("a01") void setM01(float v);
    /** Get row 0, column 2 */
    @Accessor("a02") float getM02();
    /** Set row 0, column 2 */
    @Accessor("a02") void setM02(float v);
    /** Get row 0, column 3 */
    @Accessor("a03") float getM03();
    /** Set row 0, column 3 */
    @Accessor("a03") void setM03(float v);
    /** Get row 1, column 0 */
    @Accessor("a10") float getM10();
    /** Set row 1, column 0 */
    @Accessor("a10") void setM10(float v);
    /** Get row 1, column 1 */
    @Accessor("a11") float getM11();
    /** Set row 1, column 1 */
    @Accessor("a11") void setM11(float v);
    /** Get row 1, column 2 */
    @Accessor("a12") float getM12();
    /** Set row 1, column 2 */
    @Accessor("a12") void setM12(float v);
    /** Get row 1, column 3 */
    @Accessor("a13") float getM13();
    /** Set row 1, column 3 */
    @Accessor("a13") void setM13(float v);
    /** Get row 2, column 0 */
    @Accessor("a20") float getM20();
    /** Set row 2, column 0 */
    @Accessor("a20") void setM20(float v);
    /** Get row 2, column 1 */
    @Accessor("a21") float getM21();
    /** Set row 2, column 1 */
    @Accessor("a21") void setM21(float v);
    /** Get row 2, column 2 */
    @Accessor("a22") float getM22();
    /** Set row 2, column 2 */
    @Accessor("a22") void setM22(float v);
    /** Get row 2, column 3 */
    @Accessor("a23") float getM23();
    /** Set row 2, column 3 */
    @Accessor("a23") void setM23(float v);
    /** Get row 3, column 0 */
    @Accessor("a30") float getM30();
    /** Set row 3, column 0 */
    @Accessor("a30") void setM30(float v);
    /** Get row 3, column 1 */
    @Accessor("a31") float getM31();
    /** Set row 3, column 1 */
    @Accessor("a31") void setM31(float v);
    /** Get row 3, column 2 */
    @Accessor("a32") float getM32();
    /** Set row 3, column 2 */
    @Accessor("a32") void setM32(float v);
    /** Get row 3, column 3 */
    @Accessor("a33") float getM33();
    /** Set row 3, column 3 */
    @Accessor("a33") void setM33(float v);
}
