package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.MiscUtil
import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.mirage.Mirage
import com.teamwizardry.librarianlib.testbase.objects.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.awt.Color

public abstract class TestMod(public val module: LibrarianLibModule) {
    public val modid: String = MiscUtil.getModId(this.javaClass)
    public val name: String = "${module.name}-test"
    public val itemGroup: ItemGroup = object: ItemGroup("librarianlib-$name") {
        private val stack: ItemStack by lazy {
            val stack = ItemStack(LibrarianLibTestBaseModule.testTool)
            stack.orCreateTag.putString("mod", name)
            return@lazy stack
        }

        override fun createIcon(): ItemStack {
            return stack
        }
    }

    private val loggers = mutableMapOf<String?, Logger>()
    private val _items: MutableList<Item> = mutableListOf()
    private val _blocks: MutableList<Block> = mutableListOf()
    private val _entities: MutableList<EntityType<*>> = mutableListOf()
    private val _testEntities: MutableList<EntityType<TestEntity>> = mutableListOf()
    private val _unitTests: MutableList<UnitTestSuite> = mutableListOf()

    public val items: List<Item> = _items.unmodifiableView()
    public val blocks: List<Block> = _blocks.unmodifiableView()
    public val entities: List<EntityType<*>> = _entities.unmodifiableView()
    public val unitTests: List<UnitTestSuite> = _unitTests.unmodifiableView()

    // auto-fill the item group
    public fun TestItemConfig(id: String, name: String, block: TestItemConfig.() -> Unit): TestItemConfig = TestItemConfig(id, name, itemGroup, block)
    public fun TestItemConfig(id: String, name: String): TestItemConfig = TestItemConfig(id, name, itemGroup)
    public fun TestEntityConfig(id: String, name: String, block: TestEntityConfig.() -> Unit): TestEntityConfig = TestEntityConfig(id, name, itemGroup, block)
    public fun TestEntityConfig(id: String, name: String): TestEntityConfig = TestEntityConfig(id, name, itemGroup)
    public fun TestScreenConfig(id: String, name: String, block: TestScreenConfig.() -> Unit): TestScreenConfig = TestScreenConfig(id, name, itemGroup, block)
    public fun TestScreenConfig(id: String, name: String): TestScreenConfig = TestScreenConfig(id, name, itemGroup)
    public fun UnitTestSuite(id: String, block: UnitTestSuite.() -> Unit): UnitTestSuite = UnitTestSuite(id).also { it.block() }
    public fun UnitTestSuite(id: String): UnitTestSuite = UnitTestSuite().also { it.registryName = ResourceLocation(modid, id) }

    public operator fun <T: Item> T.unaryPlus(): T {
        _items.add(this)
        return this
    }

    public operator fun <T: Block> T.unaryPlus(): T {
        val properties = Item.Properties()
            .group(itemGroup)
        val item = if (this is TestBlock)
            TestBlockItem(this, properties.maxStackSize(1))
        else
            BlockItem(this, properties)
        item.registryName = ResourceLocation(this.registryName!!.namespace, this.registryName!!.path + "_block")
        _blocks.add(this)
        _items.add(item)
        return this
    }

    public operator fun <T: EntityType<*>> T.unaryPlus(): T {
        _entities.add(this)
        return this
    }

    public operator fun TestEntityConfig.unaryPlus(): TestEntityConfig {
        _entities.add(this.type)
        _testEntities.add(this.type)
        +this.spawnerItem
        return this
    }

    public operator fun TestScreenConfig.unaryPlus(): TestScreenConfig {
        +this.activatorItem
        return this
    }

