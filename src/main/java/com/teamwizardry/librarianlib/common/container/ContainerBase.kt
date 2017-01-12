package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.container.internal.SlotBase
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by TheCodeWarrior
 */
@SaveInPlace
abstract class ContainerBase(val player: EntityPlayer) {

    lateinit var impl: ContainerImpl // hopefully people don't do anything screwy.

    protected fun addSlots(wrapper: InventoryWrapper) {
        wrapper.slotArray.forEach {
            allSlots.add(it)
        }
    }

    val allSlots = mutableListOf<SlotBase>()

    fun addContainerSlots() {
        allSlots.forEach { impl.addSlotToContainer(it) }
    }
}
