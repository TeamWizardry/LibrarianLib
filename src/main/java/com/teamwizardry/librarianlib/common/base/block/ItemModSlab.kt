package com.teamwizardry.librarianlib.common.base.block

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * The default implementation for an IModBlock wrapper Item that gets registered as an IVariantHolder.
 */
@Suppress("LeakingThis")
open class ItemModSlab(block: Block) : ItemModBlock(block) {

    override fun onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos.offset(facing), facing, stack)) {
            val iblockstate = worldIn.getBlockState(pos)

            if (iblockstate.block == block) {
                val half = iblockstate.getValue(BlockModSlab.STATE)

                if (facing == EnumFacing.UP && half == SlabType.BOTTOM || facing == EnumFacing.DOWN && half == SlabType.TOP) {
                    val iblockstate1 = block.defaultState.withProperty(BlockModSlab.STATE, SlabType.FULL)
                    val axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos)

                    if (axisalignedbb !== Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb!!.offset(pos)) && worldIn.setBlockState(pos, iblockstate1)) {
                        val soundtype = block.soundType
                        worldIn.playSound(playerIn, pos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)
                        --stack.stackSize
                    }

                    return EnumActionResult.SUCCESS
                }
            }

            return if (this.tryPlace(playerIn, stack, worldIn, pos.offset(facing))) EnumActionResult.SUCCESS else super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
        } else {
            return EnumActionResult.FAIL
        }
    }

    @SideOnly(Side.CLIENT)
    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean {
        val blockpos = pos
        val state = worldIn.getBlockState(pos)

        if (state.block == block &&
                side == EnumFacing.UP && state.getValue(BlockModSlab.STATE) == SlabType.BOTTOM
                || side == EnumFacing.DOWN && state.getValue(BlockModSlab.STATE) == SlabType.TOP) {
            return true
        }

        val offPos = pos.offset(side)
        val offState = worldIn.getBlockState(offPos)
        return if (offState.block == block) true else super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack)
    }

    private fun tryPlace(player: EntityPlayer, stack: ItemStack, worldIn: World, pos: BlockPos): Boolean {
        val iblockstate = worldIn.getBlockState(pos)

        if (iblockstate.block == block) {
            val iblockstate1 = block.defaultState.withProperty(BlockModSlab.STATE, SlabType.FULL)
            val axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos)

            if (axisalignedbb !== Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb!!.offset(pos)) && worldIn.setBlockState(pos, iblockstate1, 11)) {
                val soundtype = block.soundType
                worldIn.playSound(player, pos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)
                --stack.stackSize
            }

            return true
        }

        return false
    }
}

