package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.Key
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * TODO: Document file ComponentGuiEventHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentGuiEventHandler(private val component: GuiComponent) {
    internal var mouseButtonsDown = BooleanArray(EnumMouseButton.values().size)
    internal var keysDown: MutableMap<Key, Boolean> = HashMap<Key, Boolean>().withDefault({ false })

    fun tick() {
        component.BUS.fire(GuiComponentEvents.ComponentTickEvent(component))
        component.relationships.forEachChild { it.guiEventHandler.tick() }
    }

    /**
     * Called when the mouse is pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was pressed
     */
    fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDownEvent(component, mousePos, button)).isCanceled())
            return

        if (component.mouseOver)
            mouseButtonsDown[button.ordinal] = true

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseDown(mousePos, button)
        }
    }

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        val wasDown = mouseButtonsDown[button.ordinal]
        mouseButtonsDown[button.ordinal] = false

        if (component.BUS.fire(GuiComponentEvents.MouseUpEvent(component, mousePos, button)).isCanceled())
            return

        if (component.mouseOver && wasDown) {
            component.BUS.fire(GuiComponentEvents.MouseClickEvent(component, mousePos, button))
            // don't return here, if a click was handled we should still handle the mouseUp
        }

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseUp(mousePos, button)
        }
    }

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDragEvent(component, mousePos, button)).isCanceled())
            return

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseDrag(mousePos, button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    fun mouseWheel(mousePos: Vec2d, direction: GuiComponentEvents.MouseWheelDirection) {
        val mousePos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseWheelEvent(component, mousePos, direction)).isCanceled())
            return

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseWheel(mousePos, direction)
        }
    }

    /**
     * Called when a key is pressed in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyPressed(key: Char, keyCode: Int) {
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.KeyDownEvent(component, key, keyCode)).isCanceled())
            return

        keysDown.put(Key[key, keyCode], true)

        component.relationships.forEachChild { child ->
            child.guiEventHandler.keyPressed(key, keyCode)
        }
    }

    /**
     * Called when a key is released in the parent component.
     *
     * @param key The actual key that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyReleased(key: Char, keyCode: Int) {
        if (!component.isVisible) return
        keysDown.put(Key[key, keyCode], false) // do this before so we don't have lingering keyDown entries

        if (component.BUS.fire(GuiComponentEvents.KeyUpEvent(component, key, keyCode)).isCanceled())
            return

        component.relationships.forEachChild { child ->
            child.guiEventHandler.keyReleased(key, keyCode)
        }
    }
}
