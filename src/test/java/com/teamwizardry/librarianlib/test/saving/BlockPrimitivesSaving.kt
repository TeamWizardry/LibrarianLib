package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import com.teamwizardry.librarianlib.common.util.times
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
            te.secretString = te.coolInt * "ftyjbnk"
            te.markDirty()
        } else {
            te.forEveryField {
                playerIn.sendMessage("${it.first}: ${it.second}")
            }
        }
        return true
    }


    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }


    @TileRegister("saving_primitives")
    class TETest : TileMod() {
        override val automaticallyAddFieldsToWaila: Boolean
            get() = true
        @Save(displayName = "Boolean") var coolBoolean: Boolean = false
        @Save(displayName = "Byte") var coolByte: Byte = 0
        @Save(displayName = "Char") var coolChar: Char = '0'
        @Save(displayName = "Short") var coolShort: Short = 0
        @Save(displayName = "Int") var coolInt: Int = 0
        @Save(displayName = "Long") var coolLong: Long = 0
        @Save(displayName = "Float") var coolFloat: Float = 0f
        @Save(displayName = "Double") var coolDouble: Double = 0.0
        @Save(displayName = "") var secretString: String = "" //this string is secret, sssh, it should not appear in waila
    }
}
