package com.teamwizardry.librarianlib.foundation.registration

import com.mojang.datafixers.util.Pair
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.foundation.LibrarianLibFoundationModule
import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.bridge.FoundationSignTileEntityCreator
import com.teamwizardry.librarianlib.foundation.item.IFoundationItem
import com.teamwizardry.librarianlib.foundation.loot.BlockLootTableGenerator
import com.teamwizardry.librarianlib.foundation.loot.LootTableGenerator
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.data.BlockTagsProvider
import net.minecraft.data.DataGenerator
import net.minecraft.data.ItemTagsProvider
import net.minecraft.data.LootTableProvider
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.*
import net.minecraft.tags.Tag
import net.minecraft.tileentity.SignTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.LootParameterSet
import net.minecraft.world.storage.loot.LootTable
import net.minecraft.world.storage.loot.LootTableManager
import net.minecraft.world.storage.loot.ValidationTracker
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * The main class for registering objects in LibrarianLib Foundation. This class manages when to register them, along
 * with any secondary registrations, such as [block items][BlockItem] and block render layers.
 */
public class RegistrationManager(public val modid: String, modEventBus: IEventBus) {
    init {
        modEventBus.register(this)
    }

    /**
     * The default item group for items registered with this registration manager.
     */
    public val itemGroup: ItemGroup = object: ItemGroup(modid) {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(itemGroupIcon?.get() ?: Items.AIR)
        }
    }

    private val logger = LibrarianLibFoundationModule.makeLogger("RegistrationManager for $modid")

    /**
     * The default item group icon for items registered with this registration manager.
     */
    public var itemGroupIcon: LazyItem? = null

    private val blocks = mutableListOf<BlockSpec>()
    private val items = mutableListOf<ItemSpec>()
    private val tileEntities = mutableListOf<TileEntitySpec<*>>()
    private val entities = mutableListOf<EntitySpec<*>>()
    private val capabilities = mutableListOf<CapabilitySpec<*>>()
    private val containers = mutableListOf<ContainerSpec<*>>()
    private val atlasSprites = mutableMapOf<ResourceLocation, MutableSet<ResourceLocation>>()

    /**
     * Methods for performing data generation
     */
    public val datagen: DataGen = DataGen()

    /**
     * Adds a block to this registration manager and returns a lazy reference to it
     */
    public fun add(spec: BlockSpec): LazyBlock {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        blocks.add(spec)
        return spec.lazy
    }

    /**
     * Adds an item to this registration manager and returns a lazy reference to it
     */
    public fun add(spec: ItemSpec): LazyItem {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        items.add(spec)
        return spec.lazy
    }

    /**
     * Adds a tile entity type to this registration manager and returns a lazy reference to it
     */
    public fun <T: TileEntity> add(spec: TileEntitySpec<T>): LazyTileEntityType<T> {
        spec.modid = modid
        tileEntities.add(spec)
        return spec.lazy
    }

    /**
     * Adds an entity type to this registration manager and returns a lazy reference to it
     */
    public fun <T: Entity> add(spec: EntitySpec<T>): LazyEntityType<T> {
        spec.modid = modid
        entities.add(spec)
        return spec.lazy
    }

    /**
     * Adds a capability type to this registration manager. To access the resulting capability, use
     * [@CapabilityInject][CapabilityInject].
     */
    public fun <T> add(spec: CapabilitySpec<T>) {
        capabilities.add(spec)
    }

    /**
     * Adds a container type to this registration manager and returns a lazy reference to it
     */
    public fun <T: FacadeContainer> add(spec: ContainerSpec<T>): LazyContainerType<T> {
        spec.modid = modid
        containers.add(spec)
        return spec.lazy
    }

    public fun addAtlasSprite(atlas: ResourceLocation, sprite: ResourceLocation) {
        atlasSprites.getOrPut(atlas) { mutableSetOf() }.add(sprite)
    }

    /**
     * A lazy TE type used by built-in block collections
     */
    @get:JvmSynthetic
    internal val foundationSignTileEntityType: LazyTileEntityType<SignTileEntity> by lazy {
        if(tileEntities.any { it.id == "sign" })
            throw IllegalStateException("Tile entity type `$modid:sign` already configured")
        this.add(
            TileEntitySpec("sign") { FoundationSignTileEntityCreator.create(foundationSignTileEntityType.get()) }
                .renderer { SignTileEntityRenderer(it) }
        )
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerBlocks(e: RegistryEvent.Register<Block>) {
        for(block in blocks) {
            logger.debug("Registering block ${block.registryName}")
            e.registry.register(block.blockInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerItems(e: RegistryEvent.Register<Item>) {
        for(block in blocks) {
            if (block.hasItem) {
                logger.debug("Registering blockitem ${block.registryName}")
                e.registry.register(block.itemInstance)
            }
        }
        for(item in items) {
            logger.debug("Registering item ${item.registryName}")
            e.registry.register(item.itemInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerTileEntities(e: RegistryEvent.Register<TileEntityType<*>>) {
        for(te in tileEntities) {
            logger.debug("Registering TileEntityType ${te.registryName}")
            e.registry.register(te.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
        for(type in entities) {
            logger.debug("Registering EntityType ${type.registryName}")
            e.registry.register(type.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerContainers(e: RegistryEvent.Register<ContainerType<*>>) {
        for(type in containers) {
            logger.debug("Registering FacadeContainerType ${type.registryName}")
            e.registry.register(type.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun commonSetup(e: FMLCommonSetupEvent) {
        for(cap in capabilities) {
            @Suppress("UNCHECKED_CAST")
            cap as CapabilitySpec<Any> // appease the type checking gods
            CapabilityManager.INSTANCE.register(cap.type, cap.storage, cap.defaultImpl)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun clientSetup(e: FMLClientSetupEvent) {
        for (block in blocks) {
            RenderTypeLookup.setRenderLayer(block.blockInstance, block.renderLayer.getRenderType())
        }
        for (spec in tileEntities) {
            val renderer = spec.renderer ?: continue
            logger.debug("Registering TileEntityRenderer for ${spec.registryName}")
            @Suppress("UNCHECKED_CAST")
            ClientRegistry.bindTileEntityRenderer(spec.typeInstance as TileEntityType<TileEntity>) {
                renderer.applyClient(it) as TileEntityRenderer<TileEntity>
            }
        }
        for (spec in entities) {
            val factory = spec.renderFactory ?: continue
            logger.debug("Registering EntityRenderer for ${spec.registryName}")
            @Suppress("UNCHECKED_CAST")
            RenderingRegistry.registerEntityRenderingHandler(spec.typeInstance as EntityType<Entity>) {
                factory.applyClient(it) as EntityRenderer<Entity>
            }
        }
        for(spec in containers) {
            val factory = spec.screenFactory
            logger.debug("Registering ContainerScreen factory for ${spec.registryName}")
            @Suppress("UNCHECKED_CAST")
            ScreenManager.registerFactory(spec.typeInstance as ContainerType<FacadeContainer>) { container, inventory, title ->
                (factory as ContainerSpec.ContainerScreenFactory<FacadeContainer>).create(container, inventory, title)
            }
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun dedicatedServerSetup(e: FMLDedicatedServerSetupEvent) {
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun stitchAtlasSprites(e: TextureStitchEvent.Pre) {
        (atlasSprites[e.map.textureLocation] ?: return).forEach { sprite ->
            e.addSprite(sprite)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun gatherData(e: GatherDataEvent) {
        e.generator.addProvider(BlockStateGeneration(e.generator, FoundationExistingFileHelper(e.existingFileHelper)))

        val locales = mutableSetOf<String>()
        for (spec in blocks) {
            locales.addAll(spec.datagen.names.keys)
        }
        for (spec in items) {
            locales.addAll(spec.datagen.names.keys)
        }
        for (locale in locales) {
            e.generator.addProvider(LanguageGeneration(e.generator, locale))
        }

        e.generator.addProvider(BlockTagsGeneration(e.generator))
        e.generator.addProvider(ItemTagsGeneration(e.generator))

        e.generator.addProvider(LootTableGeneration(e.generator))
    }

    public class DataGen {
        @get:JvmSynthetic
        internal val lootTableGenerators = mutableListOf<LootTableGenerator>()

        public val blockTags: BlockTagGen = BlockTagGen()
        public val itemTags: ItemTagGen = ItemTagGen()

        public fun add(lootTableGenerator: LootTableGenerator) {
            lootTableGenerators.add(lootTableGenerator)
        }

        public class BlockTagGen {
            @get:JvmSynthetic
            internal val metaTags = mutableMapOf<Tag<Block>, MutableList<Tag<Block>>>()

            @get:JvmSynthetic
            internal val valueTags = mutableMapOf<Tag<Block>, MutableList<Block>>()

            @get:JvmSynthetic
            internal val itemForms = mutableMapOf<Tag<Block>, MutableList<Tag<Item>>>()

            /**
             * Add values to the given tag
             */
            public fun add(tag: Tag<Block>, vararg values: Block): BlockTagGen {
                valueTags.getOrPut(tag) { mutableListOf() }.addAll(values)
                return this
            }

            /**
             * Add tags to the given tag
             */
            public fun meta(tag: Tag<Block>, vararg tags: Tag<Block>): BlockTagGen {
                metaTags.getOrPut(tag) { mutableListOf() }.addAll(tags)
                return this
            }

            /**
             * Copies the contents of the given block tag into the specified item tag
             */
            public fun addItemForm(blockTag: Tag<Block>, itemForm: Tag<Item>): BlockTagGen {
                itemForms.getOrPut(blockTag) { mutableListOf() }.add(itemForm)
                return this
            }
        }

        public class ItemTagGen {
            @get:JvmSynthetic
            internal val metaTags = mutableMapOf<Tag<Item>, MutableList<Tag<Item>>>()

            @get:JvmSynthetic
            internal val valueTags = mutableMapOf<Tag<Item>, MutableList<Item>>()

            /**
             * Add values to the given tag
             */
            public fun add(tag: Tag<Item>, vararg values: Item): ItemTagGen {
                valueTags.getOrPut(tag) { mutableListOf() }.addAll(values)
                return this
            }

            /**
             * Add tags to the given tag
             */
            public fun meta(tag: Tag<Item>, vararg tags: Tag<Item>): ItemTagGen {
                metaTags.getOrPut(tag) { mutableListOf() }.addAll(tags)
                return this
            }
        }
    }

    private inner class BlockStateGeneration(
        gen: DataGenerator,
        existingFileHelper: FoundationExistingFileHelper
    ): BlockStateProvider(gen, modid, existingFileHelper) {
        init {
            existingFileHelper.modelProviders.add(models())
            existingFileHelper.modelProviders.add(itemModels())
        }

        override fun registerStatesAndModels() {
            logger.debug("Generating blockstates/models")
            for (spec in blocks) {
                val manualGen = spec.datagen.model
                val instance = spec.blockInstance
                if (manualGen != null) {
                    logger.debug("Calling manual blockstate generator for block ${spec.registryName}")
                    manualGen.accept(this)
                } else if (instance is IFoundationBlock) {
                    logger.debug("Calling IFoundationBlock blockstate generator for block ${spec.registryName}")
                    instance.generateBlockState(this)
                }

                val manualItemGen = spec.datagen.itemModel
                val itemInstance = spec.itemInstance
                if (manualItemGen != null) {
                    logger.debug("Calling manual item model generator for block ${spec.registryName}")
                    manualItemGen.accept(this.itemModels())
                } else if (itemInstance is IFoundationItem) {
                    logger.debug("Calling IFoundationItem model generator for block item ${spec.registryName}")
                    itemInstance.generateItemModel(this.itemModels())
                }
            }

            for (spec in items) {
                val manualGen = spec.datagen.model
                val instance = spec.itemInstance
                if (manualGen != null) {
                    logger.debug("Calling manual model generator for item ${spec.registryName}")
                    manualGen.accept(this.itemModels())
                } else if (instance is IFoundationItem) {
                    logger.debug("Calling IFoundationItem model generator for item ${spec.registryName}")
                    instance.generateItemModel(this.itemModels())
                }
            }
        }
    }

    private inner class LanguageGeneration(gen: DataGenerator, val locale: String):
        LanguageProvider(gen, modid, locale) {
        override fun addTranslations() {
            logger.debug("Generating $locale language")

            for (spec in blocks) {
                val name = spec.datagen.names[locale] ?: continue
                this.add(spec.blockInstance, name)
                spec.itemInstance?.also { item ->
                    if (item.translationKey != spec.blockInstance.translationKey)
                        this.add(spec.itemInstance, name)
                }
            }

            for (spec in items) {
                spec.datagen.names[locale]?.also { name ->
                    this.add(spec.itemInstance, name)
                }
            }
        }
    }

    private inner class BlockTagsGeneration(gen: DataGenerator): BlockTagsProvider(gen) {
        override fun registerTags() {
            logger.debug("Generating tags")
            for (spec in blocks) {
                for(tag in spec.datagen.tags) {
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
            for (spec in blocks) {
                val item = spec.itemInstance ?: continue
                for(tag in spec.datagen.itemTags) {
                    getBuilder(tag).add(item)
                }
            }

            for (spec in items) {
                for(tag in spec.datagen.tags) {
                    getBuilder(tag).add(spec.itemInstance)
                }
            }

            datagen.itemTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }

            for((blockTag, itemTags) in datagen.blockTags.itemForms) {
                for(itemTag in itemTags) {
                    this.copy(blockTag, itemTag)
                }
            }

            datagen.itemTags.metaTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private inner class LootTableGeneration(gen: DataGenerator): LootTableProvider(gen) {
        override fun getTables(): List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> {

            val blockTableGenerator = BlockLootTableGenerator()
            for (spec in blocks) {
                val manualGen = spec.datagen.lootTable.generator
                val instance = spec.blockInstance
                if (manualGen != null) {
                    logger.debug("Calling manual loot table generator for block ${spec.registryName}")
                    manualGen.accept(blockTableGenerator)
                } else if (instance is IFoundationBlock) {
                    logger.debug("Calling IFoundationBlock loot table generator for block ${spec.registryName}")
                    instance.generateLootTable(blockTableGenerator)
                }
            }

            return (listOf(blockTableGenerator) + datagen.lootTableGenerators).map {
                Pair.of(it.lootTableProviderSupplier, it.parameterSet)
            }
        }

        override fun validate(map: MutableMap<ResourceLocation, LootTable>, validationtracker: ValidationTracker) {
            for ((name, table) in map) {
                LootTableManager.func_227508_a_(validationtracker, name, table)
            }
        }
    }
}
