package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.component.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.helpers.vec
import com.teamwizardry.librarianlib.kotlin.clamp

class PastryProgressBar(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private val bg = SpriteLayer(PastryTexture.progressbar, 0, 0, 0, 0)
    private val fg = SpriteLayer(PastryTexture.progressbarFill, 0, 0, 0, 0)

    val progress_im: IMValueDouble = IMValueDouble(0.0)
    val progress: Double by progress_im

    init {
        fg.clipToBounds = true
        this.add(bg, fg)
    }

    override fun draw(partialTicks: Float) {
        super.draw(partialTicks)
        fg.size = vec(this.size.x * progress.clamp(0.0, 1.0), this.size.y)
    }

    override fun layoutChildren() {
        bg.size = this.size
    }
}