package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import java.util.function.Supplier

public class LockingSlot(
    itemHandler: IItemHandler, index: Int,
    public var lock: ContainerLock
): FacadeSlot(itemHandler, index) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return !lock.isLocked() && super.isItemValid(stack)
    }

    override fun canTakeStack(playerIn: PlayerEntity): Boolean {
        return !lock.isLocked() && super.canTakeStack(playerIn)
    }
}
