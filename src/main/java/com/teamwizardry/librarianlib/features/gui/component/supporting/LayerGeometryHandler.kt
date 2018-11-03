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
    val frame: Rect2d
    val bounds: Rect2d

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

    fun glApplyTransform()
    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates and
     * will contain all of its children that both have [isVisible][GuiLayer.isVisible] set to true and return true from
     * the passed predicate. NOTE: layers that fail the passed predicate will still include their children.
     *
     * @param predicate A predicate to filter the layers included
     * @return The rect containing all the visible children that match the predicate, or null if neither this layer nor
     * any of its children were visible and matched the predicate
     */
    fun getContentsBounds(predicate: (layer: GuiLayer) -> Boolean): Rect2d?
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
        GlStateManager.rotate(Math.toDegrees(matrixParams.rotation).toFloat(), 0f, 0f, 1f)
        GlStateManager.scale(matrixParams.scale.x, matrixParams.scale.y, 1.0)
        GlStateManager.translate(-matrixParams.anchor.x, -matrixParams.anchor.y, 0.0)
        GlStateManager.translate(matrixParams.contentsOffset.x, matrixParams.contentsOffset.y, 0.0)
    }

    override fun getContentsBounds(predicate: (layer: GuiLayer) -> Boolean): Rect2d? {
        var bounds: Rect2d? = null
        if(predicate(layer) && layer.isVisible) {
            bounds = layer.bounds
        }
        for(child in layer.children) {
            val subBounds = child.getContentsBounds(predicate) ?: continue
            val subFrame = child.convertRectToParent(subBounds)
            bounds = bounds?.expandToFit(subFrame) ?: subFrame
        }
        return bounds
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

        val inverseX = if(matrixParams.scale.x == 0.0) Double.POSITIVE_INFINITY else 1.0/matrixParams.scale.x
        val inverseY = if(matrixParams.scale.y == 0.0) Double.POSITIVE_INFINITY else 1.0/matrixParams.scale.y
        val inverseMatrix = Matrix3()
        inverseMatrix.translate(-matrixParams.contentsOffset)
        inverseMatrix.translate(matrixParams.anchor)
        inverseMatrix.scale(vec(inverseX, inverseY))
        inverseMatrix.rotate(-matrixParams.rotation)
        inverseMatrix.translate(-matrixParams.pos)
        this.inverseMatrix = inverseMatrix.frozen()
        matrix.toString()
    }

    private fun updateMatrixIfNeeded() {
        val newParams = MatrixParams(layer.pos, layer.rotation, layer.scale2d,
            layer.anchor * layer.size, layer.contentsOffset)

        if(newParams != matrixParams) {
            matrixParams = newParams
            createMatrix()
        }
    }

    // MAKE PUBLIC AND RENAME
    private fun boundsChange() {
        layer.setNeedsLayout()
    }

    // MAKE PUBLIC AND RENAME
    private fun frameChange() {
        layer.parent?.setNeedsLayout()
    }
}
