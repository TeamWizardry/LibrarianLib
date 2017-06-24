package com.teamwizardry.librarianlib.test.container

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileModInventoryTickable
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidEvent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.items.IItemHandler

/**
 * The block for our fluid tank.
 */
object BlockFluidTank : BlockModContainer("fluid_tank", Material.IRON) {

    override fun createTileEntity(world: World, state: IBlockState) = TEFluidTank()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!worldIn.isRemote) {
            if (!playerIn.getHeldItem(hand).hasCapability(FLUID_HANDLER_ITEM_CAPABILITY, null) || !(worldIn.getTileEntity(pos) as TEFluidTank).use(playerIn, hand))
                GuiHandler.open(FluidTankContainer.NAME, playerIn, pos)
        }
        return true
    } // TODO: right-click with a fluid container to fill (buckets, tanks, ...)
}

@Savable
@TileRegister("te_fluid_tank")
class TEFluidTank : TileModInventoryTickable(2) {

    @Save
    @CapabilityProvide(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST)
    val fluidHandler = MyFluidTank()

    @CapabilityProvide(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST)
    val worldInteraction: IItemHandler = WorldItemHandler(this, SLOT_IN, SLOT_OUT)

    init {
        fluidHandler.setTileEntity(this)
    }

    override fun tick() {
        if (!world.isRemote) {
            if (getStackInSlot(SLOT_OUT).isEmpty) {
                val s = getStackInSlot(SLOT_IN).copy()
                s.count = 1
                val cap = s.getCapability(FLUID_HANDLER_ITEM_CAPABILITY, null)
                if (cap != null) {
                    var operated = false
                    val toFill = fluidHandler.fill(cap.drain(CAPACITY, false), false)
                    if (toFill > 0) {
                        fluidHandler.fill(cap.drain(toFill, true), true)
                        operated = true
                    } else {
                        val toDrain = cap.fill(fluidHandler.drain(CAPACITY, false), false)
                        if (toDrain > 0) {
                            cap.fill(fluidHandler.drain(toDrain, true), true)
                            operated = true
                        }
                    }

                    if (operated) {
                        getStackInSlot(SLOT_IN).shrink(1)
                        insertOutput(cap.container, false)
                    }
                }
            }

            if (fluidHandler.dirty) {
                fluidHandler.dirty = false
                markDirty()
            }
        }
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
            if (slot == SLOT_IN && stack.hasCapability(FLUID_HANDLER_ITEM_CAPABILITY, null))
                super.insertItem(slot, stack, simulate) else stack

    /**
     * Insert an item in the output slot.
     */
    private fun insertOutput(stack: ItemStack, simulate: Boolean) =
            super.insertItem(SLOT_OUT, stack, simulate)

    /**
     * Use an IFluidHandlerItem on this TE.
     *
     * @param playerIn the player using the item
     * @param hand the hand in use
     * @return whether the operation was successful
     */
    fun use(playerIn: EntityPlayer, hand: EnumHand): Boolean {
        val itemStack = playerIn.getHeldItem(hand)
        val cap = itemStack.getCapability(FLUID_HANDLER_ITEM_CAPABILITY, null)!!
        var operated = false
        val toFill = fluidHandler.fill(cap.drain(CAPACITY, false), false)
        if (toFill > 0) {
            fluidHandler.fill(cap.drain(toFill, true), true)
            operated = true
        } else {
            val toDrain = cap.fill(fluidHandler.drain(CAPACITY, false), false)
            if (toDrain > 0) {
                cap.fill(fluidHandler.drain(toDrain, true), true)
                operated = true
            }
        }

        if (operated) {
            itemStack.shrink(1)
            if (itemStack.isEmpty) playerIn.setHeldItem(hand, cap.container)
            else if (!playerIn.inventory.addItemStackToInventory(cap.container))
                playerIn.dropItem(cap.container, false)
            return true
        } else return false
    }

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
         * Capacity
         */
        const val CAPACITY = 4000
    }
}

/**
 * Stores the fluid handling capability for our [TEFluidTank].
 * Most of it is copied with adaptation from [net.minecraftforge.fluids.FluidTank]
 */
@Savable
class MyFluidTank : IFluidTank, IFluidHandler {

    @Save
    var fluidStack: FluidStack? = null

    var dirty = false

    lateinit var tile: TileEntity

