package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.SlotType
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

/**
 * Created by TheCodeWarrior
 */
class SlotBase(inventory: IInventory, index: Int) : Slot(inventory, index, 0, 0) {
    var type = SlotType.BASIC
}
