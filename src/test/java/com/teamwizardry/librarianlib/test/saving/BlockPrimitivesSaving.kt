package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.NoSync
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class BlockPrimitivesSaving : BlockMod("saving_primitives", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.coolBoolean = !te.coolBoolean
            te.coolByte++
            te.coolChar++
            te.coolShort++
            te.coolInt++
            te.coolLong++
            te.coolFloat += 1.25f
            te.coolDouble += 1.25
            te.secretString = te.coolInt * "secret"
            te.markDirty()
        } else {
            te.run {
                playerIn.sendMessage("bool: $coolBoolean")
                playerIn.sendMessage("byte: $coolByte")
                playerIn.sendMessage("char: $coolChar")
                playerIn.sendMessage("short: $coolShort")
                playerIn.sendMessage("int: $coolInt")
                playerIn.sendMessage("long: $coolLong")
                playerIn.sendMessage("float: $coolFloat")
                playerIn.sendMessage("double: $coolDouble")
            }
        }
        return true
    }


    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    override fun canRenderInLayer(layer: BlockRenderLayer?): Boolean {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT
    }

    @TileRegister("saving_primitives")
    class TETest : TileMod() {
        @Save var coolBoolean: Boolean = false
        @Save var coolByte: Byte = 0
        @Save var coolChar: Char = '0'
        @Save var coolShort: Short = 0
        @Save var coolInt: Int = 0
        @Save var coolLong: Long = 0
        @Save var coolFloat: Float = 0f
        @Save var coolDouble: Double = 0.0
        @Save @NoSync var secretString: String = ""
    }
}
