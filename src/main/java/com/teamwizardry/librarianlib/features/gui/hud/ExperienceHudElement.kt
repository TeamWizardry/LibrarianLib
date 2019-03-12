package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent

class ExperienceHudElement: HudElement(RenderGameOverlayEvent.ElementType.EXPERIENCE) {
    val xpBar = GuiLayer()
    val xpBarFilled = GuiLayer()
    val xpText = GuiLayer()

    init {
        add(xpBar, xpBarFilled, xpText)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        if (mc.playerController.gameIsSurvivalOrAdventure()) {
            val cap = this.mc.player.xpBarCap()
            val left = root.widthi / 2 - 91

            if (cap > 0) {
                val barWidth = 182
                val filled = (mc.player.experience * (barWidth + 1)).toInt()
                val top = root.heighti - 29
                xpBar.frame = rect(left, top, barWidth, 5)

                if (filled > 0) {
                    xpBarFilled.isVisible = true
                    xpBarFilled.frame = rect(left, top, filled, 5)
                } else {
                    xpBarFilled.isVisible = false
                    xpBarFilled.frame = rect(left, top, 0, 5)
                }
            }

            if (mc.player.experienceLevel > 0) {
                val textWidth = mc.fontRenderer.getStringWidth("" + mc.player.experienceLevel)
                val x = (root.widthi - textWidth) / 2
                val y = root.heighti - 35

                xpText.isVisible = true
                xpText.frame = rect(x-1, y-1, textWidth+2, 9)
            } else {
                xpText.isVisible = false
            }
        } else {
            this.isVisible = false
        }
    }
}