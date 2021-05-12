package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

public open class GhostSlot(itemHandler: IItemHandler, index: Int) : FacadeSlot(itemHandler, index), CustomClickSlot, TransferSlot {
    /**
     * Whether to disable JEI ghost slot integration
     */
    public var disableJeiGhostIntegration: Boolean = false

    override fun getSlotStackLimit(): Int {
        return 1
    }

    protected open fun acceptGhostStack(stack: ItemStack) {
        val ghostStack = stack.copy()
        ghostStack.count = min(slotStackLimit, ghostStack.count)
        this.putStack(ghostStack)
    }

    override fun handleClick(
        container: ScreenHandler,
        mouseButton: Int,
        clickType: SlotActionType,
        player: PlayerEntity
    ): ItemStack? {
        val ghostStack = player.inventory.itemStack
        val isValid = isItemValid(ghostStack)

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
        return ItemStack.EMPTY
    }

    override fun isStackSimilar(stack: ItemStack): Boolean = ScreenHandler.canStacksCombine(stack, this.stack)

    override fun transferIntoSlot(transfer: TransferState) {
        if(isStackSimilar(transfer.stack)) {
            transfer.foundSpot = true
            transfer.halt = true
        }
        if(this.stack.isEmpty && isItemValid(transfer.stack)) {
            acceptGhostStack(transfer.stack)
            transfer.foundSpot = true
            transfer.halt = true
        }
    }

    public open fun acceptJeiGhostStack(stack: ItemStack) {
        if(isItemValid(stack)) {
            acceptGhostStack(stack)
        }
    }
}