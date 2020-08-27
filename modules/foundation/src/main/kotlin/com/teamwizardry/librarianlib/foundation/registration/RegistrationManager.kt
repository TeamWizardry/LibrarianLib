package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.LibrarianLibFoundationModule
import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.item.IFoundationItem
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.data.BlockTagsProvider
import net.minecraft.data.DataGenerator
import net.minecraft.data.ItemTagsProvider
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tags.Tag
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import java.util.function.Function

/**
 * The main class for registering objects in LibrarianLib Foundation. This class manages when to register them, along
 * with any secondary registrations, such as [block items][BlockItem] and block render layers.
 */
class RegistrationManager(val modid: String, modEventBus: IEventBus) {
    init {
        modEventBus.register(this)
    }

    /**
     * The default item group for items registered with this registration manager.
     */
    val itemGroup: ItemGroup = object: ItemGroup(modid) {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(itemGroupIcon?.get() ?: Items.AIR)
        }
    }

    /**
     * The default item group icon for items registered with this registration manager.
     */
    var itemGroupIcon: LazyItem? = null

    private val blocks = mutableListOf<BlockSpec>()
    private val items = mutableListOf<ItemSpec>()
    private val tileEntities = mutableListOf<TileEntitySpec>()

    /**
     * Methods for performing data generation
     */
    val datagen: DataGen = DataGen()

    /**
     * Adds a block to this registration manager and returns a lazy reference to it
     */
    fun add(spec: BlockSpec): LazyBlock {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        blocks.add(spec)
        return spec.lazy
    }

    /**
     * Adds an item to this registration manager and returns a lazy reference to it
     */
    fun add(spec: ItemSpec): LazyItem {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        items.add(spec)
        return spec.lazy
    }

    /**
     * Adds a tile entity type to this registration manager and returns a lazy reference to it
     */
    fun add(spec: TileEntitySpec): LazyTileEntityType {
        spec.modid = modid
        tileEntities.add(spec)
        return spec.lazy
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerBlocks(e: RegistryEvent.Register<Block>) {
        blocks.forEach { block ->
            logger.debug("Manager for $modid: Registering block ${block.registryName}")
            e.registry.register(block.blockInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerItems(e: RegistryEvent.Register<Item>) {
        blocks.forEach { block ->
            if (block.hasItem) {
                logger.debug("Manager for $modid: Registering blockitem ${block.registryName}")
                e.registry.register(block.itemInstance)
            }
        }
        items.forEach { item ->
            logger.debug("Manager for $modid: Registering item ${item.registryName}")
            e.registry.register(item.itemInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerTileEntities(e: RegistryEvent.Register<TileEntityType<*>>) {
        tileEntities.forEach { te ->
            logger.debug("Manager for $modid: Registering TileEntityType ${te.registryName}")
            e.registry.register(te.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun commonSetup(e: FMLCommonSetupEvent) {
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun clientSetup(e: FMLClientSetupEvent) {
        blocks.forEach { block ->
            RenderTypeLookup.setRenderLayer(block.blockInstance, block.renderLayer.getRenderType())
        }
        tileEntities.forEach { spec ->
            val renderer = spec.renderer ?: return@forEach
            logger.debug("Manager for $modid: Registering TER for ${spec.registryName}")
            @Suppress("UNCHECKED_CAST")
            ClientRegistry.bindTileEntityRenderer(spec.typeInstance as TileEntityType<TileEntity>) {
                renderer.applyClient(it) as TileEntityRenderer<TileEntity>
            }
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun dedicatedServerSetup(e: FMLDedicatedServerSetupEvent) {
    }

    @SubscribeEvent
    fun gatherData(e: GatherDataEvent) {
        e.generator.addProvider(BlockStateGeneration(e.generator, e.existingFileHelper))

        val locales = mutableSetOf<String>()
        blocks.forEach { locales.addAll(it.datagen.names.keys) }
        items.forEach { locales.addAll(it.datagen.names.keys) }
        locales.forEach { locale ->
            e.generator.addProvider(LanguageGeneration(e.generator, locale))
        }

        e.generator.addProvider(BlockTagsGeneration(e.generator))
        e.generator.addProvider(ItemTagsGeneration(e.generator))
    }

    inner class DataGen {
        val blockTags: TagGen<Block> = TagGen()
        val itemTags: TagGen<Item> = TagGen()

        inner class TagGen<T> {
            @get:JvmSynthetic
            internal val metaTags = mutableMapOf<Tag<T>, MutableList<Tag<T>>>()

            @get:JvmSynthetic
            internal val valueTags = mutableMapOf<Tag<T>, MutableList<T>>()

            /**
             * Add values to the given tag
             */
            fun add(tag: Tag<T>, vararg values: T): TagGen<T> {
                valueTags.getOrPut(tag) { mutableListOf() }.addAll(values)
                return this
            }

            /**
             * Add tags to the given tag
             */
            fun meta(tag: Tag<T>, vararg tags: Tag<T>): TagGen<T> {
                metaTags.getOrPut(tag) { mutableListOf() }.addAll(tags)
                return this
            }
        }
    }

    private inner class BlockStateGeneration(gen: DataGenerator, exFileHelper: ExistingFileHelper):
        BlockStateProvider(gen, modid, TextureExistsExistingFileHelper(exFileHelper)) {
        override fun registerStatesAndModels() {
            logger.debug("Manager for $modid: Generating blockstates/models")
            blocks.forEach {
                val manualGen = it.datagen.model
                val instance = it.blockInstance
                if (manualGen != null) {
                    logger.debug("Manager for $modid: Calling manual blockstate generator for block ${it.registryName}")
                    manualGen.accept(this)
                } else if(instance is IFoundationBlock) {
                    logger.debug("Manager for $modid: Calling IFoundationBlock blockstate generator for block ${it.registryName}")
                    instance.generateBlockState(this)
                }
            }
            items.forEach {
                val manualGen = it.datagen.model
                val instance = it.itemInstance
                if (manualGen != null) {
                    logger.debug("Manager for $modid: Calling manual model generator for item ${it.registryName}")
                    manualGen.accept(this.itemModels())
                } else if(instance is IFoundationItem) {
                    logger.debug("Manager for $modid: Calling IFoundationItem model generator for item ${it.registryName}")
                    instance.generateItemModel(this.itemModels())
                }
            }
        }
    }

    private inner class LanguageGeneration(gen: DataGenerator, val locale: String):
        LanguageProvider(gen, modid, locale) {
        override fun addTranslations() {
            logger.debug("Manager for $modid: Generating $locale language")
            blocks.forEach { spec ->
                spec.datagen.names[locale]?.also { name ->
                    this.add(spec.blockInstance, name)
                    spec.itemInstance?.also { item ->
                        if (item.translationKey != spec.blockInstance.translationKey)
                            this.add(spec.itemInstance, name)
                    }
                }
            }
            items.forEach { spec ->
                spec.datagen.names[locale]?.also { name ->
                    this.add(spec.itemInstance, name)
                }
            }
        }
    }

    private inner class BlockTagsGeneration(gen: DataGenerator): BlockTagsProvider(gen) {
        override fun registerTags() {
            logger.debug("Manager for $modid: Generating tags")
            blocks.forEach { spec ->
                spec.datagen.tags.forEach { tag ->
                    getBuilder(tag).add(spec.blockInstance)
                }
            }
            datagen.blockTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
            datagen.blockTags.metaTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private inner class ItemTagsGeneration(gen: DataGenerator): ItemTagsProvider(gen) {
        override fun registerTags() {
            blocks.forEach { spec ->
                val item = spec.itemInstance ?: return@forEach
                spec.datagen.itemTags.forEach { tag ->
                    getBuilder(tag).add(item)
                }
            }
            items.forEach { spec ->
                spec.datagen.tags.forEach { tag ->
                    getBuilder(tag).add(spec.itemInstance)
                }
            }
            datagen.itemTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
            datagen.itemTags.metaTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private companion object {
        val logger = LibrarianLibFoundationModule.makeLogger<RegistrationManager>()
    }
}
