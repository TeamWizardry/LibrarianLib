package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d

interface IComponentGeometry {
    val transform: ComponentTransform
    var size: Vec2d
    var pos: Vec2d
    var mousePos: Vec2d
    var mouseOver: Boolean

    /**
     * This is like [GuiLayer.mouseOver] except it ignores the occlusion of components at higher z-indices.
     */
    val mouseOverNoOcclusion: Boolean
    /**
     * Set this to false to make this component not occlude the mouseOver of components behind it.
     */
    var componentOccludesMouseOver: Boolean
    /**
     * Set this to false to make a true mouseOver value for this component not cause the mouseOver of its parent to be
     * set to true.
     */
    var componentPropagatesMouseOverToParent: Boolean
    /**
     * Set whether the element should calculate hovering based on its bounds as
     * well as its children or if it should only calculate based on its children.
     */
    var shouldCalculateOwnHover: Boolean

    /**
     *
     */
    fun updateMouseBeforeRender(mousePos: Vec2d)

    /**
     * Takes [pos], which is in our parent's context (coordinate space), and transforms it to our context
     */
    fun transformFromParentContext(pos: Vec2d): Vec2d

    /** [GuiLayer.mouseOver] */
    fun transformToParentContext(pos: Vec2d): Vec2d

    fun transformToParentContext(): Vec2d

    /**
     * Create a matrix that moves coordinates from [other]'s context (coordinate space) to this component's context
     *
     * If [other] is null the returned matrix moves coordinates from the root context to this component's context
     */
    fun otherContextToThisContext(other: GuiLayer?): Matrix4

    /**
     * Create a matrix that moves coordinates from this component's context (coordinate space) to [other]'s context
     *
     * If [other] is null the returned matrix moves coordinates from this component's context to the root context
     */
    fun thisContextToOtherContext(other: GuiLayer?): Matrix4

    /**
     * A shorthand to transform the passed pos in this component's context (coordinate space) to a pos in [other]'s context
     *
     * If [other] is null the returned value is in the root context
     *
     * [pos] defaults to (0, 0)
     */
    fun thisPosToOtherContext(other: GuiLayer?, pos: Vec2d): Vec2d

    fun thisPosToOtherContext(other: GuiLayer?): Vec2d
}

/**
 * TODO: Document file ComponentGeometryHandler
 *
 * Created by TheCodeWarrior
 */
open class ComponentGeometryHandler: IComponentGeometry {
    lateinit var layer: GuiLayer

    /** [GuiLayer.transform] */
    override val transform = ComponentTransform()
    /** [GuiLayer.size] */
    override var size: Vec2d by RMValue(vec(0, 0)) {
        layer.setNeedsLayout()
    }
    /** [GuiLayer.pos] */
    override var pos: Vec2d by transform::translate.delegate
    /** [GuiLayer.mouseOver] */
    override var mousePos = Vec2d.ZERO
    /** [GuiLayer.mouseOver] */
    override var mouseOver = false
    /**
     * This is like [GuiLayer.mouseOver] except it ignores the occlusion of components at higher z-indices.
     */
    override var mouseOverNoOcclusion = false
    /**
     * Set this to false to make this component not occlude the mouseOver of components behind it.
     */
    override var componentOccludesMouseOver = true
    /**
     * Set this to false to make a true mouseOver value for this component not cause the mouseOver of its parent to be
     * set to true.
     */
    override var componentPropagatesMouseOverToParent = true

    /**
     * Set whether the element should calculate hovering based on its bounds as
     * well as its children or if it should only calculate based on its children.
     */
    override var shouldCalculateOwnHover = true

    private var wasMouseOver = false

    override fun updateMouseBeforeRender(mousePos: Vec2d) {
        this.mousePos = layer.transformFromParentContext(mousePos)
        layer.BUS.fire(GuiLayerEvents.AdjustMousePosition())
        layer.children.forEach { it.updateMouseBeforeRender(this.mousePos) }
    }

    /**
     * Takes [pos], which is in our parent's context (coordinate space), and transforms it to our context
     */
    override fun transformFromParentContext(pos: Vec2d): Vec2d {
        return transform.applyInverse(pos)
    }

    /** [GuiLayer.mouseOver] */
    override fun transformToParentContext(pos: Vec2d): Vec2d {
        return transform.apply(pos)
    }

    override fun transformToParentContext(): Vec2d = transformToParentContext(Vec2d.ZERO)

