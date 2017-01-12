package com.teamwizardry.librarianlib.common.container.builtin

import com.teamwizardry.librarianlib.common.container.InventoryWrapper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

/**
 * Created by TheCodeWarrior
 */
object BaseWrappers {

    fun player(inv: InventoryPlayer) = InventoryWrapperPlayer(InvWrapper(inv))

    fun inventory(inv: IInventory) = InventoryWrapper(InvWrapper(inv))

    fun stacks(inv: IItemHandler) = InventoryWrapper(inv)

    class InventoryWrapperPlayer(inv: IItemHandler) : InventoryWrapper(inv) {

        val armor = slots[36..39]
        val head = slots[39]
        val chest = slots[38]
        val legs = slots[37]
        val feet = slots[36]

        val hotbar = slots[0..8]
        val main = slots[9..35]
        val offhand = slots[40]

    }

}
