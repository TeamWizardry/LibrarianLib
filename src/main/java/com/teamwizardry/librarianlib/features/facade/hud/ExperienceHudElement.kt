package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class ExperienceHudElement: HudElement(RenderGameOverlayEvent.ElementType.EXPERIENCE) {
    val xpBarFilled = HudElement()
    val xpText = HudElement()

    init {
        add(xpBarFilled, xpText)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        this.isVisible = mc.playerController.gameIsSurvivalOrAdventure()

        if (this.isVisible) {
            val left = root.widthi / 2 - 91
            val top = root.heighti - 29
            val barWidth = 182
            this.frame = rect(left, top, barWidth, 5)

            if (this.mc.player.xpBarCap() > 0) {
                val filled = (mc.player.experience * (barWidth + 1)).toInt()

                xpBarFilled.frame = rect(0, 0, filled, 5)
            }

            if (mc.player.experienceLevel > 0) {
                val textWidth = mc.fontRenderer.getStringWidth("" + mc.player.experienceLevel)
                val x = (root.widthi - textWidth) / 2
                val y = root.heighti - 35

                xpText.isVisible = true
                xpText.frame = rect(x - left - 1, y - top - 1, textWidth + 2, 9)
            } else {
                xpText.isVisible = false
            }
        }
    }
}