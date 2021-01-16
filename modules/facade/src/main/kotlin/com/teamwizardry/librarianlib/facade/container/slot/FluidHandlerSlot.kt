package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

public class FluidHandlerSlot(public val handler: IFluidHandler, public val slot: Int): FluidSlot() {
    override val capacity: Int
        get() = handler.getTankCapacity(slot)

    override fun getActualFluid(): FluidStack {
        return handler.getFluidInTank(slot)
    }
}