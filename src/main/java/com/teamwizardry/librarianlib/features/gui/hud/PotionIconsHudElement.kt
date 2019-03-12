package com.teamwizardry.librarianlib.features.gui.hud

import net.minecraftforge.client.event.RenderGameOverlayEvent

class PotionIconsHudElement(type: RenderGameOverlayEvent.ElementType): HudElement(type) {
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        this.frame = root.bounds
    }
}