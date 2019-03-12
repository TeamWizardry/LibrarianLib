package com.teamwizardry.librarianlib.features.gui.hud

import net.minecraftforge.client.event.RenderGameOverlayEvent

class ChatHudElement: HudElement(RenderGameOverlayEvent.ElementType.CHAT) {
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        e as RenderGameOverlayEvent.Chat
    }
}