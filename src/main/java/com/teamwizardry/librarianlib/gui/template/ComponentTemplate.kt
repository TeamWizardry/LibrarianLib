package com.teamwizardry.librarianlib.gui.template

import com.teamwizardry.librarianlib.gui.GuiComponent

open class ComponentTemplate<T : GuiComponent<*>> {

    protected var result: T? = null

    fun get(): T {
        return result
    }

}
