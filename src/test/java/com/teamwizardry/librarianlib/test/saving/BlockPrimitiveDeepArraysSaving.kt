package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
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
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class BlockPrimitiveDeepArraysSaving : BlockMod("saving_primitiveDeepArrays", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            if (side == EnumFacing.UP) {
                if(playerIn.isSneaking) {
                    te.index2 = (te.index2 + 1) % 3
                } else {
                    te.index = (te.index + 1) % 3
                }
            } else {
                te.coolBoolean[te.index][te.index2] = !te.coolBoolean[te.index][te.index2]
                te.coolByte[te.index][te.index2]++
                te.coolChar[te.index][te.index2]++
                te.coolShort[te.index][te.index2]++
                te.coolInt[te.index][te.index2]++
                te.coolLong[te.index][te.index2]++
                te.coolFloat[te.index][te.index2] += 1.25f
                te.coolDouble[te.index][te.index2] += 1.25
            }
            te.markDirty()
        } else {
            playerIn.sendMessage("Boolean: " + Arrays.deepToString(te.coolBoolean))
            playerIn.sendMessage("Byte: " + Arrays.deepToString(te.coolByte))
            playerIn.sendMessage("Char: " + Arrays.deepToString(te.coolChar))
            playerIn.sendMessage("Short: " + Arrays.deepToString(te.coolShort))
            playerIn.sendMessage("Int: " + Arrays.deepToString(te.coolInt))
            playerIn.sendMessage("Long: " + Arrays.deepToString(te.coolLong))
            playerIn.sendMessage("Float: " + Arrays.deepToString(te.coolFloat))
            playerIn.sendMessage("Double: " + Arrays.deepToString(te.coolDouble))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    init {
        TileMod.registerTile(TETest::class.java, registryName.resourcePath)
    }

    class TETest : TileMod() {
        @Save var index: Int = 0
        @Save var index2: Int = 0
        @Save var coolBoolean: Array<BooleanArray> = Array(3) { BooleanArray(3) }
        @Save var coolByte: Array<ByteArray> = Array(3) { ByteArray(3) }
        @Save var coolChar: Array<CharArray> = Array(3) { CharArray(3) }
        @Save var coolShort: Array<ShortArray> = Array(3) { ShortArray(3) }
        @Save var coolInt: Array<IntArray> = Array(3) { IntArray(3) }
        @Save var coolLong: Array<LongArray> = Array(3) { LongArray(3) }
        @Save var coolFloat: Array<FloatArray> = Array(3) { FloatArray(3) }
        @Save var coolDouble: Array<DoubleArray> = Array(3) { DoubleArray(3) }
    }
}
