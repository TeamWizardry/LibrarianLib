package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent

open class HudElement(val type: RenderGameOverlayEvent.ElementType): GuiLayer() {
    protected val mc: Minecraft get() = Minecraft.getMinecraft()

    open fun hudEvent(e: RenderGameOverlayEvent.Post) {

    }

    open fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        this.isVisible = true
    }
}