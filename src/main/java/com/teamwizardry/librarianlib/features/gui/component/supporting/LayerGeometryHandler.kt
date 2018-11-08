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

    @JvmDefault
    var width: Double
        get() = size.x
        set(value) {
            size = vec(value, size.y)
        }
    @JvmDefault
    var widthf: Float
        get() = size.xf
        set(value) {
            size = vec(value, size.y)
        }
    @JvmDefault
    var widthi: Int
        get() = size.xi
        set(value) {
            size = vec(value, size.y)
        }

    @JvmDefault
    var height: Double
        get() = size.y
        set(value) {
            size = vec(size.x, value)
        }
    @JvmDefault
    var heightf: Float
        get() = size.yf
        set(value) {
            size = vec(size.x, value)
        }
    @JvmDefault
    var heighti: Int
        get() = size.yi
        set(value) {
            size = vec(size.x, value)
        }

    @JvmDefault
    var x: Double
        get() = pos.x
        set(value) {
            pos = vec(value, pos.y)
        }
    @JvmDefault
    var xf: Float
        get() = pos.xf
        set(value) {
            pos = vec(value, pos.y)
        }
    @JvmDefault
    var xi: Int
        get() = pos.xi
        set(value) {
            pos = vec(value, pos.y)
        }

    @JvmDefault
    var y: Double
        get() = pos.y
        set(value) {
            pos = vec(pos.x, value)
        }
    @JvmDefault
    var yf: Float
        get() = pos.yf
        set(value) {
            pos = vec(pos.x, value)
        }
    @JvmDefault
    var yi: Int
        get() = pos.yi
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * Applies this layer's transforms, barring the final content offset operation
     */
    fun glApplyTransform(inverse: Boolean)

    /**
     * Performs the final content offset operation
     */
    fun glApplyContentsOffset(inverse: Boolean)

    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates. Any
     * layers for which [includeOwnBounds] returns false will not count their own bounds in the calculation
     * (useful for things such as large mask wrappers which would bloat the content size). Any layers for which
     * [includeChildren] returns false will not count their children's content bounds (useful primarily in combination
     * with returning false from [includeOwnBounds] to totally exclude a layer)
     *
     * @param includeOwnBounds A predicate to filter out which layers should count their own bounds
     * @param includeChildren A predicate to filter out which layers should count their children's bounds
     * @return The rect containing all the children that are included as per the above rules, or null if neither this
     * layer nor any of its children were included
     */
    fun getContentsBounds(
        includeOwnBounds: (layer: GuiLayer) -> Boolean,
        includeChildren: (layer: GuiLayer) -> Boolean
    ): Rect2d?

    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates. Any
     * layers for which [includeLayer] returns false will not count their own bounds nor their children's in the
     * calculation.
     *
     * @param includeLayer A predicate to filter out which layers should count their own bounds
     * @return The rect containing all the children that return true from the passed predicate
     */
    @JvmDefault
    fun getContentsBounds(includeLayer: (layer: GuiLayer) -> Boolean): Rect2d? {
        return getContentsBounds(includeLayer, includeLayer)
    }

    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates. Any
     * layers which have [isVisible][GuiLayer.isVisible] set to false will not count their own bounds nor their
     * children's in the calculation.
     *
     * @return The rect containing all the children that return true from the passed predicate
     */
    @JvmDefault
    fun getContentsBounds(): Rect2d? {
        return getContentsBounds { it.isVisible }
    }
}

class LayerGeometryHandler(initialFrame: Rect2d): ILayerGeometry {
    lateinit var layer: GuiLayer

    override val frame: Rect2d
        get() = layer.parentSpace?.let { this.convertRectTo(layer.bounds, it) } ?: layer.bounds
    override val bounds: Rect2d
        get() = Rect2d(-layer.contentsOffset, layer.size)

    override val size_rm: RMValue<Vec2d> = RMValue(initialFrame.size) { old, new ->
        if(old != new) {
            boundsChange()
            frameChange()
        }
    }
    override var size: Vec2d by size_rm