    public operator fun UnitTestSuite.unaryPlus(): UnitTestSuite {
        _unitTests.add(this)
        return this
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(clazz: Class<*>): Logger {
        return makeLogger(clazz.simpleName)
    }

    /**
     * Create a logger for this module.
     */
    public inline fun <reified T> makeLogger(): Logger {
        return makeLogger(T::class.java)
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(label: String?): Logger {
        return loggers.getOrPut(label) {
            val labelSuffix = label?.let { " ($it)" } ?: ""
            val logger = LogManager.getLogger("LibrarianLib Test: ${module.humanName}$labelSuffix")
            module.registerLogger(logger)
            logger
        }
    }

    init {
        LibrarianLibTestBaseModule.add(this)
    }

    init {
        MOD_BUS.addListener<FMLCommonSetupEvent> {
            this.testSetup(it)
            this.setup(it)
        }
        MOD_BUS.addListener<FMLClientSetupEvent> {
            this.clientTestSetup(it)
            this.clientSetup(it)
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
        MOD_BUS.register(this)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun testSetup(event: FMLCommonSetupEvent) {
    }

    @Suppress("UNUSED_PARAMETER")
    @OnlyIn(Dist.CLIENT)
    private fun clientTestSetup(event: FMLClientSetupEvent) {
        _testEntities.forEach { entity ->
            RenderingRegistry.registerEntityRenderingHandler(entity) { TestEntityRenderer(it) }
        }

        blocks.forEach { block ->
            if (block is TestBlock) {
                block.tileEntityRenderer?.also { renderer ->
                    @Suppress("UNCHECKED_CAST")
                    ClientRegistry.bindTileEntityRenderer(block.tileEntityType as TileEntityType<TileEntity>) {
                        renderer.applyClient(it) as TileEntityRenderer<TileEntity>
                    }
                }
            }
        }

        generateItemAssets()
        generateBlockAssets()
        generateLanguageAssets()

        blocks.forEach { block ->
            if (block is TestBlock) {
                RenderTypeLookup.setRenderLayer(block, RenderType.getCutout())
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    public open fun setup(event: FMLCommonSetupEvent) {
    }

    @Suppress("UNUSED_PARAMETER")
    @OnlyIn(Dist.CLIENT)
    public open fun clientSetup(event: FMLClientSetupEvent) {
    }

    private fun generateItemAssets() {
        items.forEach { item ->
            if (item is TestBlockItem) {
                val name = item.registryName!!
                Mirage.clientResources.add(
                    ResourceLocation(name.namespace, "models/item/${name.path}.json"),
                    """
                        {
                            "parent": "librarianlib-testbase:block/test_block/${item.block.modelName}"
                        }
                    """.trimIndent()
                )
            } else if (item is ITestItem) {
                val name = item.registryName!!
                Mirage.clientResources.add(
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
            Mirage.clientResources.add(
                ResourceLocation(name.namespace, "blockstates/${name.path}.json"),
                if (block.config.directional) {
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
            Mirage.languageMap.add(key, value)
        }
    }

    private fun languageKeys(): Map<String, String> {
        val keys = mutableMapOf<String, String>()
        val groupKey = (itemGroup.groupName as TranslationTextComponent).key
        keys[groupKey] = "${module.humanName} Test"
        items.forEach { item ->
            if (item is ITestItem) {
                val registryName = item.registryName!!
                keys[registryName.translationKey("item")] = item.itemName
                item.itemDescription?.also {
                    keys[registryName.translationKey("item", "tooltip")] = it
                }
            }
        }
        blocks.forEach { block ->
            if (block is TestBlock) {
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
        colorHandlerEvent.itemColors.register({ stack, tintIndex ->
            val item = stack.item
            if (tintIndex == 1 && item is TestBlockItem)
                DistinctColors.forObject(item.block.registryName).rgb
            else if (tintIndex == 1 && item is ITestItem)
                DistinctColors.forObject(item.registryName).rgb
            else
                Color.WHITE.rgb
        }, *items.toTypedArray())
        colorHandlerEvent.blockColors.register({ state, _, _, tintIndex ->
            if (tintIndex == 1 && state.block is TestBlock)
                DistinctColors.forObject(state.block.registryName).rgb
            else
                Color.WHITE.rgb
        }, *blocks.toTypedArray())
    }

    @SubscribeEvent
    public open fun registerUnitTests(e: RegistryEvent.Register<UnitTestSuite>) {
        unitTests.forEach {
            e.registry.register(it)
        }
    }

    @SubscribeEvent
    public open fun registerBlocks(e: RegistryEvent.Register<Block>) {
        blocks.forEach {
            e.registry.register(it)
        }
    }

    @SubscribeEvent
    public open fun registerTileEntities(e: RegistryEvent.Register<TileEntityType<*>>) {
        blocks.forEach { block ->
            if (block is TestBlock) {
                block.tileEntityType?.also { type ->
                    e.registry.register(type)
                }
            }
        }
    }

    @SubscribeEvent
    public open fun registerItems(e: RegistryEvent.Register<Item>) {
        items.forEach {
            e.registry.register(it)
        }
    }

    @SubscribeEvent
    public open fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
        entities.forEach {
            e.registry.register(it)
        }
    }
}
