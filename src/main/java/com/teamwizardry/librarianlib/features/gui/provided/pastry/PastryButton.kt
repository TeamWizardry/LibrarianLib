package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer

class PastryButton(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    private val sprite = SpriteLayer(PastryTexture.button, 0, 0, width, height)
    private var pressed = false

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(this.mouseOver)
            pressed = true
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        pressed = false
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseMoveOutEvent) {
        pressed = false
    }


    override fun layoutChildren() {
        sprite.size = this.size
    }
}