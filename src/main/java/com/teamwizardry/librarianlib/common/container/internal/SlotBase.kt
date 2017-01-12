package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.SlotType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by TheCodeWarrior
 */
class SlotBase(handler: IItemHandler, index: Int) : SlotItemHandler(handler, index, 0, 0) {
    var type = SlotType.BASIC
    var visible = true
    var lastVisible = visible

    override fun getStack(): ItemStack? {
        return if(visible) super.getStack() else null
    }

    override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
        return if(visible) super.canTakeStack(playerIn) else false
    }

    override fun canBeHovered(): Boolean {
        return if(visible) super.canBeHovered() else false
    }

    override fun isItemValid(stack: ItemStack?): Boolean {
        return if(visible) super.isItemValid(stack) else false
    }
}
