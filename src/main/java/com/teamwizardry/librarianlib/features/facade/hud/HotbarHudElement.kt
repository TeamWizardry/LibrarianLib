package com.teamwizardry.librarianlib.features.facade.hud

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHandSide
import net.minecraftforge.client.event.RenderGameOverlayEvent

class HotbarHudElement: HudElement(RenderGameOverlayEvent.ElementType.HOTBAR) {

    val background = GuiLayer()
    val slotHighlight = GuiLayer()
    val offhandBackground = GuiLayer()
    val slots = Array(9) { GuiLayer() }
    val offhandSlot = GuiLayer()
    val attackCooldown = GuiLayer()
    val attackCooldownFill = GuiLayer()

    init {
        this.add(background,
            slotHighlight,
            offhandBackground,
            *slots,
            offhandSlot,
            attackCooldown,
            attackCooldownFill)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        if (mc.playerController.isSpectator) {
            this.isVisible = false
            return
        }

        val entityplayer = this.mc.renderViewEntity as? EntityPlayer ?: return
        val enumhandside = entityplayer.primaryHand.opposite()

        val centerX = root.widthi / 2
        val hotbarWidth = 182
        val hotbarLeft = centerX - hotbarWidth/2
        val hotbarRight = centerX + hotbarWidth/2
        val hotbarTop = root.heighti - 22

        background.frame = rect(hotbarLeft, hotbarTop, hotbarWidth, 22)
        slotHighlight.frame = rect(hotbarLeft - 1 + entityplayer.inventory.currentItem * 20, hotbarTop - 1, 24, 22)

        if (!entityplayer.heldItemOffhand.isEmpty) {
            offhandBackground.isVisible = true
            offhandSlot.isVisible = true
            if (enumhandside == EnumHandSide.LEFT) {
                offhandBackground.frame = rect(hotbarLeft - 7 - 22, hotbarTop, 22, 22)
            } else {
                offhandBackground.frame = rect(hotbarRight + 7, hotbarTop, 22, 22)
            }
            offhandSlot.pos = offhandBackground.pos + vec(3, 3)
            offhandSlot.size = vec(16, 16)
        } else {
            offhandBackground.isVisible = false
            offhandSlot.isVisible = false
        }

        slots.forEachIndexed { i, slot ->
            slot.frame = rect(hotbarLeft + 3 + i*20, hotbarTop + 3, 16, 16)
        }

        attackCooldown.isVisible = false
        attackCooldownFill.isVisible = false
        if (this.mc.gameSettings.attackIndicator == 2)
        {
            val f1 = this.mc.player.getCooledAttackStrength(0.0F)

            if (f1 < 1.0F)
            {
                val i2 = root.heighti - 20
                var j2 = hotbarRight + 6

                if (enumhandside == EnumHandSide.RIGHT)
                {
                    j2 = hotbarLeft - 22
                }

                val k1 = (f1 * 19).toInt()
                attackCooldown.frame = rect(j2, i2, 18, 18)
                attackCooldownFill.frame = rect(j2, i2 + 18 - k1, 18, k1)
                attackCooldown.isVisible = true
                attackCooldownFill.isVisible = true
            }
        }
    }
}