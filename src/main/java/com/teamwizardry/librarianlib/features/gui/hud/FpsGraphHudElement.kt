package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.MathHelper

class FpsGraphHudElement: HudElement(RenderGameOverlayEvent.ElementType.FPS_GRAPH) {
    val box = GuiLayer()
    val lines = Array(240) { GuiLayer() }
    val fpsLine30 = GuiLayer()
    val fpsText30 = GuiLayer()
    val fpsLine60 = GuiLayer()
    val fpsText60 = GuiLayer()
    val fpsLineLimit = GuiLayer()

    init {
        this.add(box, *lines, fpsLine30, fpsText30, fpsLine60, fpsText60, fpsLineLimit)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.showLagometer) {
            this.isVisible = true
            val frametimer = this.mc.getFrameTimer()
            val tailIndex = frametimer.lastIndex
            val headIndex = frametimer.index
            val frames = frametimer.frames
            var cursorIndex = tailIndex
            var lineIndex = 0
            this.box.frame = rect(0, root.heighti - 60, 240, 60)

            while (cursorIndex != headIndex) {
                val value = frametimer.getLagometerValue(frames[cursorIndex], 30)
                lines[239 - lineIndex].frame = rect(lineIndex, root.heighti - value + 1, 1, value - 1)
                ++lineIndex
                cursorIndex = frametimer.parseIndex(cursorIndex + 1)
            }

            fpsText60.frame = rect(1, root.heighti - 30 + 1, 13, 9)
            fpsLine60.frame = rect(0, root.heighti - 30, 240, 1)
            fpsText30.frame = rect(1, root.heighti - 60 + 1, 13, 9)
            fpsLine30.frame = rect(0, root.heighti - 60, 240, 1)

            if (this.mc.gameSettings.limitFramerate <= 120) {
                fpsLineLimit.isVisible = true
                fpsLineLimit.frame = rect(0, root.heighti - 60 + this.mc.gameSettings.limitFramerate / 2, 240, 1)
            } else {
                fpsLineLimit.isVisible = false
            }
        } else {
            this.isVisible = false
        }
    }
}