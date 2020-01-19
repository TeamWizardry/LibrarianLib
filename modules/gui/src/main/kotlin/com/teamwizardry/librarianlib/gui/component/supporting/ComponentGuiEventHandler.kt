package com.teamwizardry.librarianlib.gui.component.supporting

import com.teamwizardry.librarianlib.gui.EnumMouseButton
import com.teamwizardry.librarianlib.gui.Key
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.math.Vec2d

interface IComponentGuiEvent {

    /**
     * Called when a key repeat is triggered in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyRepeat(key: Char, keyCode: Int)

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

    /**
     * Called when a key repeated is triggered in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    override fun keyRepeat(key: Char, keyCode: Int) {
        if (!component.isVisible) return
        component.BUS.fire(GuiComponentEvents.KeyRepeatEvent(key, keyCode))

        keysDown[Key[key, keyCode]] = true

        component.subComponents.forEach { child ->
            child.keyRepeat(key, keyCode)
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
        component.BUS.fire(GuiComponentEvents.KeyDownEvent(key, keyCode))

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
        keysDown[Key[key, keyCode]] = false // do this before so we don't have lingering keyDown entries

        component.BUS.fire(GuiComponentEvents.KeyUpEvent(key, keyCode))

        component.subComponents.forEach { child ->
            child.keyReleased(key, keyCode)
        }
    }
}
