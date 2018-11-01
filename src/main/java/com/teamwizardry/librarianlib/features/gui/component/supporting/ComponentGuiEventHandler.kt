package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.Key
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d
import org.lwjgl.input.Keyboard

interface IComponentGuiEvent {
    fun tick()

    /**
     * Called when the mouse is pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was pressed
     */
    fun mouseDown(button: EnumMouseButton)

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    fun mouseUp(button: EnumMouseButton)

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    fun mouseDrag(button: EnumMouseButton)

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    fun mouseWheel(direction: GuiComponentEvents.MouseWheelDirection)

    /**
     * Called when a key is pressed in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyPressed(key: Char, keyCode: Int)

    /**
     * Called when a key is released in the parent component.
     *
     * @param key The actual key that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyReleased(key: Char, keyCode: Int)
}

class ComponentGuiEventHandler: IComponentGuiEvent {
    lateinit var component: GuiComponent

    internal var mouseButtonsDownInside = arrayOfNulls<Vec2d?>(EnumMouseButton.values().size)
    internal var mouseButtonsDownOutside = arrayOfNulls<Vec2d?>(EnumMouseButton.values().size)
    internal var keysDown: MutableMap<Key, Boolean> = HashMap<Key, Boolean>().withDefault { false }

    override fun tick() {
        component.BUS.fire(GuiComponentEvents.ComponentTickEvent())
        component.subComponents.forEach { it.tick() }
    }

    /**
     * Called when the mouse is pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was pressed
     */
    override fun mouseDown(button: EnumMouseButton) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDownEvent(component.mousePos, button)).isCanceled())
            return

        if (component.mouseOver)
            mouseButtonsDownInside[button.ordinal] = component.mousePos
        else
            mouseButtonsDownOutside[button.ordinal] = component.mousePos

        component.subComponents.forEach { child ->
            child.mouseDown(button)
        }
    }

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    override fun mouseUp(button: EnumMouseButton) {
        if (!component.isVisible) return
        val posDownInside = mouseButtonsDownInside[button.ordinal]
        val posDownOutside = mouseButtonsDownOutside[button.ordinal]
        mouseButtonsDownInside[button.ordinal] = null
        mouseButtonsDownOutside[button.ordinal] = null

        if (component.BUS.fire(GuiComponentEvents.MouseUpEvent(component.mousePos, button)).isCanceled())
            return

        if (component.mouseOver) {
             if(posDownInside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickEvent(posDownInside, component.mousePos, button))
             } else if(posDownOutside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickDragInEvent(posDownOutside, component.mousePos, button))
             }
        } else {
            if(posDownInside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickDragOutEvent(posDownInside, component.mousePos, button))
            } else if(posDownOutside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickOutsideEvent(posDownOutside, component.mousePos, button))
            }
        }

        component.subComponents.forEach { child ->
            child.mouseUp(button)
        }
    }

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    override fun mouseDrag(button: EnumMouseButton) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDragEvent(component.mousePos, button)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.mouseDrag(button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    override fun mouseWheel(direction: GuiComponentEvents.MouseWheelDirection) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseWheelEvent(component.mousePos, direction)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.mouseWheel(direction)
        }
    }

    /**
     * Called when a key is pressed in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    override fun keyPressed(key: Char, keyCode: Int) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.KeyDownEvent(key, keyCode)).isCanceled())
            return

        keysDown.put(Key[key, keyCode], true)

        component.subComponents.forEach { child ->
            child.keyPressed(key, keyCode)
        }
    }

    /**
     * Called when a key is released in the parent component.
     *
     * @param key The actual key that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    override fun keyReleased(key: Char, keyCode: Int) {
        if (!component.isVisible) return
        keysDown.put(Key[key, keyCode], false) // do this before so we don't have lingering keyDown entries

        if (component.BUS.fire(GuiComponentEvents.KeyUpEvent(key, keyCode)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.keyReleased(key, keyCode)
        }
    }
}
