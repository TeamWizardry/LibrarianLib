package com.teamwizardry.librarianlib.test.cap

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.common.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.toComponent
import com.teamwizardry.librarianlib.common.util.toRl
import com.teamwizardry.librarianlib.test.testcore.TestMod
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
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

/**
 * Created by Elad on 1/22/2017.
 */
class CapabilityTest : CapabilityMod("${TestMod.MODID}:CapTest".toRl()) {

    companion object {
        @JvmStatic
        @CapabilityInject(CapabilityTest::class)
        lateinit var cap: Capability<CapabilityTest>

        fun init() {
            println("Init")
            register(CapabilityTest::class.java, ICapabilityObjectProvider {
                cap
            })
            MinecraftForge.EVENT_BUS.register(CapabilityTest::class.java)
            BlockCapTest()
        }
    }


    @Save
    var test = 0

    override fun writeCustomNBT(nbtTagCompound: NBTTagCompound) {
        nbtTagCompound.setInteger("test", test)
    }

    override fun readCustomNBT(nbtTagCompound: NBTTagCompound) {
        test = nbtTagCompound.getInteger("test")
    }

}

class BlockCapTest : BlockMod("hi", Material.ROCK), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        (worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test = (worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test?.plus(1) ?: 0
        //(worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.markDirty()
        playerIn?.sendStatusMessage((worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test.toString().toComponent())
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCapTest()
    }

}

@TileRegister("hi")
class TileEntityCapTest : TileMod() {
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityTest.cap || super.hasCapability(capability, facing)
    }

    val cap: CapabilityTest = CapabilityTest()

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityTest.cap) cap as T else super.getCapability(capability, facing)
    }
}
