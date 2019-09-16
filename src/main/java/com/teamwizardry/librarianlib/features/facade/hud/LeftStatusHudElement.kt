package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent

class LeftStatusHudElement(type: RenderGameOverlayEvent.ElementType, val shown: () -> Boolean): HudElement(type) {

    private var leftHeightBefore = 0
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        runLayout()

        this.isVisible = shown()
        if(this.isVisible) {
            GuiIngameForge.left_height += bottom.heighti
        }

        leftHeightBefore = GuiIngameForge.left_height
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Post) {
        super.hudEvent(e)
        val hotbarEdge = root.widthi / 2 - 91
        this.size = vec(81, GuiIngameForge.left_height - leftHeightBefore - 1)
        this.pos = vec(hotbarEdge, root.heighti - GuiIngameForge.left_height + 10)
        if(this.isVisible) {
            GuiIngameForge.left_height += top.heighti
        }
    }
}