package com.teamwizardry.librarianlib.core.util.lerp

public object Lerpers: LerperMatcher() {
    init {
        registerDefaultLerpers()
    }

    private fun registerDefaultLerpers() {
        register(
            BooleanLerper, PrimitiveBooleanLerper,
            DoubleLerper, PrimitiveDoubleLerper,
            FloatLerper, PrimitiveFloatLerper,
            IntLerper, PrimitiveIntLerper,
            LongLerper, PrimitiveLongLerper,
            Vec2dLerper, Vec3dLerper, Rect2dLerper,
            ColorLerper
        )
    }
}
