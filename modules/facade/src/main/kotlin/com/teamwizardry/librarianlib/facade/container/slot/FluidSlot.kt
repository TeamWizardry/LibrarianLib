package com.teamwizardry.librarianlib.facade.container.slot

import net.minecraft.inventory.container.Container
import net.minecraftforge.fluids.FluidStack

/**
 * A fluid slot that will sync to the client. There is no built-in way for the client to send interactions to the
 * server, but there's little precedent for directly interacting with tank readouts anyway.
 */
public abstract class FluidSlot {
    public var slotNumber: Int = 0

    /**
     * The cached contents of the slot. This is updated on the server during [Container.detectAndSendChanges] and then
     * synced to the client.
     */
    public var fluid: FluidStack = FluidStack.EMPTY
        private set

    public abstract val capacity: Int

    public abstract fun getActualFluid(): FluidStack

    public fun setCachedContents(stack: FluidStack) {
        fluid = stack
    }
}