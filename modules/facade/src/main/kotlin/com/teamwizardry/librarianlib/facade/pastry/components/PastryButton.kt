package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.Pastry
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.math.Align2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
class PastryButton @JvmOverloads constructor(
        buttonText: String = "",
        posX: Int, posY: Int, width: Int? = null, height: Int = Pastry.lineHeight,
        callback: (() -> Unit)? = null
) : PastryActivatedControl(posX, posY, width ?: 0, height) {

    class ClickEvent(): Event()

    val label = PastryLabel(4, 0, buttonText)

    init {
        if(width == null) {
            this.width = label.width + 8
        }
//        label.align = Align2d.CENTER TODO
    }

    private val sprite = SpriteLayer(PastryTexture.button, 0, 0, this.widthi, height)
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
        label.wrap = false
//        label.maxLines = 1 TODO
//        label.truncate = true TODO
        label.text = buttonText
        if(callback != null)
            this.BUS.hook<ClickEvent> {
                callback()
            }
        this.add(sprite, label)

//        clipToBounds = true TODO
    }

    override fun activate() {
        this.BUS.fire(ClickEvent())
        pressed = true
    }

    override fun activationEnd() {
        pressed = false
    }

    @Hook
    private fun mouseClick(e: GuiLayerEvents.MouseClick) {
        this.BUS.fire(ClickEvent())
    }

    @Hook
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if(this.mouseOver) {
            pressed = true
            mouseDown = true
        }
    }

    @Hook
    private fun mouseUp(e: GuiLayerEvents.MouseUp) {
        pressed = false
        mouseDown = false
    }

    @Hook
    private fun mouseLeave(e: GuiLayerEvents.MouseMoveOff) {
        pressed = false
    }
    @Hook
    private fun mouseEnter(e: GuiLayerEvents.MouseMoveOver) {
        pressed = mouseDown
    }

    override fun layoutChildren() {
        sprite.size = this.size
        label.size = this.size - vec(8, 0)
    }
}