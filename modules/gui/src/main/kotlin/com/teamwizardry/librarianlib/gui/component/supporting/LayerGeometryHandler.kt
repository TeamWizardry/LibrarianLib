package com.teamwizardry.librarianlib.gui.component.supporting

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.value.RMValue
import com.teamwizardry.librarianlib.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dView
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec

interface ILayerGeometry: CoordinateSpace2D {
    /**
     * The bounding rectangle of this layer in its parent's coordinate space. The "outer" edge. Setting this value will
     * not respect rotation.
     */
    var frame: Rect2d
    /**
     * The bounding rectangle of this layer in its own coordinate space. The "inner" edge. Takes into account
     * [contentsOffset], so the rectangle's position may not be the origin
     */
    val bounds: Rect2d

    /**
     * The size of the layer in its own coordinate space
     */
    val size_rm: RMValue<Vec2d>
    /**
     * The size of the layer in its own coordinate space
     */
    var size: Vec2d

    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    val pos_rm: RMValue<Vec2d>
    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    var pos: Vec2d

    /**
     * The layer's translation along the Z axis. Minecraft's GUI clipping planes are at ±1000 units along the Z axis.
     */
    val translateZ_rm: RMValueDouble
    /**
     * The layer's translation along the Z axis. Minecraft's GUI clipping planes are at ±1000 units along the Z axis.
     */
    var translateZ: Double

    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    val scale_rm: RMValue<Vec2d>
    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    var scale2d: Vec2d
    /**
     * The average scale between the X and Y axes. Setting this value sets both the X and Y scales to this value.
     */
    var scale: Double

    /**
     * The clockwise rotation in radians about the anchor.
     */
    val rotation_rm: RMValueDouble
    /**
     * The clockwise rotation in radians about the anchor.
     */
    var rotation: Double

    /**
     * The fractional anchor position in this layer's coordinate space.
     * (0, 0) is the top-left corner, (1, 1) is the bottom-right, (0.5, 0.5) is the middle, and (-1, -1) is a point
     * outside the bounds of the layer.
     *
     * All transformations (except for [contentsOffset]) focus around this point, not the layer origin.
     *
     * Setting [pos] sets the position of the anchor, not the layer's origin.
     *
     * Setting [rotation] rotates around the anchor, not the layer origin.
     *
     * Setting [scale] scales around the anchor, not the layer origin.
     */
    val anchor_rm: RMValue<Vec2d>
    /**
     * The fractional anchor position in this layer's coordinate space.
     * (0, 0) is the top-left corner, (1, 1) is the bottom-right, (0.5, 0.5) is the middle, and (-1, -1) is a point
     * outside the bounds of the layer.
     *
     * All transformations (except for [contentsOffset]) focus around this point, not the layer origin.
     *
     * Setting [pos] sets the position of the anchor, not the layer's origin.
     *
     * Setting [rotation] rotates around the anchor, not the layer origin.
     *
     * Setting [scale] scales around the anchor, not the layer origin.
     */
    var anchor: Vec2d

    /**
     * The width of this layer as a double.
     *
     * Shorthand for `layer.size.x`
     *
     * @see widthf
     * @see widthi
     */
    @JvmDefault
    var width: Double
        get() = size.x
        set(value) {
            size = vec(value, size.y)
        }
    /**
     * The width of this layer as a float.
     *
     * Shorthand for `layer.size.xf`
     *
     * @see width
     * @see widthi
     */
    @JvmDefault
    var widthf: Float
        get() = size.xf
        set(value) {
            size = vec(value, size.y)
        }
    /**
     * The width of this layer as an int (truncating)
     *
     * Shorthand for `layer.size.xi`
     *
     * @see width
     * @see widthf
     */
    @JvmDefault
    var widthi: Int
        get() = size.xi
        set(value) {
            size = vec(value, size.y)
        }