    /**
     * Create a matrix that moves coordinates from [other]'s context (coordinate space) to this component's context
     *
     * If [other] is null the returned matrix moves coordinates from the root context to this component's context
     */
    override fun otherContextToThisContext(other: GuiLayer?): Matrix4 {
        if (other == null)
            return thisContextToOtherContext(null).invert()
        return other.thisContextToOtherContext(layer)
    }

    /**
     * Create a matrix that moves coordinates from this component's context (coordinate space) to [other]'s context
     *
     * If [other] is null the returned matrix moves coordinates from this component's context to the root context
     */
    override fun thisContextToOtherContext(other: GuiLayer?): Matrix4 {
        return _thisContextToOtherContext(other, Matrix4())
    }

    private fun _thisContextToOtherContext(other: GuiLayer?, matrix: Matrix4): Matrix4 {
        if (other == null) {
            layer.parent?.geometry?._thisContextToOtherContext(null, matrix)
            transform.apply(matrix)
            return matrix
        }
        val mat = other.thisContextToOtherContext(null).invert()
        mat *= thisContextToOtherContext(null)
        return mat
    }

    /**
     * A shorthand to transform the passed pos in this component's context (coordinate space) to a pos in [other]'s context
     *
     * If [other] is null the returned value is in the root context
     *
     * [pos] defaults to (0, 0)
     */
    override fun thisPosToOtherContext(other: GuiLayer?, pos: Vec2d): Vec2d {
        return thisContextToOtherContext(other) * pos
    }

    override fun thisPosToOtherContext(other: GuiLayer?): Vec2d = thisPosToOtherContext(other, Vec2d.ZERO)

    fun calculateMouseOver(mousePos: Vec2d) {
        val transformPos = transformFromParentContext(mousePos)
        this.mouseOver = false
        this.mouseOverNoOcclusion = false

        var occludeChildren = false
        var occludeSelf = false

        if (!layer.isVisible || layer.isPointClipped(transformPos)) {
            this.propagateClippedMouseOver()
        } else {
            layer.children.asReversed().forEach { child ->
                child.geometry.calculateMouseOver(transformPos)
                if (occludeChildren) {
                    child.geometry.mouseOver = false // occlude the child position
                }
                if (child.mouseOver && child.componentOccludesMouseOver) {
                    // occlude all siblings below this component
                    occludeChildren = true
                    if(!child.componentPropagatesMouseOverToParent) {
                        // if the component occludes and also doesn't pass the mouseover up the chain, set a flag to
                        // make the parent component's mouseover occlude
                        occludeSelf = true
                    }
                }
                if (child.mouseOver && child.componentPropagatesMouseOverToParent) {
                    // propagate the mouseOver of children up to this, their parent
                    mouseOver = true
                }
                if (child.mouseOverNoOcclusion && child.componentPropagatesMouseOverToParent) {
                    // propagate the non-occluded mouseover to this component
                    mouseOverNoOcclusion = true
                }
            }

            // don't calculate our own bounding box if the mouse is over a non-propagating, occluding child
            if (!occludeSelf) {
                mouseOver = mouseOver || (shouldCalculateOwnHover && calculateOwnHover(mousePos))
            }
            // even if this component should be occluded by a child, use calculateOwnHover for mouseOverNoOcclusion
            mouseOverNoOcclusion = mouseOverNoOcclusion || (shouldCalculateOwnHover && calculateOwnHover(mousePos))
        }

        if (wasMouseOver != layer.mouseOver) {
            if (layer.mouseOver) {
                layer.BUS.fire(GuiLayerEvents.MouseInEvent())
            } else {
                layer.BUS.fire(GuiLayerEvents.MouseOutEvent())
            }
        }
        wasMouseOver = layer.mouseOver
    }

    private fun propagateClippedMouseOver() {
        this.mouseOver = false
        this.mouseOverNoOcclusion = false
        layer.children.forEach { child -> child.geometry.propagateClippedMouseOver() }
    }

    /**
     * Calculates whether the given position is over this component specifically, ignoring any child components.
     */
    fun calculateOwnHover(mousePos: Vec2d): Boolean {
        val transformPos = transformFromParentContext(mousePos)
        return !layer.isPointClipped(transformPos) &&
                transformPos.x >= 0 && transformPos.x <= size.x &&
                transformPos.y >= 0 && transformPos.y <= size.y
    }

}