    override val pos_rm: RMValue<Vec2d> = RMValue(initialFrame.pos) { old, new ->
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

    override fun glApplyTransform(inverse: Boolean) {
        if(inverse) {
            if(matrixParams.scale.x == 0.0 || matrixParams.scale.y == 0.0) {
                GlStateManager.popMatrix()
            } else {
                GlStateManager.translate(-matrixParams.anchor.x, -matrixParams.anchor.y, 0.0)
                GlStateManager.scale(matrixParams.inverseScale.x, matrixParams.inverseScale.y, 1.0)
                GlStateManager.rotate(-Math.toDegrees(matrixParams.rotation).toFloat(), 0f, 0f, 1f)
                GlStateManager.translate(-matrixParams.pos.x, -matrixParams.pos.y, -layer.translateZ)
            }
        } else {
            updateMatrixIfNeeded()
            if(matrixParams.scale.x == 0.0 || matrixParams.scale.y == 0.0) {
                GlStateManager.pushMatrix()
            }
            GlStateManager.translate(matrixParams.pos.x, matrixParams.pos.y, layer.translateZ)
            GlStateManager.rotate(Math.toDegrees(matrixParams.rotation).toFloat(), 0f, 0f, 1f)
            GlStateManager.scale(matrixParams.scale.x, matrixParams.scale.y, 1.0)
            GlStateManager.translate(-matrixParams.anchor.x, -matrixParams.anchor.y, 0.0)
        }
    }

    override fun glApplyContentsOffset(inverse: Boolean) {
        val z = if(GuiLayer.isDebugMode) 0.1 else 0.0
        if(inverse) {
            GlStateManager.translate(-matrixParams.contentsOffset.x, -matrixParams.contentsOffset.y, z)
        } else {
            GlStateManager.translate(matrixParams.contentsOffset.x, matrixParams.contentsOffset.y, z)
        }
    }

    override fun getContentsBounds(
        includeOwnBounds: (layer: GuiLayer) -> Boolean,
        includeChildren: (layer: GuiLayer) -> Boolean
    ): Rect2d? {
        var bounds: Rect2d? = null
        if (includeOwnBounds(layer)) {
            bounds = layer.bounds
        }
        if (includeChildren(layer)) {
            for (child in layer.children) {
                val subBounds = child.getContentsBounds(includeOwnBounds, includeChildren) ?: continue
                val subFrame = child.convertRectToParent(subBounds)
                bounds = bounds?.expandToFit(subFrame) ?: subFrame
            }
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
        val inverseScale: Vec2d = Vec2d.ONE, val anchor: Vec2d = Vec2d.ZERO, val contentsOffset: Vec2d = Vec2d.ZERO)
    var matrixParams = MatrixParams()

    private fun createMatrix() {
        val matrix = Matrix3()
        matrix.translate(matrixParams.pos)
        matrix.rotate(matrixParams.rotation)
        matrix.scale(matrixParams.scale)
        matrix.translate(-matrixParams.anchor)
        matrix.translate(matrixParams.contentsOffset)
        this.matrix = matrix.frozen()

        val inverseMatrix = Matrix3()
        inverseMatrix.translate(-matrixParams.contentsOffset)
        inverseMatrix.translate(matrixParams.anchor)
        inverseMatrix.scale(matrixParams.inverseScale)
        inverseMatrix.rotate(-matrixParams.rotation)
        inverseMatrix.translate(-matrixParams.pos)
        this.inverseMatrix = inverseMatrix.frozen()
    }

    private fun updateMatrixIfNeeded() {
        val inverseScale = vec(
            if(layer.scale2d.x == 0.0) Double.POSITIVE_INFINITY else 1.0/layer.scale2d.x,
            if(layer.scale2d.y == 0.0) Double.POSITIVE_INFINITY else 1.0/layer.scale2d.y
        )
        val newParams = MatrixParams(layer.pos, layer.rotation, layer.scale2d, inverseScale,
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
