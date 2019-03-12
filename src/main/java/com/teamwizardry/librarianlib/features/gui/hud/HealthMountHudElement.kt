package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.ceilInt
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import kotlin.math.min

class HealthMountHudElement: HudElement(RenderGameOverlayEvent.ElementType.HEALTHMOUNT) {
    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)

        val player = mc.renderViewEntity as EntityPlayer
        val mount = player.ridingEntity as EntityLivingBase // the event is only ever fired if this is true

        val hearts = min(30, (mount.maxHealth + 0.5F).toInt() / 2)
        val rows = ceilInt(hearts / 10.0)

        val hotbarEdge = root.widthi / 2 + 91
        this.size = vec(81, rows * 10 - 1)
        this.pos = vec(hotbarEdge-width, root.heighti - GuiIngameForge.right_height - (rows-1)*10)
    }
}