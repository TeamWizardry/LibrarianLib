package com.teamwizardry.librarianlib.common.container.internal

import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.container.SlotType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
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

    override fun onTake(thePlayer: EntityPlayer?, stack: ItemStack): ItemStack {
        if(type.onPickup(this, thePlayer, stack))
            return super.onTake(thePlayer, stack)
        return ItemStack.EMPTY
    }

    override fun onSlotChanged() {
        if (type.onSlotChange(this))
            super.onSlotChanged()
    }

    override fun onSlotChange(old: ItemStack, new: ItemStack) {
        if(type.onSlotChange(this, old, new))
            super.onSlotChange(old, new)
    }

    override fun putStack(stack: ItemStack) {
        if(type.putStack(this, stack))
            super.putStack(stack)
    }

    override fun getStack(): ItemStack {
        return if(visible) type.getStack(this, super.getStack()) else ItemStack.EMPTY
    }

    override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
        return if (visible) type.canTake(this, playerIn, stack, super.canTakeStack(playerIn)) else false
    }

    override fun canBeHovered(): Boolean {
        return if (visible) type.canHover(this) else false
    }

    override fun isItemValid(stack: ItemStack): Boolean {
        return if(visible) type.isValid(this, stack, super.isItemValid(stack)) else false
    }

    override fun getSlotStackLimit(): Int {
        return type.stackLimit(this, stack)
    }

    fun handleClick(container: ContainerBase, dragType: Int, clickType: ClickType?, player: EntityPlayer): Pair<Boolean, ItemStack> {
        return type.handleClick(this, container, dragType, clickType, player)
    }
}
