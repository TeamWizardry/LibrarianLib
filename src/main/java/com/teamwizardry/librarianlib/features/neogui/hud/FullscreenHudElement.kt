package com.teamwizardry.librarianlib.features.neogui.hud

import net.minecraftforge.client.event.RenderGameOverlayEvent

class FullscreenHudElement(type: RenderGameOverlayEvent.ElementType): HudElement(type) {
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        this.frame = root.bounds
    }
}
