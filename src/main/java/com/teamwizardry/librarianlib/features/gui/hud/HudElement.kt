package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent

open class HudElement(val type: RenderGameOverlayEvent.ElementType): GuiComponent() {
    protected val mc: Minecraft get() = Minecraft.getMinecraft()

    open fun hudEvent(e: RenderGameOverlayEvent.Post) {

    }

    open fun hudEvent(e: RenderGameOverlayEvent.Pre) {

    }
}