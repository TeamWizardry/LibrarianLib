package com.teamwizardry.librarianlib.test.container

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockModDirectional
import com.teamwizardry.librarianlib.features.base.block.TileModInventory
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * The block for our powered machine.
 * It's an object, because, duh! Why not.
 */
object BlockPoweredMachine : BlockModDirectional("powered_machine", Material.IRON) {

    override fun hasTileEntity(state: IBlockState?) = true

    /**
     * Called when the block is right-clicked.
     * We tell the [GuiHandler] to open our container for the player.
     */
    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        GuiHandler.open(PoweredMachineContainer.NAME, playerIn, pos)
        return true
    }

    /**
     * Returning our custom TE here.
     */
    override fun createTileEntity(world: World, state: IBlockState) = TEPoweredMachine()
}

/**
 * The TileEntity for our [BlockPoweredMachine].
 */
@Savable
@TileRegister("te_powered_machine")
class TEPoweredMachine : TileModInventory(2), ITickable {

    /**
     * Forge Energy handler for this machine.
     *
     * This could use [net.minecraftforge.energy.EnergyStorage] if/when we make a serializer for it.
     */
    @Save
    @CapabilityProvide(DOWN, UP, NORTH, SOUTH, WEST, EAST)
    val energyHandler = MyEnergyStorage()

    @CapabilityProvide(DOWN, UP, NORTH, SOUTH, WEST, EAST)
    val worldInteraction: IItemHandler = WorldItemHandler(this, SLOT_IN, SLOT_OUT)

    /**
     * The current operation in progress, if any.
     */
    @Save
    var currentOperation: MachineProgress? = null

    /**
     * Called once per tick.
     *
     * We use this to handle the logic of our machine :
     *  - generate energy
     *  - if there's no current operation, we create a new one if possible (aka if we have an input)
     *  - otherwise we tick the current operation, and consume power
     *      then check whether it is completed and try to output to our output slot
     *      once everything has been output, we remove the current recipe
     */
    override fun update() {
        if (!world.isRemote) {
            this.generateEnergy()

            val op = currentOperation

            if (op != null) {
                if (energyHandler.extractEnergy(CONSUMPTION, true) == CONSUMPTION && op.tick())
                    energyHandler.extractEnergy(CONSUMPTION, false)

                if (op.completed) {
                    op.result = insertOutput(currentOperation!!.result, false)
                    if (op.result.isEmpty) currentOperation = null
                }
            }

            if (currentOperation == null && !getStackInSlot(SLOT_IN).isEmpty)
                currentOperation = MachineProgress(extractItem(SLOT_IN, 1, false))

            if (energyHandler.dirty || currentOperation?.dirty ?: false) {
                energyHandler.dirty = false
                currentOperation?.dirty = false
                markDirty()
            }
        }
    }

    /**
     * This will check whether we should generate energy, and whether we can (glowstone block on top).
     * If we can, we'll add some energy.
     */
    private fun generateEnergy() {
        if (!world.isRemote && energyHandler.canReceive() && world.getBlockState(pos.up()).block == Blocks.GLOWSTONE) energyHandler.receiveEnergy((CONSUMPTION * 1.5).toInt(), false)
    }

    /**
     * We don't want to let the output slot be inserted into (by the player or by automation).
     */
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return if (slot == SLOT_IN) super.insertItem(slot, stack, simulate) else stack
    }

    /**
     * Insert an item in the output slot.
     */
    private fun insertOutput(stack: ItemStack, simulate: Boolean) =
            super.insertItem(SLOT_OUT, stack, simulate)

    companion object {
        /**
         * ID of INPUT slot
         */
        const val SLOT_IN = 0
        /**
         * ID of OUTPUT slot
         */
        const val SLOT_OUT = 1
        /**
         * Power consumption rate
         */
        const val CONSUMPTION = 30
    }
}

/**
 * Stores a [TEPoweredMachine]'s operation progress.
 * This one will basically grow the input [ItemStack] by 1.
 */
@Savable
class MachineProgress(@Save var iss: ItemStack) {

    @Deprecated(message = "For serializers")
    constructor(): this(ItemStack.EMPTY)

    var dirty = false

    /**
     * Total time, in ticks, required for the operation to complete.
     */
    private val totalTime = 100

    /**
     * The current progression of this operation.
     */
    @Save
    private var currentProgress = 0
        set(value) {
            if (value != field) {
                field = value
                dirty = true
            }
        }

    /**
     * The result of this operation.
     * The result will get increased in init.
     */
    @Save
    var result = iss.copy()!!

    init {
        result.grow(1)
    }

    /**
     * Tick this operation. Used to make it progress.
     */
    fun tick(): Boolean {
        val r = !completed
        if (r) ++currentProgress
        return r
    }

    /**
     * Whether this operation is completed and should reward it's result.
     */
    val completed: Boolean
        get() = currentProgress >= totalTime

    /**
     * Current progress of this operation, ranging [0f, 1f].
     */
    val progress: Float
        get() = Math.min(currentProgress / totalTime.toFloat(), 1f)
}

/**
 * Stores the energy handling capability for our [TEPoweredMachine].
 *
 * (Yeah this currently, as of 3.0, can't be done via an anonymous class)
 */
@Savable
class MyEnergyStorage : IEnergyStorage {

    var dirty = false

    @Save
    private var energy = 0
        set(value) {
            if (value != field) {
                field = value
                dirty = true
            }
        }

    private val capacity = 10000
    private val input = 100
    private val output = 100

    override fun canExtract() = energy > 0

    override fun getMaxEnergyStored() = capacity

    override fun getEnergyStored() = energy

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        val r = Math.min(Math.min(maxExtract, output), energy)
        if (!simulate) energy -= r
        return r
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        val r = Math.min(Math.min(maxReceive, input), capacity - energy)
        if (!simulate) energy += r
        return r
    }

    override fun canReceive() = energy < capacity
}

/**
 * Used to restrict world interactions/automation.
 */
class WorldItemHandler(val te: IItemHandlerModifiable, val inputSlots: IntRange, val outputSlots: IntRange) : IItemHandler {

    constructor(te: IItemHandlerModifiable, inputSlot: Int, outputSlot: Int): this(te, inputSlot..inputSlot, outputSlot..outputSlot)

    /**
     * Nothing to change, pure delegation.
     */
    override fun getSlotLimit(slot: Int) = te.getSlotLimit(slot)

    /**
     * We wanna make sure only to allow automatic insertion to the input slot.
     */
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
            if (slot in inputSlots) te.insertItem(slot, stack, simulate) else stack

    /**
     * Nothing to change, pure delegation.
     */
    override fun getStackInSlot(slot: Int) = te.getStackInSlot(slot)

    /**
     * Nothing to change, pure delegation.
     */
    override fun getSlots() = te.slots

    /**
     * We wanna make sure only to allow automatic extraction from the output slot.
     */
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
            if (slot in outputSlots) te.extractItem(slot, amount, simulate) else ItemStack.EMPTY
}
