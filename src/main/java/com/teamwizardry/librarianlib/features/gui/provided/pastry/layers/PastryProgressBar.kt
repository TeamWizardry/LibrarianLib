package com.teamwizardry.librarianlib.features.gui.provided.pastry.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp

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