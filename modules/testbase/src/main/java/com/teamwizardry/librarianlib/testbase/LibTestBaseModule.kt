package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestEntity
import com.teamwizardry.librarianlib.testbase.objects.TestEntityRenderer
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import com.teamwizardry.librarianlib.virtualresources.VirtualResources
import net.minecraft.block.Block
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import java.awt.Color

@Mod("librarianlib-testbase")
class LibTestBaseModule : LibrarianLibModule("testbase", logger) {

    init {
        _testTool = Item(Item.Properties().maxStackSize(1)).also {
            it.registryName = ResourceLocation(modid, "test_tool")
        }
    }

    override fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        super.registerItems(itemRegistryEvent)
        ForgeRegistries.ITEMS.register(testTool)
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        RenderingRegistry.registerEntityRenderingHandler(TestEntity::class.java) { TestEntityRenderer(it) }
        VirtualResources.client.add(ResourceLocation(modid, "lang/en_us.json")) {
            val keys = languageKeys()
            return@add "{\n" + keys.map {
                "    '${it.key}': \"${it.value.replace("\n", "\\n").replace("\"", "\\\"")}\""
            }.joinToString(",\n") + "\n}"
        }
    }

    @SubscribeEvent
    internal fun registerColors(colorHandlerEvent: ColorHandlerEvent.Item) {
        colorHandlerEvent.itemColors.register(IItemColor { stack, tintIndex ->
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

    companion object {
        val testTool: Item get() = _testTool
        private lateinit var _testTool: Item

        private val mods = mutableListOf<TestMod>()

        private fun languageKeys(): Map<String, String> {
            val keys = mutableMapOf<String, String>()
            keys[Util.makeTranslationKey("item", testTool.registryName)] = "<Test creative tab icon>"
            mods.forEach { mod ->
                keys["itemGroup.${mod.modid}"] = "${mod.humanName} Test"
                mod.items.forEach forEachItem@{ item ->
                    if(item !is TestItem) return@forEachItem
                    keys[Util.makeTranslationKey("item", item.registryName)] = item.config.name
                }
                mod.blocks.forEach forEachBlock@{ block ->
                    if(block !is TestBlock) return@forEachBlock
                    keys[Util.makeTranslationKey("block", block.registryName)] = block.config.name
                }
            }
            return keys
        }

        internal fun add(mod: TestMod) {
            mods.add(mod)
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Test Base")
