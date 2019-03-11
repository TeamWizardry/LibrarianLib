package com.teamwizardry.librarianlib.features.gui.hud

import net.minecraftforge.client.event.RenderGameOverlayEvent

class HealthHudElement(type: RenderGameOverlayEvent.ElementType): HudElement(type) {
    override fun layoutChildren() {
        super.layoutChildren()
        this.frame = root.bounds
    }
}