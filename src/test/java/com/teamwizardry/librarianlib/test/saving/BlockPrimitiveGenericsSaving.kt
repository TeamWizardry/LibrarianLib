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
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import javax.annotation.Nonnull

/**
 * Created by TheCodeWarrior
 */
class BlockPrimitiveGenericsSaving : BlockMod("saving_primitiveGenerics", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.map[side] = (te.map[side] ?: 0) + 1
            te.markDirty()
        } else {
            playerIn.sendMessage("HashMap<EnumFacing, Int>: [" + te.map.map { "${it.key}:${it.value}" }.joinToString(", ") + "]")
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_primitiveGenerics")
    class TETest : TileMod() {
        @Save @Nonnull var map: HashMap<EnumFacing, Int> = HashMap()
    }
}