    val tankPropertiesImpl: Array<IFluidTankProperties> by lazy {
        arrayOf<IFluidTankProperties>(object : IFluidTankProperties {
            override fun canDrainFluidType(fluidStack: FluidStack) = this@MyFluidTank.canDrainFluidType(fluidStack)
            override fun getContents() = this@MyFluidTank.fluid
            override fun canFillFluidType(fluidStack: FluidStack) = this@MyFluidTank.canFillFluidType(fluidStack)
            override fun getCapacity() = this@MyFluidTank.capacity
            override fun canFill() = this@MyFluidTank.canFill()
            override fun canDrain() = this@MyFluidTank.canDrain()
        })
    }

    override fun getFluid() = fluidStack

    override fun getFluidAmount() = fluidStack?.amount ?: 0

    override fun getCapacity() = TEFluidTank.CAPACITY

    fun setTileEntity(tile: TileEntity) {
        this.tile = tile
    }

    override fun getInfo() = FluidTankInfo(this)

    override fun getTankProperties() = this.tankPropertiesImpl

    override fun fill(resource: FluidStack?, doFill: Boolean) =
            if (resource == null || !canFillFluidType(resource)) 0
            else fillInternal(resource, doFill)

    /**
     * Use this method to bypass the restrictions from [.canFillFluidType]
     * Meant for use by the owner of the tank when they have [set to false][.canFill].
     */
    fun fillInternal(resource: FluidStack?, doFill: Boolean): Int {
        if (resource == null || resource.amount <= 0) return 0

        if (!doFill) return if (fluidStack == null) Math.min(capacity, resource.amount)
        else if (!fluidStack!!.isFluidEqual(resource)) 0
        else Math.min(capacity - fluidStack!!.amount, resource.amount)

        if (fluidStack == null) {
            fluidStack = FluidStack(resource, Math.min(capacity, resource.amount))

            onContentsChanged()
            FluidEvent.fireEvent(FluidEvent.FluidFillingEvent(fluidStack, tile.world, tile.pos, this, fluidStack!!.amount))

            return fluidStack!!.amount
        }

        if (!fluidStack!!.isFluidEqual(resource)) return 0
        var filled = capacity - fluidStack!!.amount

        if (resource.amount < filled) {
            fluidStack!!.amount += resource.amount
            filled = resource.amount
        } else fluidStack!!.amount = capacity

        onContentsChanged()
        FluidEvent.fireEvent(FluidEvent.FluidFillingEvent(fluidStack, tile.world, tile.pos, this, filled))

        return filled
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        return if (!canDrainFluidType(resource)) null
        else drainInternal(resource, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        val fs = fluidStack
        return if (fs == null || !canDrainFluidType(fs)) null
        else drainInternal(maxDrain, doDrain)
    }

    /**
     * Use this method to bypass the restrictions from [.canDrainFluidType]
     * Meant for use by the owner of the tank when they have [.canDrain] set to false}.
     */
    internal fun drainInternal(resource: FluidStack?, doDrain: Boolean) =
            if (resource == null || !resource.isFluidEqual(fluidStack)) null
            else drainInternal(resource.amount, doDrain)

    /**
     * Use this method to bypass the restrictions from [.canDrainFluidType]
     * Meant for use by the owner of the tank when they have [.canDrain] set to false}.
     */
    internal fun drainInternal(maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (fluidStack == null || maxDrain <= 0) {
            return null
        }

        var drained = maxDrain
        if (fluidStack!!.amount < drained) {
            drained = fluidStack!!.amount
        }

        val stack = FluidStack(fluidStack!!, drained)
        if (doDrain) {
            fluidStack!!.amount -= drained
            if (fluidStack!!.amount <= 0) {
                fluidStack = null
            }

            onContentsChanged()
            FluidEvent.fireEvent(FluidEvent.FluidDrainingEvent(fluidStack, tile.world, tile.pos, this, drained))
        }
        return stack
    }

    /**
     * Whether this tank can be filled with [IFluidHandler]

     * @see IFluidTankProperties.canFill
     */
    fun canFill() = true

    /**
     * Whether this tank can be drained with [IFluidHandler]

     * @see IFluidTankProperties.canDrain
     */
    fun canDrain() = true

    /**
     * Returns true if the tank can be filled with this type of fluid.
     * Used as a filter for fluid types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever fill with this type of fluid.

     * @see IFluidTankProperties.canFillFluidType
     */
    fun canFillFluidType(fluid: FluidStack) = canFill()

    /**
     * Returns true if the tank can drain out this type of fluid.
     * Used as a filter for fluid types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever drain out this type of fluid.

     * @see IFluidTankProperties.canDrainFluidType
     */
    fun canDrainFluidType(fluid: FluidStack) = canDrain()

    private fun onContentsChanged() {
        dirty = true
    }

}
