package com.teamwizardry.librarianlib.albedo.base.state

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.state.RenderState
import net.minecraft.client.render.RenderPhase
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL33.*

/**
 * Yes, I know we shouldn't have to "undo" our state changes, but this is Mojang we're talking about, so it is.
 *
 * - [RenderPhase.ENABLE_CULLING] is a no-op
 * - [RenderPhase.ALWAYS_DEPTH_TEST] (depth test = `GL_ALWAYS`) is a no-op
 * - [RenderPhase.ALL_MASK] Enabling the depth/color write mask is a no-op
 */
public object BaseRenderStates {
    public data class Blend(
        private val enabled: Boolean,
        private val srcFactor: Factor,
        private val dstFactor: Factor,
        private val srcAlpha: Factor,
        private val dstAlpha: Factor,
        private val rgbEquation: Equation,
        private val alphaEquation: Equation,
    ) : RenderState.State(Identifier("liblib-albedo:blend")) {
        public constructor(
            enabled: Boolean,
            srcFactor: Factor,
            dstFactor: Factor,
            srcAlpha: Factor,
            dstAlpha: Factor,
        ) : this(enabled, srcFactor, dstFactor, srcAlpha, dstAlpha, Equation.ADD, Equation.ADD)

        public constructor(
            enabled: Boolean,
            srcFactor: Factor,
            dstFactor: Factor,
        ) : this(enabled, srcFactor, dstFactor, srcFactor, dstFactor)

        override fun apply() {
            if (enabled) {
                RenderSystem.enableBlend()
                GlStateManager._blendFuncSeparate(srcFactor.value, dstFactor.value, srcAlpha.value, dstAlpha.value)
                glBlendEquationSeparate(rgbEquation.value, alphaEquation.value)
            } else {
                RenderSystem.disableBlend()
            }
        }

        override fun cleanup() {
            RenderSystem.disableBlend()
            RenderSystem.defaultBlendFunc()
            glBlendEquationSeparate(Equation.ADD.value, Equation.ADD.value)
        }

        public enum class Factor(public val value: Int) {
            ZERO(GL_ZERO),
            ONE(GL_ONE),
            SRC_COLOR(GL_SRC_COLOR),
            ONE_MINUS_SRC_COLOR(GL_ONE_MINUS_SRC_COLOR),
            DST_COLOR(GL_DST_COLOR),
            ONE_MINUS_DST_COLOR(GL_ONE_MINUS_DST_COLOR),
            SRC_ALPHA(GL_SRC_ALPHA),
            ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
            DST_ALPHA(GL_DST_ALPHA),
            ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
            CONSTANT_COLOR(GL_CONSTANT_COLOR),
            ONE_MINUS_CONSTANT_COLOR(GL_ONE_MINUS_CONSTANT_COLOR),
            CONSTANT_ALPHA(GL_CONSTANT_ALPHA),
            ONE_MINUS_CONSTANT_ALPHA(GL_ONE_MINUS_CONSTANT_ALPHA),
            SRC_ALPHA_SATURATE(GL_SRC_ALPHA_SATURATE),
            SRC1_COLOR(GL_SRC1_COLOR),
            ONE_MINUS_SRC1_COLOR(GL_ONE_MINUS_SRC1_COLOR),
            SRC1_ALPHA(GL_SRC1_ALPHA),
            ONE_MINUS_SRC1_ALPHA(GL_ONE_MINUS_SRC1_ALPHA),
        }

        public enum class Equation(public val value: Int) {
            ADD(GL_FUNC_ADD),
            SUBTRACT(GL_FUNC_SUBTRACT),
            REVERSE_SUBTRACT(GL_FUNC_REVERSE_SUBTRACT),
            MIN(GL_MIN),
            MAX(GL_MAX),
        }
    }

    public data class Cull(
        private val enabled: Boolean,
    ) : RenderState.State(Identifier("liblib-albedo:cull")) {
        override fun apply() {
            if (enabled) {
                RenderSystem.enableCull()
            } else {
                RenderSystem.disableCull()
            }
        }

        override fun cleanup() {
            RenderSystem.enableCull()
        }
    }

    public data class DepthTest(
        private val enabled: Boolean,
        private val func: Func
    ) : RenderState.State(Identifier("liblib-albedo:depth_test")) {
        public constructor(enabled: Boolean) : this(enabled, Func.LEQUAL)

        override fun apply() {
            if (enabled) {
                RenderSystem.enableDepthTest()
                RenderSystem.depthFunc(func.value)
            } else {
                RenderSystem.disableDepthTest()
            }
        }

        override fun cleanup() {
            RenderSystem.disableDepthTest()
            RenderSystem.depthFunc(Func.LEQUAL.value)
        }

        public enum class Func(public val value: Int) {
            NEVER(GL_NEVER),
            ALWAYS(GL_ALWAYS),
            LESS(GL_LESS),
            LEQUAL(GL_LEQUAL),
            GREATER(GL_GREATER),
            GEQUAL(GL_GEQUAL),
            EQUAL(GL_EQUAL),
            NOTEQUAL(GL_NOTEQUAL);
        }
    }

    public data class WriteMask(
        private val depth: Boolean,
        private val red: Boolean,
        private val green: Boolean,
        private val blue: Boolean,
        private val alpha: Boolean,
    ) : RenderState.State(Identifier("liblib-albedo:write_mask")) {
        public constructor(depth: Boolean, color: Boolean) : this(depth, color, color, color, color)

        override fun apply() {
            RenderSystem.depthMask(depth)
            RenderSystem.colorMask(red, green, blue, alpha)
        }

        override fun cleanup() {
            RenderSystem.depthMask(true)
            RenderSystem.colorMask(true, true, true, true)
        }
    }

}