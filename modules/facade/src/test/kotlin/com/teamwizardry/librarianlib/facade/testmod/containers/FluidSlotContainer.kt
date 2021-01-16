package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.kotlin.getOrNull
import com.teamwizardry.librarianlib.facade.container.slot.FluidHandlerSlot
import com.teamwizardry.librarianlib.facade.container.slot.FluidSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainerData
import com.teamwizardry.librarianlib.prism.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.items.ItemStackHandler

class FluidSlotContainer(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestContainer<FluidSlotContainer.Data>(Data::class.java, windowId, player, pos) {
    val ioSlots: SlotManager = SlotManager(data.ioInventory)

    val tankSlot: FluidSlot = FluidHandlerSlot(data.tank, 0)

    init {
        addSlot(tankSlot)

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(ioSlots.slots)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(ioSlots.slots)
        createTransferRule().from(ioSlots.slots).into(playerSlots.main).into(playerSlots.hotbar)
    }

    class Data: TestContainerData() {
        @Save
        val ioInventory: ItemStackHandler = object : ItemStackHandler(2) {
            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent
            }
        }

        @Save
        val tank: FluidTank = FluidTank(4000)

        override fun tick() {
            if(tank.space > 0) {
                val cap = ioInventory.getStackInSlot(0)
                    .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                    .getOrNull()
                if(cap != null) {
                    val drainable = cap.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                    val fillable = tank.fill(drainable, IFluidHandler.FluidAction.SIMULATE)
                    if(fillable > 0 && fillable <= drainable.amount) {
                        tank.fill(cap.drain(fillable, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
                    }
                    ioInventory.setStackInSlot(0, cap.container)
                }
            }

            if(!tank.isEmpty) {
                val cap = ioInventory.getStackInSlot(1)
                    .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                    .getOrNull()
                if(cap != null) {
                    val drainable = tank.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                    val fillable = cap.fill(drainable, IFluidHandler.FluidAction.SIMULATE)
                    if(fillable > 0 && fillable <= drainable.amount) {
                        cap.fill(tank.drain(fillable, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)
                    }
                    ioInventory.setStackInSlot(1, cap.container)
                }
            }
        }
    }
}