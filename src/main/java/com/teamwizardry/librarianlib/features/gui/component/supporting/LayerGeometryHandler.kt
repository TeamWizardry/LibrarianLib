package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.math.Matrix3
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import net.minecraft.client.renderer.GlStateManager

interface ILayerGeometry: CoordinateSpace2D {
    val size_rm: RMValue<Vec2d>
    var size: Vec2d

    val pos_rm: RMValue<Vec2d>
    var pos: Vec2d
    val translateZ_rm: RMValueDouble
    var translateZ: Double

    val scale_rm: RMValue<Vec2d>
    var scale2d: Vec2d
    var scale: Double

    val rotation_rm: RMValueDouble
    var rotation: Double

    val anchor_rm: RMValue<Vec2d>
    var anchor: Vec2d

    var contentsOffset_rm: RMValue<Vec2d>
    var contentsOffset: Vec2d

    val frame: Rect2d
    val bounds: Rect2d
    fun glApplyTransform()
}

class LayerGeometryHandler: ILayerGeometry {
    lateinit var layer: GuiLayer

    override val frame: Rect2d
        get() = parentSpace?.let { this.convertRectTo(bounds, it) } ?: bounds
    override val bounds: Rect2d
        get() = Rect2d(-contentsOffset, size)

    override val size_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            clearMatrixCache()
            boundsChange()
            frameChange()
        }
    }
    override var size: Vec2d by size_rm

    override val pos_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            clearMatrixCache()
            frameChange()
        }
    }
    override var pos: Vec2d by pos_rm

    override val translateZ_rm: RMValueDouble = RMValueDouble(0.0)
    override var translateZ: Double by translateZ_rm

    override val scale_rm: RMValue<Vec2d> = RMValue(Vec2d.ONE) { old, new ->
        if(old != new) {
            clearMatrixCache()
            frameChange()
        }
    }
    override var scale2d: Vec2d by scale_rm
    override var scale: Double
        get() = (scale2d.x + scale2d.y) / 2
        set(value) { scale2d = vec(value, value) }

    override val rotation_rm: RMValueDouble = RMValueDouble(0.0) { old, new ->
        if(old != new) {
            clearMatrixCache()
            frameChange()
        }
    }
    override var rotation: Double by rotation_rm

    override val anchor_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            clearMatrixCache()
            frameChange()
        }
    }
    override var anchor: Vec2d by anchor_rm

    override var contentsOffset_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            clearMatrixCache()
            boundsChange()
        }
    }
    override var contentsOffset: Vec2d by contentsOffset_rm

    override fun glApplyTransform() {
        GlStateManager.translate(pos.x, pos.y, translateZ)
        GlStateManager.rotate(Math.toDegrees(rotation).toFloat(), 0f, 1f, 0f)
        GlStateManager.scale(scale2d.x, scale2d.y, 1.0)
        GlStateManager.translate(-anchor.x, -anchor.y, 0.0)
        GlStateManager.translate(contentsOffset.x, contentsOffset.y, 0.0)
    }

    override val parentSpace: CoordinateSpace2D?
        get() = layer.parent

    private var matrixCache: Matrix3? = null
    override val matrix: Matrix3
        get() {
            val value = matrixCache ?: createMatrix()
            matrixCache = value
            return value
        }

    private var inverseMatrixCache: Matrix3? = null
    override val inverseMatrix: Matrix3
        get() {
            val value = inverseMatrixCache ?: matrix.invertSafely()
            inverseMatrixCache = value
            return value
        }

    private fun createMatrix(): Matrix3 {
        val matrix = Matrix3()
        matrix.translate(pos)
        matrix.rotate(rotation)
        matrix.scale(scale2d)
        matrix.translate(-anchor)
        matrix.translate(contentsOffset)
        return matrix
    }

    private fun clearMatrixCache() {
        matrixCache = null
        inverseMatrixCache = null
    }

    private fun boundsChange() {
        layer.setNeedsLayout()
    }

    private fun frameChange() {
        layer.parent?.setNeedsLayout()
    }
}
