package com.teamwizardry.librarianlib.core.util.lerp

import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.minus
import com.teamwizardry.librarianlib.math.plus
import com.teamwizardry.librarianlib.math.times
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.util.math.Vec3d
import java.awt.Color
import kotlin.math.roundToInt
import kotlin.math.roundToLong

//region primitives =============================================================================================================
object BooleanLerper: Lerper<Boolean>() {
    override fun lerp(from: Boolean, to: Boolean, fraction: Float): Boolean = if(fraction < 0.5f) from else to
}
object PrimitiveBooleanLerper: Lerper<Boolean>(Mirror.types.boolean) {
    override fun lerp(from: Boolean, to: Boolean, fraction: Float): Boolean = if(fraction < 0.5f) from else to
}

object DoubleLerper: Lerper<Double>() {
    override fun lerp(from: Double, to: Double, fraction: Float): Double = from + (to - from) * fraction
}
object PrimitiveDoubleLerper: Lerper<Double>(Mirror.types.double) {
    override fun lerp(from: Double, to: Double, fraction: Float): Double = from + (to - from) * fraction
}

object FloatLerper: Lerper<Float>() {
    override fun lerp(from: Float, to: Float, fraction: Float): Float = from + (to - from) * fraction
}
object PrimitiveFloatLerper: Lerper<Float>(Mirror.types.float) {
    override fun lerp(from: Float, to: Float, fraction: Float): Float = from + (to - from) * fraction
}

object IntLerper: Lerper<Int>() {
    override fun lerp(from: Int, to: Int, fraction: Float): Int = (from + (to - from) * fraction).toInt()
}
object PrimitiveIntLerper: Lerper<Int>(Mirror.types.int) {
    override fun lerp(from: Int, to: Int, fraction: Float): Int = (from + (to - from) * fraction).toInt()
}

object LongLerper: Lerper<Long>() {
    override fun lerp(from: Long, to: Long, fraction: Float): Long = (from + (to - from) * fraction).toLong()
}
object PrimitiveLongLerper: Lerper<Long>(Mirror.types.long) {
    override fun lerp(from: Long, to: Long, fraction: Float): Long = (from + (to - from) * fraction).toLong()
}
//endregion =====================================================================================================================

//region vectors ================================================================================================================

object Vec2dLerper: Lerper<Vec2d>() {
    override fun lerp(from: Vec2d, to: Vec2d, fraction: Float): Vec2d = from + (to - from) * fraction
}

object Vec3dLerper: Lerper<Vec3d>() {
    override fun lerp(from: Vec3d, to: Vec3d, fraction: Float): Vec3d = from + (to - from) * fraction
}

object Rect2dLerper: Lerper<Rect2d>() {
    override fun lerp(from: Rect2d, to: Rect2d, fraction: Float): Rect2d {
        return Rect2d(
            from.pos + (to.pos - from.pos) * fraction,
            from.size + (to.size - from.size) * fraction
        )
    }
}
//endregion =====================================================================================================================

//region others =================================================================================================================
object ColorLerper: Lerper<Color>() {
    override fun lerp(from: Color, to: Color, fraction: Float): Color = Color(
        lerp(from.red, to.red, fraction),
        lerp(from.green, to.green, fraction),
        lerp(from.blue, to.blue, fraction),
        lerp(from.alpha, to.alpha, fraction)
    )

    private fun lerp(from: Int, to: Int, fraction: Float): Int = (from + (to - from) * fraction).toInt()
}
//endregion =====================================================================================================================
