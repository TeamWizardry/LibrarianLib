package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents

open class DraggableComponent(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    private var mightDrag = false
    private var isDragging = false

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        mightDrag = mouseOver
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        mightDrag = false
    }

    @Hook
    private fun mouseMove(e: GuiComponentEvents.MouseMoveEvent) {

    }

    override fun draw(partialTicks: Float) {
        // nop
    }
}
