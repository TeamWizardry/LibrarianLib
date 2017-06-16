package com.teamwizardry.librarianlib.test.cap

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.features.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.features.kotlin.toComponent
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
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
    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        (worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test = (worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test?.plus(1) ?: 0
        //(worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.markDirty()
        playerIn?.sendStatusMessage((worldIn?.getTileEntity(pos) as? TileEntityCapTest?)?.cap?.test.toString().toComponent(), false)
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCapTest()
    }

}

@TileRegister("hi")
class TileEntityCapTest : TileMod() {
    @CapabilityProvide(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN)
    val cap: CapabilityTest = CapabilityTest()
}
