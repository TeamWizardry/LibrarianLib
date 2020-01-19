package com.teamwizardry.librarianlib.gui.hud

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent

class RightStatusHudElement(type: RenderGameOverlayEvent.ElementType, val shown: () -> Boolean): HudElement(type) {

    private var rightHeightBefore = 0
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        runLayout()

        this.isVisible = shown()

        if(this.isVisible) {
            GuiIngameForge.right_height += bottom.heighti
        }

        rightHeightBefore = GuiIngameForge.right_height
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Post) {
        super.hudEvent(e)
        val hotbarEdge = root.widthi / 2 + 91
        this.size = vec(81, GuiIngameForge.right_height - rightHeightBefore - 1)
        this.pos = vec(hotbarEdge-width, root.heighti - GuiIngameForge.right_height + 10)
        if(this.isVisible) {
            GuiIngameForge.right_height += top.heighti
        }
        runLayout()
    }
}