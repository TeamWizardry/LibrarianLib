package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer

class PastryButton(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    private val sprite = SpriteLayer(PastryTexture.button, 0, 0, width, height)
    private var mouseDown = false
    private var pressed = false
        set(value) {
            field = value
            if(value)
                sprite.sprite = PastryTexture.buttonPressed
            else
                sprite.sprite = PastryTexture.button
        }

    init {
        this.add(sprite)
    }

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(this.mouseOver) {
            pressed = true
            mouseDown = true
        }
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        pressed = false
        mouseDown = false
    }

    @Hook
    private fun mouseLeave(e: GuiComponentEvents.MouseLeaveEvent) {
        pressed = false
    }
    @Hook
    private fun mouseEnter(e: GuiComponentEvents.MouseEnterEvent) {
        pressed = mouseDown
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}