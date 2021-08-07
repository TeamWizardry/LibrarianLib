package com.teamwizardry.librarianlib.albedo.base.state

import com.teamwizardry.librarianlib.albedo.state.RenderState

public object DefaultRenderStates {
    public object Blend {
        @JvmField
        public val DISABLED: RenderState.State = BaseRenderStates.Blend(false,
            BaseRenderStates.Blend.Factor.ONE, BaseRenderStates.Blend.Factor.ZERO
        )
        @JvmField
        public val DEFAULT: RenderState.State = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA,
        )
        @JvmField
        public val ADDITIVE: RenderState.State = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE, // add the alpha-multiplied color
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA
        )
        @JvmField
        public val SUBTRACTIVE: RenderState.State = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE, // subtract the alpha-multiplied color
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA,
            BaseRenderStates.Blend.Equation.REVERSE_SUBTRACT, BaseRenderStates.Blend.Equation.ADD
        )
    }

    public object Cull {
        @JvmField
        public val ENABLED: RenderState.State = BaseRenderStates.Cull(true)
        @JvmField
        public val DISABLED: RenderState.State = BaseRenderStates.Cull(false)
    }

    public object DepthTest {
        @JvmField
        public val DISABLED: RenderState.State = BaseRenderStates.DepthTest(false)
        @JvmField
        public val LEQUAL: RenderState.State = BaseRenderStates.DepthTest(true, BaseRenderStates.DepthTest.Func.LEQUAL)
        @JvmField
        public val ALWAYS: RenderState.State = BaseRenderStates.DepthTest(true, BaseRenderStates.DepthTest.Func.ALWAYS)
    }

    public object WriteMask {
        @JvmField
        public val ENABLED: RenderState.State = BaseRenderStates.WriteMask(depth = true, color = true)
        @JvmField
        public val NO_DEPTH: RenderState.State = BaseRenderStates.WriteMask(depth = false, color = true)
        @JvmField
        public val NO_COLOR: RenderState.State = BaseRenderStates.WriteMask(depth = true, color = false)
    }
}