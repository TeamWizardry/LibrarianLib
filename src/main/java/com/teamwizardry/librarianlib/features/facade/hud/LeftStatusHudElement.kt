package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent

class LeftStatusHudElement(type: RenderGameOverlayEvent.ElementType, val shown: () -> Boolean): HudElement(type) {
    val leftStack: GuiLayer = StackLayout.build().horizontal().reverse().alignRight().alignCenterY().layer()
    val rightStack: GuiLayer = StackLayout.build().horizontal().alignRight().alignCenterY().layer()

    init {
        add(leftStack, rightStack)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        leftStack.frame = rect(0, 0, 0, this.height)
        rightStack.frame = rect(this.width, 0, 0, this.height)
    }

    private var leftHeightBefore = 0
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        leftHeightBefore = GuiIngameForge.left_height
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Post) {
        super.hudEvent(e)
        val hotbarEdge = root.widthi / 2 - 91
        this.size = vec(81, GuiIngameForge.left_height - leftHeightBefore - 1)
        this.pos = vec(hotbarEdge, root.heighti - GuiIngameForge.left_height + 10)
        this.isVisible = shown()
    }
}