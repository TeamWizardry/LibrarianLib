package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * TODO: Document file ComponentGeometryHandler
 *
 * Created by TheCodeWarrior
 */
open class ComponentGeometryHandler(private val component: GuiComponent) {
    /** [GuiComponent.transform] */
    val transform = ComponentTransform()
    /** [GuiComponent.size] */
    var size: Vec2d = vec(0, 0)
    /** [GuiComponent.pos] */
    var pos: Vec2d
        get() = transform.translate
        set(value) {
            transform.translate = value
        }
    /** [GuiComponent.mouseOver] */
    var mouseOver = false
    /**
     * This is like [GuiComponent.mouseOver] except it ignores the occlusion of components at higher z-indices.
     */
    var mouseOverNoOcclusion = false
        private set
    /**
     * Set this to false to make this component not occlude the mouseOver of components behind it.
     */
    var componentOccludesMouseOver = true
    /**
     * Set this to false to make a true mouseOver value for this component not cause the mouseOver of its parent to be
     * set to true.
     */
    var componentPropagatesMouseOverToParent = true

    /**
     * Set whether the element should calculate hovering based on its bounds as
     * well as its children or if it should only calculate based on its children.
     */
    var shouldCalculateOwnHover = true


    /**
     * Takes [pos], which is in our parent's context (coordinate space), and transforms it to our context
     */
    fun transformFromParentContext(pos: Vec2d): Vec2d {
        return transform.applyInverse(pos)
    }

    /** [GuiComponent.mouseOver] */
    @JvmOverloads
    fun transformToParentContext(pos: Vec2d = Vec2d.ZERO): Vec2d {
        return transform.apply(pos)
    }

    /**
     * Create a matrix that moves coordinates from [other]'s context (coordinate space) to this component's context
     *
     * If [other] is null the returned matrix moves coordinates from the root context to this component's context
     */
    fun otherContextToThisContext(other: GuiComponent?): Matrix4 {
        if (other == null)
            return thisContextToOtherContext(null).invert()
        return other.geometry.thisContextToOtherContext(component)
    }

    /**
     * Create a matrix that moves coordinates from this component's context (coordinate space) to [other]'s context
     *
     * If [other] is null the returned matrix moves coordinates from this component's context to the root context
     */
    fun thisContextToOtherContext(other: GuiComponent?): Matrix4 {
        return _thisContextToOtherContext(other, Matrix4())
    }

    private fun _thisContextToOtherContext(other: GuiComponent?, matrix: Matrix4): Matrix4 {
        if (other == null) {
            component.parent?.geometry?._thisContextToOtherContext(null, matrix)
            transform.apply(matrix)
            return matrix
        }
        val mat = other.geometry.thisContextToOtherContext(null).invert()
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
    @JvmOverloads
    fun thisPosToOtherContext(other: GuiComponent?, pos: Vec2d = Vec2d.ZERO): Vec2d {
        return thisContextToOtherContext(other) * pos
    }

    fun calculateMouseOver(mousePos: Vec2d) {
        component.BUS.fire(GuiComponentEvents.PreMouseOverEvent(component, mousePos))
        val transformPos = transformFromParentContext(mousePos)
        this.mouseOver = false
        this.mouseOverNoOcclusion = false

        var occludeChildren = false
        var occludeSelf = false

        if (!component.isVisible || component.clipping.isPointClipped(transformPos)) {
            this.propagateClippedMouseOver()
        } else {
            component.relationships.components.asReversed().forEach { child ->
                child.geometry.calculateMouseOver(transformPos)
                if (occludeChildren) {
                    child.mouseOver = false // occlude the child position
                }
                if (child.mouseOver && child.geometry.componentOccludesMouseOver) {
                    // occlude all siblings below this component
                    occludeChildren = true
                    if(!child.geometry.componentPropagatesMouseOverToParent) {
                        // if the component occludes and also doesn't pass the mouseover up the chain, set a flag to
                        // make the parent component's mouseover occlude
                        occludeSelf = true
                    }
                }
                if (child.mouseOver && child.geometry.componentPropagatesMouseOverToParent) {
                    // propagate the mouseOver of children up to this, their parent
                    mouseOver = true
                }
                if (child.geometry.mouseOverNoOcclusion && child.geometry.componentPropagatesMouseOverToParent) {
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
        val event = component.BUS.fire(
                GuiComponentEvents.MouseOverEvent(component, transformPos, this.mouseOver, this.mouseOverNoOcclusion)
        )
        this.mouseOver = event.isOver
        this.mouseOverNoOcclusion = event.isOverNoOcclusion
    }

    private fun propagateClippedMouseOver() {
        this.mouseOver = false
        this.mouseOverNoOcclusion = false
        component.relationships.components.forEach { child -> child.geometry.propagateClippedMouseOver() }
    }

    /**
     * Calculates whether the given position is over this component specifically, ignoring any child components.
     */
    fun calculateOwnHover(mousePos: Vec2d): Boolean {
        val transformPos = transformFromParentContext(mousePos)
        return !component.clipping.isPointClipped(transformPos) &&
                transformPos.x >= 0 && transformPos.x <= size.x &&
                transformPos.y >= 0 && transformPos.y <= size.y
    }

}
