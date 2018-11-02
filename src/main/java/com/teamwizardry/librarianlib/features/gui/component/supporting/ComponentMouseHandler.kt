package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d

interface IComponentMouse {
    /**
     * The position of the mouse in this component's coordinate space
     */
    val mousePos: Vec2d

    /**
     * The [mousePos] value from the previous frame
     */
    val lastMousePos: Vec2d

    /**
     * True if the mouse lies [inside this component][shouldComputeMouseInsideFromBounds] or any subcomponents that
     * [propagate][mousePropagationType] their [inside][MousePropagationType.INSIDE] or
     * [over][MousePropagationType.OVER] status to their parent.
     *
     * [mouseOver] should generally be used to determine if the user is interacting with a component. This flag is
     * more useful for things such as detecting whether a component has been dragged onto another one, as the one being
     * dragged will likely occlude this one.
     */
    val mouseInside: Boolean

    /**
     * True if the mouse is over this component. This is a subset of the mouse being [inside][mouseInside] the
     * component. Even if the mouse is inside this component, another component may be above this one. However, if
     * the mouse is over this component it will also by definition be inside this component.
     * See [mousePropagationType] for more information about how it relates to subcomponents.
     *
     * This one should generally be used to determine if the user is interacting with a component. [mouseInside] is
     * more useful for things such as detecting whether a component has been dragged onto another one, as the one being
     * dragged will likely occlude this one.
     */
    val mouseOver: Boolean

    /**
     * Whether this component will block the mouse being over those behind it.
     *
     * If this is true, as it is by default, this component will "shade" those behind it, preventing the mouse
     * from counting as [over them][mouseOver]. The default value is true.
     */
    var isOpaqueToMouse: Boolean

    /**
     * This flag controls the effect the mouse being [inside][mouseInside] or [over][mouseOver] this component has on
     * its parent. The default value is [NONE][MousePropagationType.NONE]
     */
    var mousePropagationType: MousePropagationType

    /**
     * This flag controls whether the mouse being within this component's bounding rectangle should count as it being
     * [inside][mouseInside] or [over][mouseOver] this component. The default value is true.
     */
    var shouldComputeMouseInsideFromBounds: Boolean

    /**
     * Update the mousePos of this component and its children based on the given mouse position in its parent.
     */
    fun updateMouse(parentMousePos: Vec2d)

    /**
     * Update the mouseInside of this component and its children based on the current mousePos.
     *
     * @return whether the parent component should set its mouseInside to true
     */
    fun updateMouseInside(): Boolean

    /**
     * Update the mouseOver of this component and its children based on the current mousePos
     *
     * @param occluded whether the mouse has already been occluded
     * @return whether this component should occlude the mouse
     */
    fun updateMouseOver(occluded: Boolean): Boolean

    /**
     * Returns whether the parent component should set its mouseOver to true
     */
    fun shouldPropagateMouseOverTrue(): Boolean
}

enum class MousePropagationType {
    /**
     * The mouse being inside or over this component will not count as the mouse being inside/over its parent
     */
    NONE,
    /**
     * The mouse being [inside][IComponentMouse.mouseInside] this component will count as the mouse being inside its
     * parent, however the mouse being [over][IComponentMouse.mouseOver] this component will not count as the mouse
     * being over its parent.
     */
    INSIDE,
    /**
     * The mouse being [inside][IComponentMouse.mouseInside] or [over][IComponentMouse.mouseOver] this component will
     * count as the mouse being inside or over its parent.
     */
    OVER
}

class ComponentMouseHandler: IComponentMouse {
    lateinit var component: GuiComponent

    override var mousePos: Vec2d = Vec2d.ZERO
        private set
    override var lastMousePos: Vec2d = Vec2d.ZERO
        private set
    override var mouseInside: Boolean = false
        private set
    override var mouseOver: Boolean = false
        private set
    override var isOpaqueToMouse: Boolean = true
    override var mousePropagationType: MousePropagationType = MousePropagationType.NONE
    override var shouldComputeMouseInsideFromBounds: Boolean = true

    override fun updateMouse(parentMousePos: Vec2d) {
        this.mousePos = component.convertPointFromParent(parentMousePos)
        component.subComponents.forEach {
            it.updateMouse(this.mousePos)
        }
    }

    override fun updateMouseInside(): Boolean {
        var mouseInside = false
        if(shouldComputeMouseInsideFromBounds) {
            mouseInside = mousePos in component.bounds
        }
        for(child in component.subComponents.asReversed()) {
            mouseInside = mouseInside || child.updateMouseInside()
        }
        if(mouseInside && !this.mouseInside) {
            component.BUS.fire(GuiComponentEvents.MouseMoveInEvent(lastMousePos, mousePos))
        }
        if(!mouseInside && this.mouseInside) {
            component.BUS.fire(GuiComponentEvents.MouseMoveOutEvent(lastMousePos, mousePos))
        }
        this.mouseInside = mouseInside
        if(mousePropagationType != MousePropagationType.NONE) {
            return mouseInside
        } else {
            return false
        }
    }

    override fun updateMouseOver(occluded: Boolean): Boolean {
        @Suppress("NAME_SHADOWING") var occluded = occluded
        var mouseOver = false
        for(child in component.subComponents.reversed()) {
            occluded = occluded || child.updateMouseOver(occluded)
            mouseOver = mouseOver || child.shouldPropagateMouseOverTrue()
        }
        mouseOver = mouseOver || (!occluded && mouseInside)
        occluded = occluded || (mouseOver && isOpaqueToMouse)
        if(mouseOver && !this.mouseOver) {
            component.BUS.fire(GuiComponentEvents.MouseMoveEnterEvent(lastMousePos, mousePos))
        }
        if(!mouseOver && this.mouseOver) {
            component.BUS.fire(GuiComponentEvents.MouseMoveLeaveEvent(lastMousePos, mousePos))
        }
        return occluded
    }

    override fun shouldPropagateMouseOverTrue(): Boolean {
        return mouseOver && mousePropagationType == MousePropagationType.OVER
    }
}