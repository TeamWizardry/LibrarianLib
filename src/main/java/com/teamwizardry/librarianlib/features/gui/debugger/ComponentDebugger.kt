package com.teamwizardry.librarianlib.features.gui.debugger

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.helpers.vec

/**
 * Manages the debug panel
 */
class ComponentDebugger : GuiComponent(0, 0, 0, 0) {
    val debugPanel = ComponentDebugPanel()

    @Hook
    fun tick(e: GuiComponentEvents.ComponentTickEvent) {
        val parentSize = parent?.size ?: vec(0, 0)
        debugPanel.pos = vec(0, Math.max(0.0, parentSize.y - debugPanel.size.y))
        debugPanel.size = vec(parentSize.y, Math.min(parentSize.y, debugPanel.size.y))
    }
}
