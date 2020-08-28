package com.teamwizardry.librarianlib.core.rendering

import com.mojang.blaze3d.systems.RenderSystem
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL20
import java.awt.Color

/**
 * A representation of a full set of blend function, blend equation, and blend color
 *
 * Note: It is vitally important to call [reset]. GlStateManager doesn't track all the states required, so not calling
 * `reset` will lead to `GlStateManager` having out of date information, which causes very bad state leaks.
 */
public data class BlendMode @JvmOverloads constructor(
    val sourceRGB: Factor,
    val destRGB: Factor,
    val sourceAlpha: Factor,
    val destAlpha: Factor,
    val rgbEquation: Equation = Equation.ADD,
    val alphaEquation: Equation = Equation.ADD,
    val constantColor: Color = Color(0, 0, 0, 0)
) {
    public constructor(src: Factor, dst: Factor) : this(src, dst, src, dst)

    public fun glApply() {
        RenderSystem.blendFuncSeparate(sourceRGB.glConst, destRGB.glConst, sourceAlpha.glConst, destAlpha.glConst)
        GL20.glBlendEquationSeparate(rgbEquation.glConst, alphaEquation.glConst)
        RenderSystem.blendColor(constantColor.red/255f, constantColor.green/255f, constantColor.blue/255f,
            constantColor.alpha/255f)
    }

    public fun reset() {
        RenderSystem.blendFuncSeparate(Factor.SRC_ALPHA.glConst, Factor.ONE_MINUS_SRC_ALPHA.glConst, Factor.ONE.glConst, Factor.ZERO.glConst)
        // GlStateManager doesn't track with glBlendEquationSeparate, so we switch back and forth to make sure its
        // internal state is reflected in the GL state
        RenderSystem.blendEquation(GL14.GL_FUNC_SUBTRACT)
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD)
        RenderSystem.blendColor(0f, 0f, 0f, 0f)
    }

    public enum class Equation(public val glConst: Int) {
        ADD(GL14.GL_FUNC_ADD),
        SUBTRACT(GL14.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT),
        MIN(GL14.GL_MIN),
        MAX(GL14.GL_MAX)
    }

    public enum class Factor(public val glConst: Int) {
        ZERO(GL11.GL_ZERO),
        ONE(GL11.GL_ONE),
        SRC_COLOR(GL11.GL_SRC_COLOR),
        ONE_MINUS_SRC_COLOR(GL11.GL_ONE_MINUS_SRC_COLOR),
        DST_COLOR(GL11.GL_DST_COLOR),
        ONE_MINUS_DST_COLOR(GL11.GL_ONE_MINUS_DST_COLOR),
        SRC_ALPHA(GL11.GL_SRC_ALPHA),
        ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
        DST_ALPHA(GL11.GL_DST_ALPHA),
        ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
        CONSTANT_COLOR(GL14.GL_CONSTANT_COLOR),
        ONE_MINUS_CONSTANT_COLOR(GL14.GL_ONE_MINUS_CONSTANT_COLOR),
        CONSTANT_ALPHA(GL14.GL_CONSTANT_ALPHA),
        ONE_MINUS_CONSTANT_ALPHA(GL14.GL_ONE_MINUS_CONSTANT_ALPHA),
        SRC_ALPHA_SATURATE(GL11.GL_SRC_ALPHA_SATURATE)
    }

    public companion object {
        @JvmStatic
        public val NORMAL: BlendMode = BlendMode(Factor.SRC_ALPHA, Factor.ONE_MINUS_SRC_ALPHA, Factor.ONE, Factor.ZERO)
        @JvmStatic
        public val ADDITIVE: BlendMode = BlendMode(Factor.SRC_ALPHA, Factor.ONE, Factor.ONE, Factor.ZERO)
        @JvmStatic
        public val SUBTRACTIVE: BlendMode = BlendMode(Factor.SRC_ALPHA, Factor.ONE, Factor.ONE, Factor.ZERO, Equation.SUBTRACT, Equation.ADD)
    }
}
