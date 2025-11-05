package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.core.util.vec

public class PastryProgressBar(posX: Int, posY: Int, width: Int): GuiLayer(posX, posY, width, 6) {
    private val bg = SpriteLayer(PastryTexture.progressbar, 0, 0, 0, 0)
    private val fg = SpriteLayer(PastryTexture.progressbarFill, 0, 0, 0, 0)

    public val progress_im: IMValueDouble = imDouble(1.0)
    public val progress: Double by progress_im

    init {
        this.add(bg, fg)
    }

    override fun prepareLayout() {
        super.prepareLayout()
        fg.size = vec(this.size.x * progress.clamp(0.0, 1.0), this.size.y)
    }

    override fun layoutChildren() {
        bg.size = this.size
    }
}