package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.core.util.vec

public class PastrySwitch(posX: Int, posY: Int): PastryToggle(posX, posY, 11, 7) {
    public constructor(): this(0, 0)

    private val background = SpriteLayer(PastryTexture.switchOff, 0, 0, 11, 7)
    private val handle = SpriteLayer(PastryTexture.switchHandle, 0, 0, 7, 7)
    private val switchOn = SpriteLayer(PastryTexture.switchOn, 0, 0, 11, 7)
    private val switchOnMask = GuiLayer(0, 0, 4, 7)

    override fun visualStateChanged(visualState: Boolean) {
        val duration = 2f
        if (visualState) {
            handle.pos_rm.animate(vec(4, 0), duration)
            switchOnMask.size_rm.animate(vec(8, 7), duration)
        } else {
            handle.pos_rm.animate(vec(0, 0), duration)
            switchOnMask.size_rm.animate(vec(4, 7), duration)
        }
    }

    init {
        switchOnMask.clipToBounds = true
        switchOnMask.add(switchOn)
        this.add(background, switchOnMask, handle)
    }
}