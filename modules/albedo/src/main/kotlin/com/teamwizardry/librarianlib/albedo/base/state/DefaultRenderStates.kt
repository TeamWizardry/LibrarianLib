package com.teamwizardry.librarianlib.albedo.base.state

public object DefaultRenderStates {
    public object Blend {
        @JvmField
        public val DISABLED: BaseRenderStates.Blend = BaseRenderStates.Blend(false,
            BaseRenderStates.Blend.Factor.ONE, BaseRenderStates.Blend.Factor.ZERO
        )
        @JvmField
        public val DEFAULT: BaseRenderStates.Blend = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA,
        )
        @JvmField
        public val ADDITIVE: BaseRenderStates.Blend = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE, // add the alpha-multiplied color
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA
        )
        @JvmField
        public val SUBTRACTIVE: BaseRenderStates.Blend = BaseRenderStates.Blend(true,
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE, // subtract the alpha-multiplied color
            BaseRenderStates.Blend.Factor.SRC_ALPHA, BaseRenderStates.Blend.Factor.ONE_MINUS_SRC_ALPHA,
            BaseRenderStates.Blend.Equation.REVERSE_SUBTRACT, BaseRenderStates.Blend.Equation.ADD
        )
    }

    public object Cull {
        @JvmField
        public val ENABLED: BaseRenderStates.Cull = BaseRenderStates.Cull(true)
        @JvmField
        public val DISABLED: BaseRenderStates.Cull = BaseRenderStates.Cull(false)
    }

    public object DepthTest {
        @JvmField
        public val DISABLED: BaseRenderStates.DepthTest = BaseRenderStates.DepthTest(false)
        @JvmField
        public val LEQUAL: BaseRenderStates.DepthTest = BaseRenderStates.DepthTest(true, BaseRenderStates.DepthTest.Func.LEQUAL)
        @JvmField
        public val ALWAYS: BaseRenderStates.DepthTest = BaseRenderStates.DepthTest(true, BaseRenderStates.DepthTest.Func.ALWAYS)
    }

    public object WriteMask {
        @JvmField
        public val ENABLED: BaseRenderStates.WriteMask = BaseRenderStates.WriteMask(depth = true, color = true)
        @JvmField
        public val NO_DEPTH: BaseRenderStates.WriteMask = BaseRenderStates.WriteMask(depth = false, color = true)
        @JvmField
        public val NO_COLOR: BaseRenderStates.WriteMask = BaseRenderStates.WriteMask(depth = true, color = false)
    }
}