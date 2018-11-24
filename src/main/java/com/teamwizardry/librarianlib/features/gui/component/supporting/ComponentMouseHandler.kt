package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.RootComponent
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.util.Collections

interface IComponentMouse {
    /**
     * The position of the mouse in this component's coordinate space
     */
    val mousePos: Vec2d

    /**
     * The [mousePos] value from the previous frame
     */
    val lastMousePos: Vec2d

    val mouseHit: MouseHit?

    val mouseOver: Boolean

    /**
     * The set of mouse buttons currently being pressed
     */
    val pressedButtons: Set<EnumMouseButton>

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
    var propagateMouse: Boolean

    /**
     * This flag controls whether the mouse being within this component's bounding rectangle should count as it being
     * [inside][mouseInside] or [over][mouseOver] this component. The default value is true.
     */
    var disableMouseCollision: Boolean

    /**
     * Update the mousePos of this component and its children based on the given mouse position in its parent.
     */
    fun updateMouse(parentMousePos: Vec2d)

    /**
     * Update the mouseInside of this component and its children based on the current mousePos.
     *
     * @return whether the parent component should set its mouseInside to true
     */
    fun updateHits(root: RootComponent, parentZ: Double)

    /**
     * Update the mouseOver of this component and its children based on the current mousePos
     *
     * @param occluded whether the mouse has already been occluded
     * @return whether this component should occlude the mouse
     */
    fun propagateHits()

    fun mouseDown(button: EnumMouseButton)

    fun mouseUp(button: EnumMouseButton)

    fun mouseWheel(direction: GuiComponentEvents.MouseWheelDirection)
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
    override val mouseOver: Boolean
        get() {
            val mouseHit = this.component.mouseHit
            val topHit = (this.component.rootComponent as? RootComponent)?.topMouseHit

            return mouseHit != null && mouseHit >= topHit
        }
    override var mouseHit: MouseHit? = null
        private set

    private val buttonsDownOver = mutableMapOf<EnumMouseButton, Boolean>()
    override val pressedButtons: Set<EnumMouseButton> = Collections.unmodifiableSet(buttonsDownOver.keys)

    override var isOpaqueToMouse: Boolean = true
    override var propagateMouse: Boolean = true
    override var disableMouseCollision: Boolean = false

    override fun updateMouse(parentMousePos: Vec2d) {
        this.lastMousePos = mousePos
        this.mousePos = GuiComponentEvents.CalculateMousePositionEvent(
            component.convertPointFromParent(parentMousePos)
        ).mousePos
        if(lastMousePos.squareDist(mousePos) > 0.1 * 0.1) {
            if(mouseOver && pressedButtons.isNotEmpty())
                component.BUS.fire(GuiComponentEvents.MouseDragEvent())
            component.BUS.fire(GuiComponentEvents.MouseMoveEvent())
        }
        component.subComponents.forEach {
            it.updateMouse(this.mousePos)
        }
        this.mouseHit = null
    }

    override fun updateHits(root: RootComponent, parentZ: Double) {
        val zIndex = parentZ + component.zIndex
        if(!component.disableMouseCollision && component.isPointInBounds(component.mousePos)) {
            val mouseHit = MouseHit(this.component, zIndex)
            this.mouseHit = mouseHit
            if(component.isOpaqueToMouse && mouseHit > root.topMouseHit) {
                root.topMouseHit = mouseHit
            }
        } else {
            this.mouseHit = null
        }

        for(child in component.subComponents) {
            if(child.isVisible) {
                child.updateHits(root, zIndex)
            }
        }
    }

    override fun propagateHits() {
        for(child in component.subComponents) {
            child.propagateHits()
            if(child.isVisible && child.propagateMouse && child.mouseHit > this.mouseHit) {
                this.mouseHit = child.mouseHit
            }
        }
    }

    override fun mouseDown(button: EnumMouseButton) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDownEvent(button)).isCanceled())
            return

        buttonsDownOver[button] = mouseOver

        component.subComponents.forEach { child ->
            child.mouseDown(button)
        }
    }

    override fun mouseUp(button: EnumMouseButton) {
        if (!component.isVisible) return

        val wasOver = buttonsDownOver[button] ?: false
        buttonsDownOver[button] = false

        if (component.BUS.fire(GuiComponentEvents.MouseUpEvent(button)).isCanceled())
            return

        if (component.mouseOver) {
            if(wasOver) {
                component.BUS.fire(GuiComponentEvents.MouseClickEvent(button))
            } else {
                component.BUS.fire(GuiComponentEvents.MouseClickDragInEvent(button))
            }
        } else {
            if(wasOver) {
                component.BUS.fire(GuiComponentEvents.MouseClickDragOutEvent(button))
            } else {
                component.BUS.fire(GuiComponentEvents.MouseClickOutsideEvent(button))
            }
        }

        component.subComponents.forEach { child ->
            child.mouseUp(button)
        }
    }

    override fun mouseWheel(direction: GuiComponentEvents.MouseWheelDirection) {
        if (!component.isVisible) return

        if (component.BUS.fire(GuiComponentEvents.MouseWheelEvent(direction)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.mouseWheel(direction)
        }
    }


}