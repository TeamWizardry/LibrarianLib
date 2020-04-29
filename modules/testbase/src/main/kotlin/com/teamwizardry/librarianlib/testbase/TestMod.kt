package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestBlockItem
import com.teamwizardry.librarianlib.testbase.objects.TestEntity
import com.teamwizardry.librarianlib.testbase.objects.TestEntityConfig
import com.teamwizardry.librarianlib.testbase.objects.TestEntityRenderer
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import com.teamwizardry.librarianlib.testbase.objects.TestScreenConfig
import com.teamwizardry.librarianlib.mirage.Mirage
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.block.Block
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.Logger
import java.awt.Color

abstract class TestMod(targetName: String, val humanName: String, val logger: Logger) {
    val name = "$targetName-test"
    val itemGroup = object : ItemGroup("librarianlib-$name") {
        private val stack: ItemStack by lazy {
            val stack = ItemStack(LibrarianLibTestBaseModule.testTool)
            stack.orCreateTag.putString("mod", name)
            return@lazy stack
        }
        override fun createIcon(): ItemStack {
            return stack
        }
    }

    val _items: MutableList<Item> = mutableListOf()
    val _blocks: MutableList<Block> = mutableListOf()
    val _entities: MutableList<EntityType<*>> = mutableListOf()
    val _testEntities: MutableList<EntityType<TestEntity>> = mutableListOf()

    val items: List<Item> = _items.unmodifiableView()
    val blocks: List<Block> = _blocks.unmodifiableView()
    val entities: List<EntityType<*>> = _entities.unmodifiableView()

    // auto-fill the item group
    fun TestItemConfig(id: String, name: String, block: TestItemConfig.() -> Unit): TestItemConfig
        = TestItemConfig(id, name, itemGroup, block)
    fun TestItemConfig(id: String, name: String): TestItemConfig
        = TestItemConfig(id, name, itemGroup)
    fun TestEntityConfig(id: String, name: String, block: TestEntityConfig.() -> Unit): TestEntityConfig
        = TestEntityConfig(id, name, itemGroup, block)
    fun TestEntityConfig(id: String, name: String): TestEntityConfig
        = TestEntityConfig(id, name, itemGroup)
    fun TestScreenConfig(id: String, name: String, block: TestScreenConfig.() -> Unit): TestScreenConfig
        = TestScreenConfig(id, name, itemGroup, block)
    fun TestScreenConfig(id: String, name: String): TestScreenConfig
        = TestScreenConfig(id, name, itemGroup)

    operator fun <T: Item> T.unaryPlus(): T {
        _items.add(this)
        return this
    }

    operator fun <T: Block> T.unaryPlus(): T {
        val properties = Item.Properties()
            .group(itemGroup)
        val item = if(this is TestBlock)
            TestBlockItem(this, properties.maxStackSize(1))
        else
            BlockItem(this, properties)
        item.registryName = ResourceLocation(this.registryName!!.namespace, this.registryName!!.path + "_block")
        _blocks.add(this)
        _items.add(item)
        return this
    }

    operator fun <T: EntityType<*>> T.unaryPlus(): T {
        _entities.add(this)
        return this
    }

    operator fun TestEntityConfig.unaryPlus(): TestEntityConfig {
        _entities.add(this.type)
        _testEntities.add(this.type)
        +this.spawnerItem
        return this
    }

    operator fun TestScreenConfig.unaryPlus(): TestScreenConfig {
        +this.activatorItem
        return this
    }

    init {
        LibrarianLibTestBaseModule.add(this)
    }

