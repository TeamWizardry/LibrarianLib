package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestBlockItem
import com.teamwizardry.librarianlib.testbase.objects.TestEntityConfig
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import com.teamwizardry.librarianlib.virtualresources.VirtualResources
import net.minecraft.block.Block
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.Logger
import java.awt.Color

abstract class TestMod(targetName: String, val humanName: String, logger: Logger): LibrarianLibModule("$targetName-test", logger) {
    val itemGroup = object : ItemGroup("librarianlib-$name") {
        private val stack: ItemStack by lazy {
            val stack = ItemStack(LibTestBaseModule.testTool)
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
        +this.spawnerItem
        return this
    }

    init {
        LibTestBaseModule.add(this)
    }

    override fun setup(event: FMLCommonSetupEvent) {
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        entities.forEach {
//            RenderingRegistry.registerEntityRenderingHandler(entityClass) { ParticleSpawnerEntityRenderer(it) }
        }

        items.forEach { item ->
            if(item is TestBlockItem) {
                val name = item.registryName!!
                VirtualResources.client.add(
                    ResourceLocation(name.namespace, "models/item/${name.path}.json"),
                    """
                        {
                            "parent": "librarianlib-testbase:block/test_block/${item.block.modelName}"
                        }
                    """.trimIndent()
                )
            } else if(item is TestItem) {
                val name = item.registryName!!
                VirtualResources.client.add(
                    ResourceLocation(name.namespace, "models/item/${name.path}.json"),
                    """
                        {
                            "parent": "librarianlib-testbase:item/test_tool"
                        }
                    """.trimIndent()
                )
            }
        }
        blocks.forEach { block ->
            if(block is TestBlock) {
                val name = block.registryName!!
                val model = "block/test_block/${block.modelName}"
                VirtualResources.client.add(
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
    }

    @SubscribeEvent
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

    override fun registerBlocks(blockRegistryEvent: RegistryEvent.Register<Block>) {
        blocks.forEach {
            ForgeRegistries.BLOCKS.register(it)
        }
    }

    override fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
        items.forEach {
            ForgeRegistries.ITEMS.register(it)
        }
    }

    override fun registerEntities(entityRegistryEvent: RegistryEvent.Register<EntityType<*>>) {
        entities.forEach {
            ForgeRegistries.ENTITIES.register(it)
        }
    }
}
