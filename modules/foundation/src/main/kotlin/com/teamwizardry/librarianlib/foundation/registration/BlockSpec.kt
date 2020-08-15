package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.IncompleteBuilderException
import com.teamwizardry.librarianlib.core.util.mapSrgName
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer
import net.minecraft.item.BlockItem
import net.minecraft.item.DyeColor
import net.minecraft.item.Food
import net.minecraft.item.Item
import net.minecraft.item.Rarity
import net.minecraft.tags.Tag
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.ToolType
import java.util.concurrent.Callable
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.function.Function

/**
 * The specs for creating and registering a block.
 */
class BlockSpec(
    /**
     * The registry name, sans mod ID
     */
    val name: String
) {
    /**
     * The mod ID to register this block under. This is populated by the [RegistrationManager].
     */
    var modid: String = ""
        internal set

    /**
     * The registry name of the block. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    val registryName: ResourceLocation
        get() = ResourceLocation(modid, name)

    /**
     * Whether a [BlockItem] should be registered for this block
     */
    var hasItem: Boolean = true
        private set

    /**
     * What render layer this block should be drawn in
     */
    var renderLayer: RenderLayerSpec = RenderLayerSpec.SOLID
        private set

    /**
     * What item group the [BlockItem] should be in
     */
    var itemGroup: ItemGroupSpec = ItemGroupSpec.DEFAULT
        private set

    /**
     * The information used during data generation
     */
    val datagen: DataGen = DataGen()

    private var blockConstructor: Function<BlockSpec, Block>? = null
    private var itemConstructor: Function<BlockSpec, BlockItem>? = null

    /** Disables the registration of a [BlockItem] for this block */
    fun noItem(): BlockSpec = build {
        this.hasItem = false
    }

    /** Sets whether a [BlockItem] should be registered for this block */
    fun hasItem(value: Boolean): BlockSpec = build {
        this.hasItem = value
    }

    /** Sets the render layer this block should draw in */
    fun renderLayer(value: RenderLayerSpec): BlockSpec = build {
        this.renderLayer = value
    }

    /**
     * Sets the item group this block's item should be in
     */
    fun itemGroup(value: ItemGroupSpec): BlockSpec = build {
        this.itemGroup = value
    }

    /**
     * Sets the block constructor for deferred evaluation
     */
    fun block(constructor: Function<BlockSpec, Block>): BlockSpec = build {
        this.blockConstructor = constructor
    }

    /**
     * Sets the block constructor for deferred evaluation
     */
    @JvmSynthetic
    inline fun block(crossinline constructor: (BlockSpec) -> Block): BlockSpec = block(Function { constructor(it) })

    /**
     * Sets the custom [BlockItem] constructor for deferred evaluation
     */
    fun blockItem(constructor: Function<BlockSpec, BlockItem>): BlockSpec = build {
        this.itemConstructor = constructor
    }

    /**
     * Sets the block constructor for deferred evaluation
     */
    @JvmSynthetic
    inline fun blockItem(crossinline constructor: (BlockSpec) -> BlockItem): BlockSpec = blockItem(Function { constructor(it) })

    /**
     * Configures the information used for data generation
     */
    fun datagen(data: Consumer<DataGen>): BlockSpec = build {
        data.accept(this.datagen)
    }

    /**
     * Configures the information used for data generation
     */
    @JvmSynthetic
    inline fun datagen(crossinline data: DataGen.() -> Unit): BlockSpec = datagen(Consumer { it.data() })

    //region block properties
    var blockProperties: Block.Properties = Block.Properties.create(GENERIC_MATERIAL)
        private set
    private var blockPropertiesHasCustomColor: Boolean = false

    /**
     * Applies the supplied properties to this block spec
     */
    fun withProperties(properties: DefaultProperties): BlockSpec = build {
        properties.apply(this)
    }

    fun material(material: Material): BlockSpec = build {
        if (!blockPropertiesHasCustomColor)
            blockPropertiesMapColorMirror.set(blockProperties, material.color)
        blockPropertiesMaterialMirror.set(blockProperties, material)
    }

    fun mapColor(color: MaterialColor): BlockSpec = build {
        blockPropertiesHasCustomColor = true
        blockPropertiesMapColorMirror.set(blockProperties, color)
    }

    fun mapColor(color: DyeColor): BlockSpec = mapColor(color.mapColor)
    fun propertiesFrom(other: Block): BlockSpec = build {
        blockProperties = Block.Properties.from(other)
    }

    fun doesNotBlockMovement(): BlockSpec = build { blockProperties.doesNotBlockMovement() }
    fun notSolid(): BlockSpec = build { blockProperties.notSolid() }
    fun slipperiness(slipperiness: Float): BlockSpec = build { blockProperties.slipperiness(slipperiness) }
    fun speedFactor(factor: Float): BlockSpec = build { blockProperties.speedFactor(factor) }
    fun jumpFactor(factor: Float): BlockSpec = build { blockProperties.jumpFactor(factor) }
    fun sound(soundType: SoundType): BlockSpec = build { blockProperties.sound(soundType) }
    fun lightValue(lightValue: Int): BlockSpec = build { blockProperties.lightValue(lightValue) }
    fun hardnessAndResistance(hardness: Float, resistance: Float): BlockSpec = build { blockProperties.hardnessAndResistance(hardness, resistance) }
    fun hardnessAndResistance(hardnessAndResistance: Float): BlockSpec = build { blockProperties.hardnessAndResistance(hardnessAndResistance) }
    fun tickRandomly(): BlockSpec = build { blockProperties.tickRandomly() }
    fun variableOpacity(): BlockSpec = build { blockProperties.variableOpacity() }
    fun harvestLevel(harvestLevel: Int): BlockSpec = build { blockProperties.harvestLevel(harvestLevel) }
    fun harvestTool(harvestTool: ToolType): BlockSpec = build { blockProperties.harvestTool(harvestTool) }
    fun noDrops(): BlockSpec = build { blockProperties.noDrops() }
    fun lootFrom(other: Block): BlockSpec = build { blockProperties.lootFrom(other) }
    //endregion

    //region item properties
    var itemProperties: Item.Properties = Item.Properties()

    /**
     * Sets this item's food type
     */
    fun food(food: Food): BlockSpec = build { itemProperties.food(food) }

    /**
     * Sets the maximum stack size for this item
     */
    fun maxStackSize(maxStackSize: Int): BlockSpec = build { itemProperties.maxStackSize(maxStackSize) }

    /**
     * Sets the max damage (i.e. durability) of this item. This also implicitly sets the max stack size to 1.
     */
    fun maxDamage(maxDamage: Int): BlockSpec = build { itemProperties.maxDamage(maxDamage) }

    /**
     * Sets the container item for this item. e.g. bucket for a lava bucket, bottle for a dragon's breath, etc. This is
     * the item left behind in the crafting grid after a recipe completes.
     */
    fun containerItem(containerItem: Item): BlockSpec = build { itemProperties.containerItem(containerItem) }

    /**
     * Sets this item's rarity
     */
    fun rarity(rarity: Rarity): BlockSpec = build { itemProperties.rarity(rarity) }

    /**
     * Removes the ability to repair this item
     */
    fun setNoRepair(): BlockSpec = build { itemProperties.setNoRepair() }

    /**
     * Sets the tool level of this item
     */
    fun addToolType(type: ToolType, level: Int): BlockSpec = build { itemProperties.addToolType(type, level) }

    /**
     * Sets the [ItemStackTileEntityRenderer]. Note that [IBakedModel.isBuiltInRenderer] must return true for this to
     * be used.
     */
    fun setISTER(ister: Supplier<Callable<ItemStackTileEntityRenderer>>): BlockSpec = build { itemProperties.setISTER(ister) }
    //endregion

    /**
     * The lazily-evaluated [Block] instance
     */
    val blockInstance: Block by lazy {
        val constructor = blockConstructor ?: throw IncompleteBuilderException(listOf("constructor"))
        constructor.apply(this).setRegistryName(registryName)
    }

    /**
     * The lazily-evaluated [BlockItem] instance
     */
    val itemInstance: Item? by lazy {
        if (!hasItem) return@lazy null
        (itemConstructor?.apply(this) ?: BlockItem(blockInstance, itemProperties)).setRegistryName(registryName)
    }

    internal fun verifyComplete() {
        val missing = mapOf("constructor" to blockConstructor).filter { it.value == null }.keys
        if (missing.isNotEmpty()) {
            throw IncompleteBuilderException(missing.toList())
        }
    }

    /**
     * Information used when generating data
     */
    class DataGen {
        val names: MutableMap<String, String> = mutableMapOf()
        val tags: MutableSet<Tag<Block>> = mutableSetOf()
        val itemTags: MutableSet<Tag<Item>> = mutableSetOf()

        /**
         * Sets the name of this block in the generated en_us lang file
         */
        fun name(name: String): DataGen = name("en_us", name)

        /**
         * Sets the name of this block in the generated lang file
         */
        fun name(locale: String, name: String): DataGen {
            this.names[locale] = name
            return this
        }

        /**
         * Adds the passed tags to this block
         */
        fun tags(vararg tags: Tag<Block>): DataGen {
            this.tags.addAll(tags)
            return this
        }

        /**
         * Adds the passed tags to this block's item
         */
        fun itemTags(vararg tags: Tag<Item>): DataGen {
            this.itemTags.addAll(tags)
            return this
        }
    }

    private inline fun build(block: () -> Unit): BlockSpec {
        block()
        return this
    }

    companion object {
        private val blockPropertiesMaterialMirror = Mirror.reflectClass<Block.Properties>()
            .getDeclaredField(mapSrgName("field_200953_a")) // material
        private val blockPropertiesMapColorMirror = Mirror.reflectClass<Block.Properties>()
            .getDeclaredField(mapSrgName("field_200954_b")) // mapColor
        private val GENERIC_MATERIAL = Material(
            MaterialColor.AIR, // materialMapColor
            false, // liquid
            true, // solid
            true, // doesBlockMovement
            true, // opaque
            true, // requiresNoTool
            false, // canBurn
            false, // replaceable
            PushReaction.NORMAL // mobilityFlag
        )
    }
}
