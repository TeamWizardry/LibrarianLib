package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
//import com.teamwizardry.librarianlib.testbase.objects.TestEntityRenderer
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.UnitTestCommand
import com.teamwizardry.librarianlib.testbase.objects.UnitTestSuite
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryBuilder
import org.apache.logging.log4j.LogManager
import java.awt.Color

public object LibrarianLibTestBaseModule : LibrarianLibModule("testbase", "Test Base") {
    public val testTool: Item = Item(Item.Properties().maxStackSize(1)).also {
        it.registryName = ResourceLocation("librarianlib-testbase", "test_tool")
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
            .setName(ResourceLocation("librarianlib-testbase:unit_tests"))
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
    internal fun serverStarting(e: FMLServerStartingEvent) {
        UnitTestCommand.register(e.commandDispatcher)
    }
}

internal val logger = LibrarianLibTestBaseModule.makeLogger(null)
