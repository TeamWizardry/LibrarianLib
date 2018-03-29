package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.Key
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.math.Vec2d
import org.lwjgl.input.Keyboard

/**
 * TODO: Document file ComponentGuiEventHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentGuiEventHandler(private val component: GuiComponent) {
    internal var mouseButtonsDownInside = arrayOfNulls<Vec2d?>(EnumMouseButton.values().size)
    internal var mouseButtonsDownOutside = arrayOfNulls<Vec2d?>(EnumMouseButton.values().size)
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
        val transformedPos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDownEvent(component, transformedPos, button)).isCanceled())
            return

        if (component.mouseOver)
            mouseButtonsDownInside[button.ordinal] = transformedPos
        else
            mouseButtonsDownOutside[button.ordinal] = transformedPos

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseDown(transformedPos, button)
        }
    }

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        val transformedPos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        val posDownInside = mouseButtonsDownInside[button.ordinal]
        val posDownOutside = mouseButtonsDownOutside[button.ordinal]
        mouseButtonsDownInside[button.ordinal] = null
        mouseButtonsDownOutside[button.ordinal] = null

        if (component.BUS.fire(GuiComponentEvents.MouseUpEvent(component, transformedPos, button)).isCanceled())
            return

        if (component.mouseOver) {
             if(posDownInside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickEvent(component, posDownInside, transformedPos, button))
             } else if(posDownOutside != null) {
                 component.BUS.fire(GuiComponentEvents.MouseClickDragInEvent(component, posDownOutside, transformedPos, button))
             }
        } else {
            if(posDownInside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickDragOutEvent(component, posDownInside, transformedPos, button))
            } else if(posDownOutside != null) {
                component.BUS.fire(GuiComponentEvents.MouseClickOutsideEvent(component, posDownOutside, transformedPos, button))
            }
        }

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseUp(transformedPos, button)
        }
    }

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton) {
        val transformedPos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseDragEvent(component, transformedPos, button)).isCanceled())
            return

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseDrag(transformedPos, button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    fun mouseWheel(mousePos: Vec2d, direction: GuiComponentEvents.MouseWheelDirection) {
        val transformedPos = component.geometry.transformFromParentContext(mousePos)
        if (!component.isVisible) return
        if (component.BUS.fire(GuiComponentEvents.MouseWheelEvent(component, transformedPos, direction)).isCanceled())
            return

        component.relationships.forEachChild { child ->
            child.guiEventHandler.mouseWheel(transformedPos, direction)
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
