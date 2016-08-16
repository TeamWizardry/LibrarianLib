package com.teamwizardry.librarianlib.gui.template

import com.teamwizardry.librarianlib.gui.GuiComponent

open class ComponentTemplate<T : GuiComponent<*>>(protected val result: T) {

    fun get(): T {
        return result
    }

}
