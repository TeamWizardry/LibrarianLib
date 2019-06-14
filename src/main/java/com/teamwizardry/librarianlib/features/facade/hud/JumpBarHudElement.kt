package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class JumpBarHudElement: HudElement(RenderGameOverlayEvent.ElementType.JUMPBAR) {
    val barFilled = HudElement()

    init {
        this.add(barFilled)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        val charge = mc.player.horseJumpPower
        val barWidth = 182
        val x = (root.widthi / 2) - 91
        val filled = (charge * (barWidth + 1)).toInt()
        val top = root.heighti - 32 + 3

        this.frame = rect(x, top, barWidth, 5)
        barFilled.frame = rect(0, 0, filled, 5)
        barFilled.isVisible = filled > 0
    }
}