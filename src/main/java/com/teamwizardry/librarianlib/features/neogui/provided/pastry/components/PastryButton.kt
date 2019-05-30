package com.teamwizardry.librarianlib.features.neogui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
class PastryButton @JvmOverloads constructor(
        buttonText: String = "",
        posX: Int, posY: Int, width: Int = Minecraft().fontRenderer.getStringWidth(buttonText) + 10, height: Int = 12,
        callback: (() -> Unit)? = null
) : PastryActivatedControl(posX, posY, width, height) {
    class ClickEvent(): Event()

    val label = TextLayer(4, 2, width-8, height-4)
    private val sprite = SpriteLayer(PastryTexture.button, 0, 0, width, height)
    private var mouseDown = false

    var pressed = false
        set(value) {
            field = value
            if(value)
                sprite.sprite = PastryTexture.buttonPressed
            else
                sprite.sprite = PastryTexture.button
        }

    init {
        this.cursor = LibCursor.POINT
        label.wrap = false
        label.maxLines = 1
        label.truncate = true
        label.text = buttonText
        if(callback != null)
            this.BUS.hook<ClickEvent> {
                callback()
            }
        this.add(sprite, label)

        clipToBounds = true
    }

    override fun activate() {
        this.BUS.fire(ClickEvent())
        pressed = true
    }

    override fun activationEnd() {
        pressed = false
    }

    @Hook
    private fun mouseClick(e: GuiComponentEvents.MouseClickEvent) {
        this.BUS.fire(ClickEvent())
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