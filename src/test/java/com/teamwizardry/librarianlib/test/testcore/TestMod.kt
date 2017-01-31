package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.core.LoggerBase
import com.teamwizardry.librarianlib.test.cap.CapabilityTest
import com.teamwizardry.librarianlib.test.command.CommandEntryPoint
import com.teamwizardry.librarianlib.test.container.ContainerEntryPoint
import com.teamwizardry.librarianlib.test.fx.FXEntryPoint
import com.teamwizardry.librarianlib.test.gui.GuiEntryPoint
import com.teamwizardry.librarianlib.test.saving.SavingEntryPoint
import com.teamwizardry.librarianlib.test.variants.VariantEntryPoint
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
@Mod(modid = TestMod.MODID, version = TestMod.VERSION, name = TestMod.MODNAME, dependencies = TestMod.DEPENDENCIES, useMetadata = true)
class TestMod {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        entrypoints = arrayOf(
                SavingEntryPoint,
                FXEntryPoint,
                GuiEntryPoint,
                VariantEntryPoint,
                UnsafeTest,
                ContainerEntryPoint,
                CommandEntryPoint
        )
        PROXY.pre(e)
        entrypoints.forEach {
            it.preInit(e)
        }
        object : ItemMod("test") {
            override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, playerIn: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack> {
                //ModItems.test(playerIn)
                //println(playerIn.getCapability(CapabilityTest.cap, null))
                return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand)
            }

            override fun onItemUse(stack: ItemStack?, playerIn: EntityPlayer?, worldIn: World?, pos: BlockPos?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
                println(worldIn?.getTileEntity(pos)?.getCapability(CapabilityTest.cap, null))
                return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
            }
        }
        //CapabilityTest.init()
        Class.forName("com.teamwizardry.librarianlib.test.items.ModItems")
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
        entrypoints.forEach {
            it.init(e)
        }
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.post(e)
        entrypoints.forEach {
            it.postInit(e)
        }
    }

    companion object {

        const val MODID = "librarianlibtest"
        const val MODNAME = "LibrarianLib Test"
        const val VERSION = "0.0"
        const val CLIENT = "com.teamwizardry.librarianlib.test.testcore.LibTestClientProxy"
        const val SERVER = "com.teamwizardry.librarianlib.test.testcore.LibTestCommonProxy"
        const val DEPENDENCIES = "required-before:librarianlib"

        @JvmStatic
        @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
        lateinit var PROXY: LibTestCommonProxy

        lateinit var entrypoints: Array<TestEntryPoint>

        object Tab : ModCreativeTab(MODID) {
            init {
                registerDefaultTab()
            }

            override val iconStack: ItemStack
                get() = ItemStack(Blocks.BOOKSHELF)
        }
    }

}

object TestLog : LoggerBase("LibrarianLibTest")