    /**
     * The height of this layer as a double.
     *
     * Shorthand for `layer.size.y`
     *
     * @see heightf
     * @see heighti
     */
    @JvmDefault
    var height: Double
        get() = size.y
        set(value) {
            size = vec(size.x, value)
        }
    /**
     * The height of this layer as a float.
     *
     * Shorthand for `layer.size.yf`
     *
     * @see height
     * @see heighti
     */
    @JvmDefault
    var heightf: Float
        get() = size.yf
        set(value) {
            size = vec(size.x, value)
        }
    /**
     * The height of this layer as an int (truncating)
     *
     * Shorthand for `layer.size.yi`
     *
     * @see height
     * @see heightf
     */
    @JvmDefault
    var heighti: Int
        get() = size.yi
        set(value) {
            size = vec(size.x, value)
        }

    /**
     * The X position of this layer as a double.
     *
     * Shorthand for `layer.pos.x`
     *
     * @see xf
     * @see xi
     */
    @JvmDefault
    var x: Double
        get() = pos.x
        set(value) {
            pos = vec(value, pos.y)
        }
    /**
     * The X position of this layer as a float.
     *
     * Shorthand for `layer.pos.xf`
     *
     * @see x
     * @see xi
     */
    @JvmDefault
    var xf: Float
        get() = pos.xf
        set(value) {
            pos = vec(value, pos.y)
        }
    /**
     * The X position of this layer as an int (truncating)
     *
     * Shorthand for `layer.pos.xi`
     *
     * @see x
     * @see xf
     */
    @JvmDefault
    var xi: Int
        get() = pos.xi
        set(value) {
            pos = vec(value, pos.y)
        }

    /**
     * The Y position of this layer as a double.
     *
     * Shorthand for `layer.pos.y`
     *
     * @see yf
     * @see yi
     */
    @JvmDefault
    var y: Double
        get() = pos.y
        set(value) {
            pos = vec(pos.x, value)
        }
    /**
     * The Y position of this layer as a float.
     *
     * Shorthand for `layer.pos.yf`
     *
     * @see y
     * @see yi
     */
    @JvmDefault
    var yf: Float
        get() = pos.yf
        set(value) {
            pos = vec(pos.x, value)
        }
    /**
     * The Y position of this layer as an int (truncating)
     *
     * Shorthand for `layer.pos.yi`
     *
     * @see y
     * @see yf
     */
    @JvmDefault
    var yi: Int
        get() = pos.yi
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * Returns true if the passed point is inside the bounds of this component. Testing for [GuiLayer.isPointClipped] in
     * this method's implementation is recommended in order to maintain normal clipping behavior.
     */
    fun isPointInBounds(point: Vec2d): Boolean

    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates.
     *
     * - Any layers for which [includeOwnBounds] returns false will not count their own bounds in the calculation
     * (useful for things such as large mask wrappers which would bloat the content size).
     * - Any layers for which [includeChildren] returns false will not count their children's content bounds
     * (useful primarily in combination with returning false from [includeOwnBounds] to totally exclude a layer)
     *
     * Pseudocode implementation:
     * ```kotlin
     * var totalBounds = null
     * if(includeOwnBounds(this)) {
     *     <expand totalBounds to fit this component's bounds>
     * }
     * if(includeChildren(this)) {
     *     for(child in children) {
     *         <expand totalBounds to fit child's getContentsBounds (converting to this layer's coordinate space)>
     *     }
     * }
     * return totalBounds
     * ```
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
     * Shorthand for `getContentsBounds(includeLayer, includeLayer)`
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
     * layers which have [isVisible][GuiLayer.isVisible] set to false will not be included in the calculation.
     *
     * Shorthand for `getContentsBounds({ it.isVisible})`
     *
     * @return The rect containing all the visible children
     */
    @JvmDefault
    fun getContentsBounds(): Rect2d? {
        return getContentsBounds { it.isVisible }
    }
}

class LayerGeometryHandler(initialFrame: Rect2d): ILayerGeometry {
    lateinit var layer: GuiLayer

