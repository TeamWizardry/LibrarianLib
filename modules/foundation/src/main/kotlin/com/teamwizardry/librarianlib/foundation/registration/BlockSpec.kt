package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.block.BaseBlock
import com.teamwizardry.librarianlib.foundation.block.BlockPropertiesBuilder
import com.teamwizardry.librarianlib.foundation.block.FoundationBlockProperties
import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.item.IFoundationItem
import com.teamwizardry.librarianlib.foundation.loot.BlockLootTableGenerator
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer
import net.minecraft.item.*
import net.minecraft.tags.ITag
import net.minecraft.tags.Tag
import net.minecraft.util.IItemProvider
import net.minecraft.util.Identifier
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.ToolType
import java.util.concurrent.Callable
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * The specs for creating and registering a block. The [BlockItem] instance is generated using the callback provided to
 * [blockItem], or the block if it's an [IFoundationBlock], or [BlockItem]. Item generation can be completely disabled
 * using [noItem].
 */
public class BlockSpec(
    /**
     * The registry name, sans mod ID
     */
    public var id: String
): IItemProvider, BlockPropertiesBuilder<BlockSpec> {
    /**
     * The mod ID to register this block under. This is populated by the [RegistrationManager].
     */
    public var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the block. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    public val registryName: Identifier
        get() = Identifier(modid, id)

    /**
     * Whether a [BlockItem] should be registered for this block
     */
    public var hasItem: Boolean = true
        private set

    /**
     * What render layer this block should be drawn in
     */
    public var renderLayer: RenderLayerSpec = RenderLayerSpec.SOLID
        private set

    /**
     * What item group the [BlockItem] should be in
     */
    public var itemGroup: ItemGroupSpec = ItemGroupSpec.DEFAULT
        private set

    /**
     * The information used during data generation
     */
    public val datagen: DataGen = DataGen()

    private var blockConstructor: Function<BlockSpec, Block> = Function { BaseBlock(it.blockProperties) }
    private var itemConstructor: Function<BlockSpec, BlockItem>? = null

    /** Disables the registration of a [BlockItem] for this block */
    public fun noItem(): BlockSpec = build {
        this.hasItem = false
    }

    /** Sets whether a [BlockItem] should be registered for this block */
    public fun hasItem(value: Boolean): BlockSpec = build {
        this.hasItem = value
    }

    /** Sets the render layer this block should draw in */
    public fun renderLayer(value: RenderLayerSpec): BlockSpec = build {
        this.renderLayer = value
    }

    /**
     * Sets the item group this block's item should be in
     */
    public fun itemGroup(value: ItemGroupSpec): BlockSpec = build {
        this.itemGroup = value
    }

    /**
     * Sets the block constructor for deferred evaluation
     */
    public fun block(constructor: Function<BlockSpec, Block>): BlockSpec = build {
        this.blockConstructor = constructor
    }

    /**
     * Sets the custom [BlockItem] constructor for deferred evaluation
     */
    public fun blockItem(constructor: Function<BlockSpec, BlockItem>): BlockSpec = build {
        this.itemConstructor = constructor
    }

    public fun tileEntity(type: LazyTileEntityType<*>): BlockSpec = build {
        val tileSpec = type.spec
            ?: if (type.typeInstance == null)
                throw IllegalStateException("Can't add a block to a LazyTileEntityType that isn't initialized")
            else
                throw IllegalArgumentException("Can't add a block to a LazyTileEntityType that isn't backed by a Foundation TileEntitySpec")
        tileSpec._validBlocks.add(this.lazy)
    }

    /**
     * Configures the information used for data generation
     */
    public fun datagen(data: Consumer<DataGen>): BlockSpec = build {
        data.accept(this.datagen)
    }

    /**
     * Configures the information used for data generation
     */
    @JvmSynthetic
    public inline fun datagen(crossinline data: DataGen.() -> Unit): BlockSpec = datagen(Consumer { it.data() })

    public override val blockProperties: FoundationBlockProperties = FoundationBlockProperties()

    //region item properties
    public var itemProperties: Item.Properties = Item.Properties()

    /**
     * Sets this item's food type
     */
    public fun food(food: Food): BlockSpec = build { itemProperties.food(food) }

    /**
     * Sets the maximum stack size for this item
     */
    public fun maxStackSize(maxStackSize: Int): BlockSpec = build { itemProperties.maxStackSize(maxStackSize) }

    /**
     * Sets the max damage (i.e. durability) of this item. This also implicitly sets the max stack size to 1.
     */
    public fun maxDamage(maxDamage: Int): BlockSpec = build { itemProperties.maxDamage(maxDamage) }

    /**
     * Sets the container item for this item. e.g. bucket for a lava bucket, bottle for a dragon's breath, etc. This is
     * the item left behind in the crafting grid after a recipe completes.
     */
    public fun containerItem(containerItem: Item): BlockSpec = build { itemProperties.containerItem(containerItem) }

    /**
     * Sets this item's rarity
     */
    public fun rarity(rarity: Rarity): BlockSpec = build { itemProperties.rarity(rarity) }

    /**
     * Removes the ability to repair this item
     */
    public fun setNoRepair(): BlockSpec = build { itemProperties.setNoRepair() }

    /**
     * Sets the tool level of this item
     */
    public fun addToolType(type: ToolType, level: Int): BlockSpec = build { itemProperties.addToolType(type, level) }

    /**
     * Sets the [ItemStackTileEntityRenderer]. Note that [IBakedModel.isBuiltInRenderer] must return true for this to
     * be used.
     */
    public fun setISTER(ister: Supplier<Callable<ItemStackTileEntityRenderer>>): BlockSpec =
        build { itemProperties.setISTER(ister) }
    //endregion

    /**
     * The lazily-evaluated [Block] instance
     */
    public val blockInstance: Block by lazy {
        try {
            blockConstructor.apply(this).setRegistryName(registryName)
        } catch (e: Exception) {
            throw RuntimeException("Error instantiating block $registryName", e)
        }
    }

    /**
     * The lazily-evaluated [BlockItem] instance
     */
    public val itemInstance: Item? by lazy {
        if (!hasItem) return@lazy null
        try {
            val item = itemConstructor?.apply(this)
                ?: (blockInstance as? IFoundationBlock)?.createBlockItem(itemProperties)
                ?: BlockItem(blockInstance, itemProperties)
            item.setRegistryName(registryName)
        } catch (e: Exception) {
            throw RuntimeException("Error instantiating block item $registryName", e)
        }
    }

    override fun asItem(): Item {
        return itemInstance ?: throw IllegalStateException("BlockSpec doesn't have an item")
    }

    public val lazy: LazyBlock = LazyBlock(this)

    /**
     * Information used when generating data
     */
    public inner class DataGen {
        @get:JvmSynthetic
        internal var model: Consumer<BlockStateProvider>? = null
            private set

        @get:JvmSynthetic
        internal var itemModel: Consumer<ItemModelProvider>? = null
            private set

        @get:JvmSynthetic
        internal val names: MutableMap<String, String> = mutableMapOf()

        @get:JvmSynthetic
        internal val tags: MutableSet<ITag.INamedTag<Block>> = mutableSetOf()

        @get:JvmSynthetic
        internal val itemTags: MutableSet<ITag.INamedTag<Item>> = mutableSetOf()

        /**
         * The loot table generation options
         */
        public val lootTable: LootDataGen = LootDataGen()

        /**
         * The block instance, for use in data generators
         */
        public val block: Block
            get() = lazy.get()

        /**
         * The item instance, for use in data generators
         */
        public val item: Item?
            get() = itemInstance

        /**
         * Sets the model generation function. Note: this will override [IFoundationBlock.generateBlockState].
         */
        public fun model(model: Consumer<BlockStateProvider>): DataGen {
            this.model = model
            return this
        }

        /**
         * Sets the model generation function. Note: this will override [IFoundationBlock.generateBlockState].
         */
        @JvmSynthetic
        public inline fun model(crossinline model: BlockStateProvider.() -> Unit): DataGen =
            model(Consumer { it.model() })

        /**
         * Sets the item model generation function. Note: this will override [IFoundationItem.generateItemModel].
         */
        public fun itemModel(model: Consumer<ItemModelProvider>): DataGen {
            this.itemModel = model
            return this
        }

        /**
         * Sets the item model generation function. Note: this will override [IFoundationItem.generateItemModel].
         */
        @JvmSynthetic
        public inline fun itemModel(crossinline model: ItemModelProvider.() -> Unit): DataGen =
            itemModel(Consumer { it.model() })

        /**
         * Sets the model generation function to create a simple cube block using the texture at
         * `yourmodid:block/blockid.png`.
         */
        public fun simpleModel(): DataGen = model { simpleBlock(blockInstance) }

        /**
         * Sets the name of this block in the generated en_us lang file
         */
        public fun name(name: String): DataGen = name("en_us", name)

        /**
         * Sets the name of this block in the generated lang file
         */
        public fun name(locale: String, name: String): DataGen {
            this.names[locale] = name
            return this
        }

        /**
         * Adds the passed tags to this block
         */
        public fun tags(vararg tags: ITag.INamedTag<Block>): DataGen {
            this.tags.addAll(tags)
            return this
        }

        /**
         * Adds the passed tags to this block's item
         */
        public fun itemTags(vararg tags: ITag.INamedTag<Item>): DataGen {
            this.itemTags.addAll(tags)
            return this
        }
    }

    public inner class LootDataGen {
        @get:JvmSynthetic
        internal var generator: Consumer<BlockLootTableGenerator>? = null
            private set

        /**
         * Sets the loot table generation function. Note: this will override [IFoundationBlock.generateLootTable].
         */
        public fun custom(loot: Consumer<BlockLootTableGenerator>) {
            this.generator = loot
        }

        /**
         * Sets the loot table generation function. Note: this will override [IFoundationBlock.generateLootTable].
         */
        @JvmSynthetic
        public inline fun custom(crossinline loot: BlockLootTableGenerator.() -> Unit) {
            custom(Consumer { it.loot() })
        }

        // shortcuts:
        // - drop self
        // - silk touch
    }

    private inline fun build(block: () -> Unit): BlockSpec {
        block()
        return this
    }
}
