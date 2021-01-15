package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.item.ItemStack

public interface CustomClickSlot {
    /**
     * Optionally override the default [slot click handling][Container.slotClick]. Returning a non-null value will
     * bypass the default click behavior, while returning null will fall through to the default click behavior.
     */
    public fun handleClick(
        container: Container,
        mouseButton: Int,
        clickType: ClickType,
        player: PlayerEntity
    ): ItemStack?
}