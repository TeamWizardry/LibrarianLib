package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class BlockPrimitiveArraysSaving : BlockMod("saving_primitiveArrays", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking) {
                te.index = (te.index + 1) % 3
            } else {
                te.coolBoolean[te.index] = !te.coolBoolean[te.index]
                te.coolByte[te.index]++
                te.coolChar[te.index]++
                te.coolShort[te.index]++
                te.coolInt[te.index]++
                te.coolLong[te.index]++
                te.coolFloat[te.index] += 1.25f
                te.coolDouble[te.index] += 1.25
            }
            te.markDirty()
        } else {
            playerIn.sendMessage("Boolean: " + Arrays.toString(te.coolBoolean))
            playerIn.sendMessage("Byte: " + Arrays.toString(te.coolByte))
            playerIn.sendMessage("Char: " + Arrays.toString(te.coolChar))
            playerIn.sendMessage("Short: " + Arrays.toString(te.coolShort))
            playerIn.sendMessage("Int: " + Arrays.toString(te.coolInt))
            playerIn.sendMessage("Long: " + Arrays.toString(te.coolLong))
            playerIn.sendMessage("Float: " + Arrays.toString(te.coolFloat))
            playerIn.sendMessage("Double: " + Arrays.toString(te.coolDouble))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_primitiveArrays")
    class TETest : TileMod() {
        @Save var index: Int = 0
        @Save var coolBoolean: BooleanArray = BooleanArray(3)
        @Save var coolByte: ByteArray = ByteArray(3)
        @Save var coolChar: CharArray = CharArray(3)
        @Save var coolShort: ShortArray = ShortArray(3)
        @Save var coolInt: IntArray = IntArray(3)
        @Save var coolLong: LongArray = LongArray(3)
        @Save var coolFloat: FloatArray = FloatArray(3)
        @Save var coolDouble: DoubleArray = DoubleArray(3)
    }
}
