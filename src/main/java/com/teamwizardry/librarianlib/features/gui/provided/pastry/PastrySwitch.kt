package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.FixedSizeComponent
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

class PastrySwitch(posX: Int, posY: Int): PastryToggle(posX, posY, 11, 7) {
    private val background = SpriteLayer(PastryTexture.switchOff, 0, 0, 11, 7)
    private val handle = SpriteLayer(PastryTexture.switchHandle, 0, 0, 7, 7)
    private val switchOn = SpriteLayer(PastryTexture.switchOn, 0, 0, 11, 7)
    private val switchOnMask = GuiLayer(0, 0, 4, 7)

    override fun visualStateChanged(visualState: Boolean) {
        val duration = 2f
        if(visualState) {
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