package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.math.MathHelper

class PastrySlider(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {
    private val bg = SpriteLayer(PastryTexture.progressbar, 0, 0, 0, 0)
    private val fg = SpriteLayer(PastryTexture.progressbarFill, 0, 0, 0, 0)

    val progress_im: IMValueDouble = IMValueDouble(0.0)
    var progress: Double by progress_im

    private var mouseDown = false

    init {
        fg.clipToBounds = true
        this.add(bg, fg)

        BUS.hook<GuiComponentEvents.MouseClickEvent> {
            if (mouseOver)
                setMouseProgress(mousePos)
        }

        BUS.hook<GuiComponentEvents.MouseDownEvent> {
            if (mouseOver)
                mouseDown = true
        }

        BUS.hook<GuiComponentEvents.MouseDragEvent> {
            if (mouseDown) {
                setMouseProgress(mousePos)
            }
        }

        BUS.hook<GuiComponentEvents.MouseUpEvent> {
            mouseDown = false
        }
    }

    private fun setMouseProgress(mousePos: Vec2d) {
        progress = MathHelper.clamp(mousePos.x / width, 0.0, 1.0)
        BUS.fire(PastryToggle.StateChangeEvent())
    }

    fun updateProgress(progress: Double) {
        this.progress = progress
        BUS.fire(PastryToggle.StateChangeEvent())
    }

    override fun draw(partialTicks: Float) {
        super.draw(partialTicks)
        fg.size = vec(this.size.x * progress.clamp(0.0, 1.0), this.size.y)
    }

    override fun layoutChildren() {
        bg.size = this.size
    }
}