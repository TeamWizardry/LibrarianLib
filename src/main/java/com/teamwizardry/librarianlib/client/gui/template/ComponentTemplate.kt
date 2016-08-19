package com.teamwizardry.librarianlib.client.gui.template

import com.teamwizardry.librarianlib.client.gui.GuiComponent

open class ComponentTemplate<T : GuiComponent<*>>(protected val result: T) {

    fun get(): T {
        return result
    }

}
