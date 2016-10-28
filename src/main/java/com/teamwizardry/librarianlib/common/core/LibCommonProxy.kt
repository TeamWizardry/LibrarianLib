@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    open fun pre(e: FMLPreInitializationEvent) {
        EasyConfigHandler().init(LibrarianLib.MODID, e.suggestedConfigurationFile, e.asmData)
        if(LibrarianLib.DEV_ENVIRONMENT) initBlock()
    }

    private fun initBlock() {
        BlockTest()
    }

    open fun init(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    open val bookInstance: Book?
        get() = null


    class BlockTest : BlockMod("test", Material.CACTUS), ITileEntityProvider {
        override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
            if (!worldIn.isRemote) {
                val te = worldIn.getTileEntity(pos!!)!! as TETest
                te.coolString += "1"
                te.coolNum++
                te.markDirty()
            } else {
                val te = worldIn.getTileEntity(pos!!)!! as TETest
                playerIn.addChatComponentMessage(TextComponentString("${te.coolString} ${te.coolNum}"))
            }
            return true
        }

        override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
            return TETest().init()
        }

        class TETest : TileMod() {
            @Save var coolString: String = ""
            @Save var coolNum: Int = 0

            fun init(): TETest {
                if (!registeredTE) {
                    TileMod.registerTile(javaClass, "test")
                    registeredTE = true
                }
                return this
            }

            companion object {
                var registeredTE = false
            }
        }
    }
}

