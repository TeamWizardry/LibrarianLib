package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class JumpBarHudElement: HudElement(RenderGameOverlayEvent.ElementType.JUMPBAR) {
    val filled = GuiLayer()

    init {
        this.add(filled)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        val charge = mc.player.horseJumpPower
        val barWidth = 182
        val x = (width / 2) - 91
        val filled = (charge * (barWidth + 1)).toInt()
        val top = height - 32 + 3

        this.frame = rect(x, top, barWidth, 5)
        this.filled.frame = rect(0, 0, filled, 5)
        this.filled.isVisible = filled > 0
    }
}