package com.teamwizardry.librarianlib.facade.compat.jei

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import mezz.jei.api.gui.handlers.IGhostIngredientHandler
import net.minecraft.client.renderer.Rectangle2d
import net.minecraft.item.ItemStack

private typealias IGhostTarget<I> = IGhostIngredientHandler.Target<I>

internal object JeiFacadeGhostIngredientHandler : IGhostIngredientHandler<FacadeContainerScreen<*>> {

    override fun <I : Any?> getTargets(
        gui: FacadeContainerScreen<*>,
        ingredient: I,
        doStart: Boolean
    ): List<IGhostTarget<I>> {
        val targets = mutableListOf<IGhostTarget<I>>()

        if (ingredient is ItemStack) {
            @Suppress("UNCHECKED_CAST")
            targets.addAll(gui.container.inventorySlots
                .filterIsInstance<GhostSlot>()
                .filter { !it.disableJeiGhostIntegration }
                .map { GhostSlotTarget(gui, it) as IGhostTarget<I> }
            )
        }

        return targets
    }

    override fun onComplete() {
    }

    private class GhostSlotTarget(
        private val gui: FacadeContainerScreen<*>,
        private val slot: GhostSlot
    ) : IGhostTarget<ItemStack> {
        override fun getArea(): Rectangle2d {
            return Rectangle2d(gui.guiLeft + slot.xPos, gui.guiTop + slot.yPos, 16, 16)
        }

        override fun accept(ingredient: ItemStack) {
            gui.jei.acceptJeiGhostStack(slot, ingredient)
        }
    }
}