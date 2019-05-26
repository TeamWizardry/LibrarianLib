package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.config.ConfigProperty
import com.teamwizardry.librarianlib.features.utilities.LoggerBase
import com.teamwizardry.librarianlib.test.animator.AnimatorEntryPoint
import com.teamwizardry.librarianlib.test.cap.CapabilityTest
import com.teamwizardry.librarianlib.test.chunkdata.ChunkDataEntryPoint
import com.teamwizardry.librarianlib.test.container.ContainerEntryPoint
import com.teamwizardry.librarianlib.test.fx.FXEntryPoint
import com.teamwizardry.librarianlib.test.neogui.GuiEntryPoint
import com.teamwizardry.librarianlib.test.particlesystem.ParticleSystemEntryPoint
import com.teamwizardry.librarianlib.test.saving.SavingEntryPoint
import com.teamwizardry.librarianlib.test.shader.ShaderEntryPoint
import com.teamwizardry.librarianlib.test.variants.VariantEntryPoint
import com.teamwizardry.librarianlib.test.worlddata.WorldDataEntryPoint
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
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
        PROXY.pre(e)
        entrypoints.forEach {
            it.preInit(e)
        }
        object : ItemMod("test") {

            override fun onItemUse(playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
                println(worldIn.getTileEntity(pos)?.getCapability(CapabilityTest.cap, null))
                return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
            }
        }
        //CapabilityTest.init()
//        Class.forName("com.teamwizardry.librarianlib.test.items.ModItems")
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

        val entrypoints: Array<TestEntryPoint> = arrayOf(
                SavingEntryPoint,
                FXEntryPoint,
                GuiEntryPoint,
                VariantEntryPoint,
                UnsafeTest,
                ContainerEntryPoint,
                ChunkDataEntryPoint,
                WorldDataEntryPoint,
                AnimatorEntryPoint,
//                RenderEntryPoint, // DO NOT ENABLE THIS OR ALL IS SPONGE
                EndEntryPoint,
                ParticleSystemEntryPoint,
                ShaderEntryPoint
        )

        object Tab : ModCreativeTab() {
            init {
                registerDefaultTab()
            }

            override val iconStack: ItemStack
                get() = ItemStack(Blocks.BOOKSHELF)
        }
    }

}

object Config {
    @ConfigProperty("general", "it's a boolean",
            configId = TestMod.MODID)
    var boolean = false

    @ConfigProperty("general", "it's an int",
            configId = TestMod.MODID)
    var int = 0

    @ConfigProperty("general", "it's a double",
            configId = TestMod.MODID)
    var double = 0.0

    @ConfigProperty("general", "it's a string",
            configId = TestMod.MODID)
    var string = ""

    @ConfigProperty("general", "it's a long",
            configId = TestMod.MODID)
    var long = 0L

    @ConfigProperty("general", "it's a boolean array",
            configId = TestMod.MODID)
    var booleanArr = booleanArrayOf(false)

    @ConfigProperty("general", "it's an int array",
            configId = TestMod.MODID)
    var intArr = intArrayOf(0)

    @ConfigProperty("general", "it's a double array",
            configId = TestMod.MODID)
    var doubleArr = doubleArrayOf(0.0)

    @ConfigProperty("general", "it's a string array",
            configId = TestMod.MODID)
    var stringArr = arrayOf("")

    @ConfigProperty("general", "it's a long array",
            configId = TestMod.MODID)
    var longArr = longArrayOf(0L)
}

object TestLog : LoggerBase("LibrarianLibTest")
