package com.teamwizardry.librarianlib.features.neogui.hud

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.neogui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent

class RightStatusHudElement(type: RenderGameOverlayEvent.ElementType, val shown: () -> Boolean): HudElement(type) {
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

    private var rightHeightBefore = 0
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        rightHeightBefore = GuiIngameForge.right_height
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Post) {
        super.hudEvent(e)
        val hotbarEdge = root.widthi / 2 + 91
        this.size = vec(81, GuiIngameForge.right_height - rightHeightBefore - 1)
        this.pos = vec(hotbarEdge-width, root.heighti - GuiIngameForge.right_height + 10)
        this.isVisible = shown()
    }
}