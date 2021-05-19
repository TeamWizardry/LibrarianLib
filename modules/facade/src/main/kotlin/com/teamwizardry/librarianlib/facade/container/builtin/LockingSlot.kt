package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

public class LockingSlot(
    inventory: Inventory, index: Int,
    public var lock: ContainerLock
): FacadeSlot(inventory, index) {

    override fun canInsert(stack: ItemStack): Boolean {
        return !lock.isLocked() && super.canInsert(stack)
    }

    override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {
        return !lock.isLocked() && super.canTakeItems(playerEntity)
    }
}
