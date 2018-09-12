package com.teamwizardry.librarianlib.features.particlesystem

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20

class BlendMode(
        val srcRGB: Factor,
        val dstRGB: Factor,
        val srcAlpha: Factor,
        val dstAlpha: Factor,
        val modeRGB: Equation = Equation.ADD,
        val modeAlpha: Equation = Equation.ADD
) {
    constructor(src: Factor, dst: Factor) : this(src, dst, src, dst)

    fun glApply() {
        GL14.glBlendFuncSeparate(srcRGB.glConst, dstRGB.glConst, srcAlpha.glConst, dstAlpha.glConst)
        GL20.glBlendEquationSeparate(modeRGB.glConst, modeAlpha.glConst)
    }

    fun reset() {
        NORMAL.glApply()
    }

    enum class Equation(val glConst: Int) {
        ADD(GL14.GL_FUNC_ADD),
        SUBTRACT(GL14.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT),
        MIN(GL14.GL_MIN),
        MAX(GL14.GL_MAX)
    }

    enum class Factor(val glConst: Int) {
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
        CONSTANT_COLOR(GL11.GL_CONSTANT_COLOR),
        ONE_MINUS_CONSTANT_COLOR(GL11.GL_ONE_MINUS_CONSTANT_COLOR),
        CONSTANT_ALPHA(GL11.GL_CONSTANT_ALPHA),
        ONE_MINUS_CONSTANT_ALPHA(GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
        SRC_ALPHA_SATURATE(GL11.GL_SRC_ALPHA_SATURATE),
        SRC1_COLOR(GL15.GL_SRC1_RGB),
      //  ONE_MINUS_SRC1_COLOR(GL15.GL_ONE_MINUS_SRC1_COLOR),
        SRC1_ALPHA(GL15.GL_SRC1_ALPHA),
     //   ONE_MINUS_SRC1_ALPHA(GL15.GL_ONE_MINUS_SRC1_ALPHA),
    }

    companion object {
        @JvmStatic
        val NORMAL = BlendMode(Factor.SRC_ALPHA, Factor.ONE_MINUS_SRC_ALPHA, Factor.SRC_ALPHA, Factor.ONE)
        @JvmStatic
        val ADDITIVE = BlendMode(Factor.SRC_ALPHA, Factor.ONE, Factor.SRC_ALPHA, Factor.ONE)
        @JvmStatic
        val SUBTRACTIVE = BlendMode(Factor.SRC_ALPHA, Factor.ONE, Factor.SRC_ALPHA, Factor.ONE, Equation.SUBTRACT, Equation.ADD)
    }
}