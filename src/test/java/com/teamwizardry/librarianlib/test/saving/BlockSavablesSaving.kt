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
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class BlockSavablesSaving : BlockMod("saving_savables", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.customThing = te.customThing ?: CustomClasz()
            te.customThingExt = te.customThingExt ?: CustomClaszExt()
            te.customThing?.let { it.a++; it.b-- }
            te.customThingExt?.let { it.a++; it.b--; it.c++ }
//            te.customThingImut = CustomImmut(te.customThingImut.A + 1, te.customThingImut.B - 1)
            te.markDirty()
        } else {
            te.run {
                playerIn.sendMessage("thing: ${customThing?.a}, ${customThing?.b}")
                playerIn.sendMessage("thingExt: ${customThingExt?.a}, ${customThingExt?.b}, ${customThingExt?.c}")
//                playerIn.sendMessage("immut: ${customThingImut.A}, ${customThingImut.B}")
            }
        }
        return true
    }


    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    override fun canRenderInLayer(state:IBlockState?, layer: BlockRenderLayer?): Boolean {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT
    }

    @TileRegister("saving_savables")
    class TETest : TileMod() {
        @Save var customThing: CustomClasz? = null
        @Save var customThingExt: CustomClaszExt? = null
//        @Save var customThingImut: CustomImmut = CustomImmut(-1, 1)
    }
}

@Savable data class CustomClasz(var a: Int, var b: Int) {
    constructor() : this(0,0)
}
open class BaseCustomThing(var a: Int, var b: Int)
@Savable class CustomClaszExt(a: Int, b: Int, var c: Int) : BaseCustomThing(a, b) {
    constructor() : this(0,0,0)
}
@Savable data class CustomImmut(val A: Int, val B: Int)