    override var frame: Rect2d
        get() = layer.parentSpace?.let { this.convertRectTo(layer.bounds, it) } ?: layer.bounds
        set(value) {
            val current = this.frame
            if(value.size == current.size) {
                layer.pos += value.pos - current.pos
            } else {
                layer.pos = value.pos + value.size * layer.anchor
                val scale = layer.scale2d
                layer.size = vec(
                    if (scale.x == 0.0) layer.size.x else value.width / scale.x,
                    if (scale.y == 0.0) layer.size.y else value.height / scale.y
                )
            }
        }
    override val bounds: Rect2d
        get() = Rect2d(vec(0, 0), layer.size)

    override val size_rm: RMValue<Vec2d> = RMValue(initialFrame.size) { old, new ->
        if(old != new) {
            matrixDirty = true
            boundsChange()
            frameChange()
        }
    }
    override var size: Vec2d by size_rm

    override val pos_rm: RMValue<Vec2d> = RMValue(initialFrame.pos) { old, new ->
        if(old != new) {
            matrixDirty = true
            frameChange()
        }
    }
    override var pos: Vec2d by pos_rm

    override val translateZ_rm: RMValueDouble = RMValueDouble(0.0) { old, new ->
        if(old != new) {
            matrixDirty = true
        }
    }
    override var translateZ: Double by translateZ_rm

    override val scale_rm: RMValue<Vec2d> = RMValue(vec(1, 1)) { old, new ->
        if(old != new) {
            matrixDirty = true
            frameChange()
        }
    }
    override var scale2d: Vec2d by scale_rm
    override var scale: Double
        get() = (layer.scale2d.x + layer.scale2d.y) / 2
        set(value) { layer.scale2d = vec(value, value) }

    override val rotation_rm: RMValueDouble = RMValueDouble(0.0) { old, new ->
        if(old != new) {
            matrixDirty = true
            frameChange()
        }
    }
    override var rotation: Double by rotation_rm

    override val anchor_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
            matrixDirty = true
            frameChange()
        }
    }
    override var anchor: Vec2d by anchor_rm

    override fun isPointInBounds(point: Vec2d): Boolean {
        return point in layer.bounds && !layer.isPointClipped(point)
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

    // TODO: Maybe change how layout is done? If users can override pos/rot/scale properties, we can't cache the matrix.
    private var matrixDirty = true
    private var _matrix = MutableMatrix3d()
    override var matrix: Matrix3d = Matrix3dView(_matrix)
        get() {
            if(matrixDirty) {
                updateMatrix()
            }
            return field
        }
        private set

    private var _inverseMatrix = MutableMatrix3d()
    override var inverseMatrix: Matrix3d = Matrix3dView(_inverseMatrix)
        get() {
            if(matrixDirty) {
                updateMatrix()
            }
            return field
        }
        private set

    private fun updateMatrix() {
        val inverseScale = vec(
            if(layer.scale2d.x == 0.0) Double.POSITIVE_INFINITY else 1.0/layer.scale2d.x,
            if(layer.scale2d.y == 0.0) Double.POSITIVE_INFINITY else 1.0/layer.scale2d.y
        )

        _matrix.set(Matrix3d.IDENTITY)
        _matrix.translate(layer.pos)
        _matrix.rotate2d(layer.rotation)
        _matrix.scale(layer.scale2d.x, layer.scale2d.y, 1.0)
        _matrix.translate(-layer.anchor * layer.size)

        _inverseMatrix.set(Matrix3d.IDENTITY)
        _inverseMatrix.translate(layer.anchor * layer.size)
        _inverseMatrix.scale(inverseScale.x, inverseScale.y, 1.0)
        _inverseMatrix.rotate2d(-layer.rotation)
        _inverseMatrix.translate(-layer.pos)

        matrixDirty = false
    }

    private fun boundsChange() {
        layer.setNeedsLayout()
    }

    private fun frameChange() {
        layer.parent?.setNeedsLayout()
    }
}
