package com.teamwizardry.librarianlib.common.container.builtin

import com.teamwizardry.librarianlib.common.container.InventoryWrapper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory

/**
 * Created by TheCodeWarrior
 */
object BaseWrappers {

    fun player(inv: InventoryPlayer) = InventoryWrapperPlayer(inv)

    fun inventory(inv: IInventory) = InventoryWrapper(inv)

    class InventoryWrapperPlayer(inv: InventoryPlayer) : InventoryWrapper<InventoryPlayer>(inv) {

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
