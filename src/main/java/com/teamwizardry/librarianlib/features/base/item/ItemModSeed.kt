package com.teamwizardry.librarianlib.features.base.item

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.BlockDispenser
import net.minecraft.block.state.IBlockState
import net.minecraft.dispenser.BehaviorDefaultDispenseItem
import net.minecraft.dispenser.IBlockSource
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable

/**
 * @author WireSegal
 * Created at 10:12 AM on 12/19/17.
 */
class ItemModSeed(name: String, val crops: Block) : ItemMod(name), IPlantable {
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val itemstack = player.getHeldItem(hand)
        val state = worldIn.getBlockState(pos)
        if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && state.block.canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.crops.defaultState)

            if (player is EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger(player, pos.up(), itemstack)
            }

            itemstack.shrink(1)
            return EnumActionResult.SUCCESS
        } else {
            return EnumActionResult.FAIL
        }
    }

    override fun getPlantType(world: IBlockAccess, pos: BlockPos): EnumPlantType {
        return EnumPlantType.Crop
    }

    override fun getPlant(world: IBlockAccess, pos: BlockPos): IBlockState {
        return this.crops.defaultState
    }

    fun makeSeedBehavior(): ItemModSeed {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, BehaviourSeeds(crops))
        return this
    }

    class BehaviourSeeds(val placeState: IBlockState) : BehaviorDefaultDispenseItem() {

        constructor(block: Block) : this(block.defaultState)

        public override fun dispenseStack(par1IBlockSource: IBlockSource, par2ItemStack: ItemStack): ItemStack {
            val facing = par1IBlockSource.blockState.getValue(BlockDispenser.FACING)
            val pos = par1IBlockSource.blockPos.offset(facing)
            val world = par1IBlockSource.world

            if (world.isAirBlock(pos) && placeState.block.canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, placeState)
                par2ItemStack.shrink(1)
                return par2ItemStack
            }

            return super.dispenseStack(par1IBlockSource, par2ItemStack)
        }

    }
}
