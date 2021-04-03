package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
//import com.teamwizardry.librarianlib.testbase.objects.TestEntityRenderer
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import com.teamwizardry.librarianlib.testcore.objects.UnitTestCommand
import com.teamwizardry.librarianlib.testcore.objects.UnitTestSuite
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryBuilder
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.awt.Color

@Mod("testcore")
public object LibrarianLibTestBaseModule : LibrarianLibModule("testbase", "Test Base") {

    init {
        MinecraftForge.EVENT_BUS.register(this)
        MOD_BUS.register(this)
    }

    public val testTool: Item = Item(Item.Properties().maxStackSize(1)).also {
        it.registryName = ResourceLocation("testcore", "test_tool")
    }

    private val mods = mutableListOf<TestMod>().synchronized()

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        ForgeRegistries.ITEMS.register(testTool)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    @JvmSynthetic
    internal fun clientSetup(event: FMLClientSetupEvent) {
//        RenderingRegistry.registerEntityRenderingHandler(TestEntity::class.java) { TestEntityRenderer(it) }
    }

    @SubscribeEvent
    internal fun createRegistries(e: RegistryEvent.NewRegistry) {
        RegistryBuilder<UnitTestSuite>()
            .setName(ResourceLocation("testcore:unit_tests"))
            .setType(UnitTestSuite::class.java)
            .disableSaving()
            .create()
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    internal fun registerColors(colorHandlerEvent: ColorHandlerEvent.Item) {
        colorHandlerEvent.itemColors.register({ stack, tintIndex ->
            if(tintIndex == 1)
                DistinctColors.forObject(stack.tag?.getString("mod")).rgb
            else
                Color.WHITE.rgb
        }, testTool)
    }

    @SubscribeEvent
    internal fun blockBreak(event: BlockEvent.BreakEvent) {
        val stack = event.player.heldItemMainhand
        val item = stack.item
        if(item is TestItem) {
            if(item.config.leftClickBlock.exists) {
                event.isCanceled = true
            }
        }
    }

    internal fun add(mod: TestMod) {
        logger.debug("Adding test mod $mod")
        mods.add(mod)
    }

    @SubscribeEvent
    internal fun registerCommands(e: RegisterCommandsEvent) {
        UnitTestCommand.register(e.dispatcher)
    }
}

internal val logger = LibrarianLibTestBaseModule.makeLogger(null)
