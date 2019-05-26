package com.teamwizardry.librarianlib.features.neogui.hud

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.kotlin.identityMapOf
import com.teamwizardry.librarianlib.features.kotlin.toIdentitySet
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableView
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

class PotionIconsHudElement: HudElement(RenderGameOverlayEvent.ElementType.POTION_ICONS) {
    val effects = identityMapOf<PotionEffect, EffectLayer>()
    var effectsByPotion: Map<Potion, EffectLayer> = identityMapOf<Potion, EffectLayer>().unmodifiableView()

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        val effects = this.mc.player.activePotionEffects

        val effectsToRemove = this.effects.keys.toIdentitySet()

        if (!effects.isEmpty()) {
            this.mc.textureManager.bindTexture(GuiContainer.INVENTORY_BACKGROUND)
            var goodIndex = 0
            var badIndex = 0

            for (potioneffect in effects.sortedDescending()) {
                val potion = potioneffect.potion

                if (!potion.shouldRenderHUD(potioneffect)) continue
                // Rebind in case previous renderHUDEffect changed texture
                if (potioneffect.doesShowParticles()) {
                    effectsToRemove.remove(potioneffect)
                    val layer = this.effects.getOrPut(potioneffect) {
                        EffectLayer(potioneffect).also { this.add(it) }
                    }
                    var drawnX = root.widthi
                    var drawnY = 1

                    if (this.mc.isDemo) {
                        drawnY += 15
                    }

                    if (potion.isBeneficial) {
                        ++goodIndex
                        drawnX -= 25 * goodIndex
                    } else {
                        ++badIndex
                        drawnX -= 25 * badIndex
                        drawnY += 26
                    }

                    layer.frame = rect(drawnX, drawnY, 24, 24)
                }
            }
        }

        effectsToRemove.forEach {
            this.effects.remove(it)?.removeFromParent()
        }

        effectsByPotion = this.effects.entries.associateTo(identityMapOf()) { (key, value) -> key.potion to value }.unmodifiableView()
    }

    class EffectLayer(val effect: PotionEffect): GuiLayer() {
        val icon = GuiLayer(3, 3, 18, 18)

        init {
            this.add(icon)
        }
    }
}