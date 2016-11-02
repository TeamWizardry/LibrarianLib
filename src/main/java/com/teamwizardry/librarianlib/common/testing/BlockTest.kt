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
            te.coolBools[te.coolNum % 100] = !te.coolBools[te.coolNum % 100]

            val x = Math.round(hitX).toInt()
            val y = Math.round(hitY).toInt()
            val z = Math.round(hitZ).toInt()
            te.coolArr[x][y][z] = if(te.coolArr[x][y][z]=="_") "#" else "_"

            te.markDirty()
        } else {
            val te = worldIn.getTileEntity(pos!!)!! as TETest
            playerIn.addChatComponentMessage(TextComponentString("${te.coolString} ${te.coolNum}"))
            playerIn.addChatComponentMessage(TextComponentString(te.coolBools.map { if(it) "'" else "." }.joinToString("")))
            playerIn.addChatComponentMessage(TextComponentString("${te.coolArr[0][1][0]} ${te.coolArr[1][1][0]}"))
            playerIn.addChatComponentMessage(TextComponentString("${te.coolArr[0][0][0]} ${te.coolArr[1][0][0]}"))
            playerIn.addChatComponentMessage(TextComponentString("${te.coolArr[0][1][1]} ${te.coolArr[1][1][1]}"))
            playerIn.addChatComponentMessage(TextComponentString("${te.coolArr[0][0][1]} ${te.coolArr[1][0][1]}"))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    class TETest : TileMod() {
        @Save var coolString: String = ""
        @Save var coolNum: Int = 0
        @Save var coolBools: BooleanArray = BooleanArray(100)
        @Save var coolArr: Array<Array<Array<String>>> = Array(2) { Array(2) { Array(2) { "_" }}}

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
