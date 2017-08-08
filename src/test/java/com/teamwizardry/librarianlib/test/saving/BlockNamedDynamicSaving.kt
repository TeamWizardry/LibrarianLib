package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.kotlin.sendMessage
import com.teamwizardry.librarianlib.features.saving.NamedDynamic
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.Save
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
class BlockNamedDynamicSaving : BlockMod("saving_named_dynamic", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {

//            var existing = te.map[facing] ?: TestType(0, 0)
//            te.map[facing] = if(playerIn.isSneaking) {
//                TestType2(existing.a+1, existing.b-1, ((existing as? TestType2)?.c ?: -1) + 1)
//            } else {
//                TestType(existing.a-1, existing.b+1)
//            }

            te.list.add(if(playerIn.isSneaking) RegTestB(hitX, hitY, hitZ) else RegTestA(facing))

            te.markDirty()
        } else {
            playerIn.sendMessage(te.list.joinToString(", ", "[", "]"))//"HashMap<EnumFacing, Int>: [" + te.map.map { "${it.key}:${it.value}" }.joinToString(", ") + "]")
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_named_dynamic")
    class TETest : TileMod() {
        @Save
        val list = mutableListOf<RegTest>()
    }
}

@NamedDynamic("")
@Savable
open class RegTest

@NamedDynamic("resource:location")
@Savable
open class RegTestA(@Save val side: EnumFacing) : RegTest() {
    override fun toString(): String {
        return "RegTestA($side)"
    }
}

@NamedDynamic("resource:location2")
@Savable
open class RegTestB(@Save val hitX: Float, @Save val hitY: Float, @Save val hitZ: Float) : RegTest() {
    override fun toString(): String {
        return "RegTestB($hitX, $hitY, $hitZ)"
    }
}
