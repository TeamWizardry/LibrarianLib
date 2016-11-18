package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.ProbeInfoWrapper
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
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
            te.markDirty()
        } else {
            playerIn.sendMessage("Boolean: " + te.coolBoolean)
            playerIn.sendMessage("Byte: " + te.coolByte)
            playerIn.sendMessage("Char: " + te.coolChar)
            playerIn.sendMessage("Short: " + te.coolShort)
            playerIn.sendMessage("Int: " + te.coolInt)
            playerIn.sendMessage("Long: " + te.coolLong)
            playerIn.sendMessage("Float: " + te.coolFloat)
            playerIn.sendMessage("Double: " + te.coolDouble)
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    override fun addProbeInformation(info: ProbeInfoWrapper, player: EntityPlayer, world: World, blockState: IBlockState) {
        val probeInfo = info.getProbeInfo()
        val hitInfo = info.getHitData()

        val te = world.getTileEntity(hitInfo.pos) as TETest

        probeInfo.text("Boolean: " + te.coolBoolean)
        probeInfo.text("Byte: " + te.coolByte)
        probeInfo.text("Char: " + te.coolChar)
        probeInfo.text("Short: " + te.coolShort)
        probeInfo.text("Int: " + te.coolInt)
        probeInfo.text("Long: " + te.coolLong)
        probeInfo.text("Float: " + te.coolFloat)
        probeInfo.text("Double: " + te.coolDouble)
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
    }
}
