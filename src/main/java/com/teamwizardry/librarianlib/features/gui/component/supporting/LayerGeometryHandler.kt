package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.times
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
        get() = layer.parentSpace?.let { this.convertRectTo(layer.bounds, it) } ?: layer.bounds
    override val bounds: Rect2d
        get() = Rect2d(-layer.contentsOffset, layer.size)

    override val size_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            boundsChange()
            frameChange()
        }
    }
    override var size: Vec2d by size_rm

    override val pos_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            frameChange()
        }
    }
    override var pos: Vec2d by pos_rm

    override val translateZ_rm: RMValueDouble = RMValueDouble(0.0)
    override var translateZ: Double by translateZ_rm

    override val scale_rm: RMValue<Vec2d> = RMValue(Vec2d.ONE) { old, new ->
        if(old != new) {
            frameChange()
        }
    }
    override var scale2d: Vec2d by scale_rm
    override var scale: Double
        get() = (layer.scale2d.x + layer.scale2d.y) / 2
        set(value) { layer.scale2d = vec(value, value) }

    override val rotation_rm: RMValueDouble = RMValueDouble(0.0) { old, new ->
        if(old != new) {
            frameChange()
        }
    }
    override var rotation: Double by rotation_rm

    override val anchor_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            frameChange()
        }
    }
    override var anchor: Vec2d by anchor_rm

    override var contentsOffset_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            boundsChange()
        }
    }
    override var contentsOffset: Vec2d by contentsOffset_rm

    override fun glApplyTransform() {
        updateMatrixIfNeeded()
        GlStateManager.translate(matrixParams.pos.x, matrixParams.pos.y, layer.translateZ)
        GlStateManager.rotate(Math.toDegrees(matrixParams.rotation).toFloat(), 0f, 1f, 0f)
        GlStateManager.scale(matrixParams.scale.x, matrixParams.scale.y, 1.0)
        GlStateManager.translate(-matrixParams.anchor.x, -matrixParams.anchor.y, 0.0)
        GlStateManager.translate(matrixParams.contentsOffset.x, matrixParams.contentsOffset.y, 0.0)
    }

    override val parentSpace: CoordinateSpace2D?
        get() = layer.parent

    override var matrix: Matrix3 = Matrix3.identity
        get() {
            updateMatrixIfNeeded()
            return field
        }
    private set

    override var inverseMatrix: Matrix3 = Matrix3.identity
        get() {
            updateMatrixIfNeeded()
            return field
        }
    private set

    data class MatrixParams(val pos: Vec2d = Vec2d.ZERO, val rotation: Double = 0.0, val scale: Vec2d = Vec2d.ONE,
        val anchor: Vec2d = Vec2d.ZERO, val contentsOffset: Vec2d = Vec2d.ZERO)
    var matrixParams = MatrixParams()

    private fun createMatrix() {
        val matrix = Matrix3()
        matrix.translate(matrixParams.pos)
        matrix.rotate(matrixParams.rotation)
        matrix.scale(matrixParams.scale)
        matrix.translate(-matrixParams.anchor)
        matrix.translate(matrixParams.contentsOffset)
        this.matrix = matrix.frozen()
        this.inverseMatrix = matrix.invertSafely().frozen()
    }

    private fun updateMatrixIfNeeded() {
        val newParams = MatrixParams(layer.pos, layer.rotation, layer.scale2d,
            layer.anchor * layer.size, layer.contentsOffset)

        if(newParams != matrixParams) {
            matrixParams = newParams
            createMatrix()
        }
    }

    private fun boundsChange() {
        layer.setNeedsLayout()
    }

    private fun frameChange() {
        layer.parent?.setNeedsLayout()
    }
}
