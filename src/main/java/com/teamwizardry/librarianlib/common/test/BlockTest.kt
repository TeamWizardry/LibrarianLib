package com.teamwizardry.librarianlib.common.test

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.Save
import com.teamwizardry.librarianlib.common.util.Serializator
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.commons.lang3.mutable.MutableObject

/**
 * Created by Elad on 10/14/2016.
 */
class BlockTest : BlockMod("test", Material.BARRIER), ITileEntityProvider {
    init {
        GameRegistry.registerTileEntity(TileTest::class.java, "test")
    }
    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TileTest()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer?, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos)
        if(te !is TileTest || worldIn.isRemote) return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
        else {
            println(te.isSaadUgly)
            if(playerIn?.isSneaking ?: false)
                te.isSaadUgly.value = true
            else te.isSaadUgly.value = false
            te.markDirty()
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }
    companion object {
        const val SHOULD_GENERATE = true
    }

}

class TileTest : TileMod() {
    @Save var saadIsUgly = ""
    @Save var saadUglinessMeter: Int = 999
    @Save(serializator = "com.teamwizardry.librarianlib.common.test.MutableSerializable") var isSaadUgly: MutableObject<Boolean> = MutableObject(true)
}
class MutableSerializable : Serializator<MutableObject<Boolean>> {
    override fun writeToNBT(t: MutableObject<Boolean>, nbt: NBTTagCompound, name: String) {
       nbt.setBoolean(name, t.value)
    }

    override fun readFromNBT(nbt: NBTTagCompound, name: String): MutableObject<Boolean> {
        return MutableObject(nbt.getBoolean(name))
    }

}
