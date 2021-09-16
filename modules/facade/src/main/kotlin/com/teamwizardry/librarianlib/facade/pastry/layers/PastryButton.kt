package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.Pastry
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager

public class PastryButton: PastryActivatedControl {
    public val label: PastryLabel
    private val sprite: SpriteLayer

    // text + position + size + optional callback
    @JvmOverloads
    public constructor(buttonText: String, posX: Int, posY: Int, width: Int, height: Int, callback: Runnable? = null)
            : super(posX, posY, width, height) {
        sprite = SpriteLayer(PastryTexture.button, 0, 0, width, height)

        label = PastryLabel(4, 0, buttonText)
        label.textAlignment = TextLayoutManager.Alignment.CENTER
        label.enableDefaultTruncation()
        if (callback != null)
            this.BUS.hook<ClickEvent> {
                callback.run()
            }
        this.add(sprite, label)
    }

    // text + position + width + optional callback
    @JvmOverloads
    public constructor(buttonText: String, posX: Int, posY: Int, width: Int, callback: Runnable? = null)
            : this(buttonText, posX, posY, width, Pastry.lineHeight, callback)

    // text + position + optional callback
    @JvmOverloads
    public constructor(buttonText: String, posX: Int, posY: Int, callback: Runnable? = null)
            : this(buttonText, posX, posY, 0, Pastry.lineHeight, callback) {
        this.width = label.width + 8
    }

    // just text + optional callback
    @JvmOverloads
    public constructor(buttonText: String, callback: Runnable? = null): this(buttonText, 0, 0, callback)

    public class ClickEvent(): Event()

    private var mouseDown = false

    private var pressed = false
        set(value) {
            field = value
            if (value)
                sprite.sprite = PastryTexture.buttonPressed
            else
                sprite.sprite = PastryTexture.button
        }

    override fun activate() {
        this.BUS.fire(ClickEvent())
        pressed = true
    }

    override fun activationEnd() {
        pressed = false
    }

    @JvmOverloads
    public fun fitLabel(fitType: TextFit = TextFit.BOTH) {
        label.textAlignment = TextLayoutManager.Alignment.LEFT
        label.fitToText(fitType)
        label.textAlignment = TextLayoutManager.Alignment.CENTER
        this.size = label.size + vec(8, 0)
    }

    @Hook
    private fun mouseClick(e: GuiLayerEvents.MouseClick) {
        this.BUS.fire(ClickEvent())
    }

    @Hook
    private fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if (this.mouseOver) {
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