package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import kotlin.math.min

public open class GhostSlot(inventory: Inventory, index: Int) : FacadeSlot(inventory, index), CustomClickSlot, TransferSlot {
    override fun getMaxItemCount(): Int {
        return 1
    }

    protected open fun acceptGhostStack(stack: ItemStack) {
        if(!canInsert(stack)) return
        val ghostStack = stack.copy()
        ghostStack.count = min(getMaxItemCount(ghostStack), ghostStack.count)
        setStack(ghostStack)
    }

    override fun handleClick(
        container: ScreenHandler,
        mouseButton: Int,
        clickType: SlotActionType,
        player: PlayerEntity
    ): Boolean {
        val ghostStack = container.cursorStack
        val isValid = canInsert(ghostStack)

        when(clickType) {
            SlotActionType.PICKUP -> {
                if(mouseButton == 0 && isValid) {
                    acceptGhostStack(ghostStack)
                }
                if(mouseButton == 1) {
                    acceptGhostStack(ItemStack.EMPTY)
                }
            }
            else -> {}
        }
        return true
    }

    override fun isStackSimilar(stack: ItemStack): Boolean = ItemStack.canCombine(stack, this.stack)

    override fun transferIntoSlot(transfer: TransferState) {
        if(isStackSimilar(transfer.stack)) {
            transfer.foundSpot = true
            transfer.halt = true
        }
        if(this.stack.isEmpty && canInsert(transfer.stack)) {
            acceptGhostStack(transfer.stack)
            transfer.foundSpot = true
            transfer.halt = true
        }
    }
}