    init {
        FMLKotlinModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> {
            this.setup(it)
        }
        FMLKotlinModLoadingContext.get().modEventBus.addListener<FMLClientSetupEvent> {
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        FMLKotlinModLoadingContext.get().modEventBus.register(this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun setup(event: FMLCommonSetupEvent) {
    }

    @Suppress("UNUSED_PARAMETER")
    @OnlyIn(Dist.CLIENT)
    fun clientSetup(event: FMLClientSetupEvent) {
        _testEntities.forEach { entity ->
            RenderingRegistry.registerEntityRenderingHandler(entity) { TestEntityRenderer(it) }
        }
        generateItemAssets()
        generateBlockAssets()
        generateLanguageAssets()
    }

    private fun generateItemAssets() {
        items.forEach { item ->
            if(item is TestBlockItem) {
                val name = item.registryName!!
                Mirage.client.add(
                    ResourceLocation(name.namespace, "models/item/${name.path}.json"),
                    """
                        {
                            "parent": "librarianlib-testbase:block/test_block/${item.block.modelName}"
                        }
                    """.trimIndent()
                )
            } else if(item is TestItem) {
                val name = item.registryName!!
                Mirage.client.add(
                    ResourceLocation(name.namespace, "models/item/${name.path}.json"),
                    """
                        {
                            "parent": "librarianlib-testbase:item/test_tool"
                        }
                    """.trimIndent()
                )
            }
        }
    }

    private fun generateBlockAssets() {
        blocks.filterIsInstance<TestBlock>().forEach { block ->
            val name = block.registryName!!
            val model = "block/test_block/${block.modelName}"
            Mirage.client.add(
                ResourceLocation(name.namespace, "blockstates/${name.path}.json"),
                if(block.config.directional) {
                    """
                        {
                            "variants": {
                                "facing=up": { "model": "librarianlib-testbase:$model" },
                                "facing=down": { "model": "librarianlib-testbase:$model", "x": 180 },
                                "facing=east": { "model": "librarianlib-testbase:$model", "y": 90, "x": 90 },
                                "facing=south": { "model": "librarianlib-testbase:$model", "y": 180, "x": 90 },
                                "facing=west": { "model": "librarianlib-testbase:$model", "y": 270, "x": 90 },
                                "facing=north": { "model": "librarianlib-testbase:$model", "y": 0, "x": 90 }
                            }
                        }
                    """.trimIndent()
                } else {
                    """
                        {
                            "variants": {
                                "": { "model": "librarianlib-testbase:$model" }
                            }
                        }
                    """.trimIndent()
                }
            )
        }
    }

    private fun generateLanguageAssets() {
        languageKeys().forEach { (key, value) ->
            Mirage.client.addLanguageKey(key, value)
        }
    }

    private fun languageKeys(): Map<String, String> {
        val keys = mutableMapOf<String, String>()
        keys[itemGroup.translationKey] = "$humanName Test"
        items.forEach { item ->
            if(item is TestItem) {
                val registryName = item.registryName!!
                keys[registryName.translationKey("item")] = item.config.name
                item.config.description?.also {
                    keys[registryName.translationKey("item", "tooltip")] = it
                }
            }
        }
        blocks.forEach { block ->
            if(block is TestBlock) {
                val registryName = block.registryName!!
                keys[registryName.translationKey("block")] = block.config.name
                block.config.description?.also {
                    keys[registryName.translationKey("block", "tooltip")] = it
                }
            }
        }
        return keys
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    internal fun registerColors(colorHandlerEvent: ColorHandlerEvent.Item) {
        colorHandlerEvent.itemColors.register(IItemColor { stack, tintIndex ->
            val item = stack.item
            if(tintIndex == 1 && item is TestBlockItem)
                DistinctColors.forObject(item.block.registryName).rgb
            else if(tintIndex == 1 && item is TestItem)
                DistinctColors.forObject(item.registryName).rgb
            else
                Color.WHITE.rgb
        }, *items.toTypedArray())
        colorHandlerEvent.blockColors.register(IBlockColor { state, _, _, tintIndex ->
            if(tintIndex == 1 && state.block is TestBlock)
                DistinctColors.forObject(state.block.registryName).rgb
            else
                Color.WHITE.rgb
        }, *blocks.toTypedArray())
    }

    @SubscribeEvent
    open fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
        blocks.forEach {
            ForgeRegistries.BLOCKS.register(it)
        }
    }

    @SubscribeEvent
    open fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        items.forEach {
            ForgeRegistries.ITEMS.register(it)
        }
    }

    @SubscribeEvent
    open fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
        entities.forEach {
            ForgeRegistries.ENTITIES.register(it)
        }
    }
}
