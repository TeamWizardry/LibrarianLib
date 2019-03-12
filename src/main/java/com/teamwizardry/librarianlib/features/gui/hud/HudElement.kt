package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
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