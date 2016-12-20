package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.Savable
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
class BlockSavableSaving : BlockMod("saving_primitiveSavable", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.data = MyImmutableDataClass(side.toString())
            te.mutabledata.side = side.toString()
            te.markDirty()
        } else {
            playerIn.sendMessage("MyImmutableDataClass: " + te.data + " - " + System.identityHashCode(te.data))
            playerIn.sendMessage("MyMutableDataClass: " + te.mutabledata + " - " + System.identityHashCode(te.mutabledata))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_primitiveSavable")
    class TETest : TileMod() {
        @Save var data = MyImmutableDataClass("none")
        @Save var mutabledata = MyMutableDataClass("none")
    }
}

@Savable
data class MyImmutableDataClass(@field:Save val side: String) {
    private constructor() : this("")
}

@Savable(true)
data class MyMutableDataClass(@field:Save var side: String) {
    private constructor() : this("")
}
