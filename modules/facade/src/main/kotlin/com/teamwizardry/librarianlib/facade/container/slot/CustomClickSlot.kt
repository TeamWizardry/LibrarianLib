package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

public interface CustomClickSlot {
    /**
     * Optionally override the default [slot click handling][ScreenHandler.onSlotClick]. Returning true will bypass the
     * default click behavior.
     */
    public fun handleClick(
        container: ScreenHandler,
        mouseButton: Int,
        clickType: SlotActionType,
        player: PlayerEntity
    ): Boolean
}