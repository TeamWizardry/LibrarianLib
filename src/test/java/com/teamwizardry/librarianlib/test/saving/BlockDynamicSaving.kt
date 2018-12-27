package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.kotlin.sendMessage
import com.teamwizardry.librarianlib.features.saving.Savable
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class BlockDynamicSaving : BlockMod("saving_dynamic", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as BlockDynamicSavingTile
        if (!worldIn.isRemote) {

//            var existing = te.map[facing] ?: TestType(0, 0)
//            te.map[facing] = if(playerIn.isSneaking) {
//                TestType2(existing.a+1, existing.b-1, ((existing as? TestType2)?.c ?: -1) + 1)
//            } else {
//                TestType(existing.a-1, existing.b+1)
//            }

            val existing = te.foo[0] ?: TestType(0, 0)
            te.foo[0] = if(playerIn.isSneaking) {
                TestType2(existing.a+1, existing.b-1, ((existing as? TestType2)?.c ?: -1) + 1)
            } else {
                TestType(existing.a-1, existing.b+1)
            }

            te.markDirty()
        } else {
            playerIn.sendMessage("" + te.foo)//"HashMap<EnumFacing, Int>: [" + te.map.map { "${it.key}:${it.array}" }.joinToString(", ") + "]")
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return BlockDynamicSavingTile()
    }
}

@Savable
open class TestType(val a: Int = 0, val b: Int = 0) {
    override fun toString(): String {
        return "TestType(a=$a, b=$b)"
    }
}
@Savable
open class TestType2(a: Int, b: Int, val c: Int) : TestType(a, b) {
    override fun toString(): String {
        return "TestType2(a=$a, b=$b, c=$c)"
    }
}
