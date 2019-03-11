package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent

class ArmorHudElement: HudElement(RenderGameOverlayEvent.ElementType.ARMOR) {
    override fun layoutChildren() {
        super.layoutChildren()
        pos = vec(root.widthi / 2 - 91, root.heighti - GuiIngameForge.left_height)
    }
}