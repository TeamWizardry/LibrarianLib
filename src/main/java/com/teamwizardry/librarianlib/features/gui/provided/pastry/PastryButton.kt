package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

class PastryButton @JvmOverloads constructor(posX: Int, posY: Int, width: Int, height: Int = 12): GuiComponent(posX, posY, width, height) {
    private val sprite = SpriteLayer(PastryTexture.button, 0, 0, width, height)
    val label = TextLayer(4, 2, width-8, height-4)
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
        this.hoverCursor = LibCursor.POINT
        label.wrap = false
        label.maxLines = 1
        label.truncate = true
        this.add(sprite, label)
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
        label.size = this.size - vec(8, 4)
    }
}