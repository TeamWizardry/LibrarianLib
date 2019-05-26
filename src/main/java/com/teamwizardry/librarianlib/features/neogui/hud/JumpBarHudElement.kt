package com.teamwizardry.librarianlib.features.neogui.hud

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class JumpBarHudElement: HudElement(RenderGameOverlayEvent.ElementType.JUMPBAR) {
    val bar = GuiLayer()
    val barFilled = GuiLayer()

    init {
        this.add(bar, barFilled)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        val charge = mc.player.horseJumpPower
        val barWidth = 182
        val x = (root.widthi / 2) - 91
        val filled = (charge * (barWidth + 1)).toInt()
        val top = root.heighti - 32 + 3

        bar.frame = rect(x, top, barWidth, 5)
        barFilled.frame = rect(x, top, filled, 5)
        barFilled.isVisible = filled > 0
    }
}