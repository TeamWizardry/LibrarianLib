package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect

/**
 * TODO: Document file ComponentDebugPanel
 *
 * Created by TheCodeWarrior
 */
class ComponentDebugPanel : GuiComponent(0, 0, 100, 100) {
    val rect = ComponentRect(0, 0, 1, 1)
    init {
        add(rect)
    }

    @Hook
    fun tick(e: GuiComponentEvents.ComponentTickEvent) {
        rect.size = size
    }
}
