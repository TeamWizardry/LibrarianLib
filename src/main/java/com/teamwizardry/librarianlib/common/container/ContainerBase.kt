package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.builtin.BasicTransferRule
import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
@SaveInPlace
abstract class ContainerBase(val player: EntityPlayer) {

    lateinit var impl: ContainerImpl // hopefully people don't do anything screwy, cause lateinit would cause problems.

    protected fun addSlots(wrapper: InventoryWrapper) {
        wrapper.slotArray.forEach {
            allSlots.add(it)
        }
    }

    /**
     * Create a new basic transfer rule
     */
    fun transferRule(): BasicTransferRule {
        val rule = BasicTransferRule()
        transferRules.add(rule)
        return rule
    }

    fun transferStackInSlot(slot: SlotBase): ItemStack {
        val stack = slot.stack
        if (stack.isEmpty) return stack
        for(rule in transferRules) {
            if(rule.shouldApply(slot)) {
                val result = rule.putStack(stack)
                if(result !== stack) {
                    slot.putStack(result)
                    return if(result === stack) result else ItemStack.EMPTY
                }
            }
        }
        return ItemStack.EMPTY
    }

    val transferRules = mutableListOf<ITransferRule>()
    val allSlots = mutableListOf<SlotBase>()

    fun addContainerSlots() {
        allSlots.forEach { impl.addSlotToContainer(it) }
    }

}
