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
    fun mouseDown(mousePos: Vec2d, button: EnumMouseButton)

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    fun mouseUp(mousePos: Vec2d, button: EnumMouseButton)

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton)

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    fun mouseWheel(mousePos: Vec2d, direction: GuiComponentEvents.MouseWheelDirection)

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

/**
 * TODO: Document file ComponentGuiEventHandler
 *
 * Created by TheCodeWarrior
 */
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
    override fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        val transformedPos = component.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDownEvent(transformedPos, button)).isCanceled())
            return

        if (component.mouseOver)
            mouseButtonsDownInside[button.ordinal] = transformedPos
        else
            mouseButtonsDownOutside[button.ordinal] = transformedPos

        component.subComponents.forEach { child ->
            child.mouseDown(transformedPos, button)
        }
    }

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    override fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        val transformedPos = component.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        val posDownInside = mouseButtonsDownInside[button.ordinal]
        val posDownOutside = mouseButtonsDownOutside[button.ordinal]
        mouseButtonsDownInside[button.ordinal] = null
        mouseButtonsDownOutside[button.ordinal] = null

        if (component.BUS.fire(GuiComponentEvents.MouseUpEvent(transformedPos, button)).isCanceled())
            return

        if (component.mouseOver) {
             if(posDownInside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickEvent(posDownInside, transformedPos, button))
             } else if(posDownOutside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickDragInEvent(posDownOutside, transformedPos, button))
             }
        } else {
            if(posDownInside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickDragOutEvent(posDownInside, transformedPos, button))
            } else if(posDownOutside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickOutsideEvent(posDownOutside, transformedPos, button))
            }
        }

        component.subComponents.forEach { child ->
            child.mouseUp(transformedPos, button)
        }
    }

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    override fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton) {
        val transformedPos = component.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDragEvent(transformedPos, button)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.mouseDrag(transformedPos, button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    override fun mouseWheel(mousePos: Vec2d, direction: GuiComponentEvents.MouseWheelDirection) {
        val transformedPos = component.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseWheelEvent(transformedPos, direction)).isCanceled())
            return

        component.subComponents.forEach { child ->
            child.mouseWheel(transformedPos, direction)
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
