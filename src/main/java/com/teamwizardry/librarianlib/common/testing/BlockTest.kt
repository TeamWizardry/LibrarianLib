package com.teamwizardry.librarianlib.common.testing;

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

class BlockTest : BlockMod("test", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!worldIn.isRemote) {
            val te = worldIn.getTileEntity(pos!!)!! as TETest
            te.coolString += "1"
            te.coolNum++
            te.markDirty()
        } else {
            val te = worldIn.getTileEntity(pos!!)!! as TETest
            playerIn.addChatComponentMessage(TextComponentString("${te.coolString} ${te.coolNum}"))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    class TETest : TileMod() {
        @Save var coolString: String = ""
        @Save var coolNum: Int = 0

        @SaveMethodGetter("name")
        fun getName(): String {
            return "$coolString $coolNum"
        }

        @SaveMethodSetter("name")
        fun setName(s: String) {
            LibrarianLog.info(s)
        }
    }
}
