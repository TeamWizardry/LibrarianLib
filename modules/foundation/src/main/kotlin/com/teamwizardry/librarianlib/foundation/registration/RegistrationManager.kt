package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.item.IFoundationItem
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.data.BlockTagsProvider
import net.minecraft.data.DataGenerator
import net.minecraft.data.ItemTagsProvider
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tags.Tag
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent

/**
 * The main class for registering objects in LibrarianLib Foundation. This class manages when to register them, along
 * with any secondary registrations, such as [block items][BlockItem] and block render layers.
 */
class RegistrationManager(val modid: String, modEventBus: IEventBus) {
    init {
        modEventBus.register(this)
    }

    val defaultItemGroup: ItemGroup = object: ItemGroup(modid) {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(defaultItemGroupIcon ?: Items.AIR)
        }
    }
    var defaultItemGroupIcon: Item? = null

    private val blocks = mutableListOf<BlockSpec>()
    private val items = mutableListOf<ItemSpec>()

    val datagen: DataGen = DataGen()

    fun add(spec: BlockSpec): BlockSpec {
        spec.verifyComplete()
        spec.modid = modid
        blocks.add(spec)
        return spec
    }

    fun add(spec: ItemSpec): ItemSpec {
        spec.verifyComplete()
        spec.modid = modid
        items.add(spec)
        return spec
    }

    @SubscribeEvent
    internal fun registerBlocks(e: RegistryEvent.Register<Block>) {
        blocks.forEach { block ->
            e.registry.register(block.blockInstance)
        }
    }

    @SubscribeEvent
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    internal fun registerItems(e: RegistryEvent.Register<Item>) {
        blocks.forEach { block ->
            if (block.hasItem) {
                block.itemProperties.group(block.itemGroup.get(this))
                e.registry.register(block.itemInstance)
            }
        }
    }

    @SubscribeEvent
    internal fun commonSetup(e: FMLCommonSetupEvent) {
    }

    @SubscribeEvent
    internal fun clientSetup(e: FMLClientSetupEvent) {
        blocks.forEach { block ->
            RenderTypeLookup.setRenderLayer(block.blockInstance, block.renderLayer.getRenderType())
        }
    }

    @SubscribeEvent
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
            internal val metaTags = mutableMapOf<Tag<T>, MutableList<Tag<T>>>()
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

    private inner class BlockStateGeneration(gen: DataGenerator, exFileHelper: ExistingFileHelper) :
        BlockStateProvider(gen, modid, TextureExistsExistingFileHelper(exFileHelper)) {
        override fun registerStatesAndModels() {
            blocks.forEach {
                (it.blockInstance as? IFoundationBlock)?.generateBlockState(this)
            }
            items.forEach {
                (it.itemInstance as? IFoundationItem)?.generateItemModel(this.itemModels())
            }
        }
    }

    private inner class LanguageGeneration(gen: DataGenerator, val locale: String) :
        LanguageProvider(gen, modid, locale) {
        override fun addTranslations() {
            blocks.forEach { spec ->
                spec.datagen.names[locale]?.also { name ->
                    this.add(spec.blockInstance, name)
                    spec.itemInstance?.also { item ->
                        if(item.translationKey != spec.blockInstance.translationKey)
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

    private inner class BlockTagsGeneration(gen: DataGenerator) : BlockTagsProvider(gen) {
        override fun registerTags() {
            blocks.forEach { spec ->
                spec.datagen.tags.forEach { tag ->
                    getBuilder(tag).add(spec.blockInstance)
                }
            }
            datagen.blockTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private inner class ItemTagsGeneration(gen: DataGenerator) : ItemTagsProvider(gen) {
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
        }
    }
}
