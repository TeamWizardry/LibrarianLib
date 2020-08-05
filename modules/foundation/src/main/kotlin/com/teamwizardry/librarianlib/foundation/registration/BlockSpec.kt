package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.IncompleteBuilderException
import com.teamwizardry.librarianlib.core.util.mapSrgName
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer
import net.minecraft.item.BlockItem
import net.minecraft.item.DyeColor
import net.minecraft.item.Food
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Rarity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.ToolType
import java.util.concurrent.Callable
import java.util.function.Supplier

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
    private var blockConstructor: ((BlockSpec) -> Block)? = null

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

    /** Disables the registration of a [BlockItem] for this block */
    fun noItem() = build {
        this.hasItem = false
    }

    /** Sets whether a [BlockItem] should be registered for this block */
    fun hasItem(value: Boolean) = build {
        this.hasItem = value
    }

    /** Sets the render layer this block should draw in */
    fun renderLayer(value: RenderLayerSpec) = build {
        this.renderLayer = value
    }

    /** Sets the item group this block's item should be in */
    fun itemGroup(value: ItemGroupSpec) = build {
        this.itemGroup = value
    }

    /**
     * Sets the block constructor for deferred evaluation
     */
    fun block(constructor: (BlockSpec) -> Block) = build {
        this.blockConstructor = constructor
    }

    //region block properties
    var blockProperties: Block.Properties = Block.Properties.create(GENERIC_MATERIAL)
        private set
    private var blockPropertiesHasCustomColor: Boolean = false

    fun material(material: Material) {
        if (!blockPropertiesHasCustomColor)
            blockPropertiesMapColorMirror.set(blockProperties, material.color)
        blockPropertiesMaterialMirror.set(blockProperties, material)
    }

    fun mapColor(color: MaterialColor) = build {
        blockPropertiesHasCustomColor = true
        blockPropertiesMapColorMirror.set(blockProperties, color)
    }

    fun mapColor(color: DyeColor) = mapColor(color.mapColor)
    fun propertiesFrom(other: Block) = build {
        blockProperties = Block.Properties.from(other)
    }

    fun doesNotBlockMovement() = build { blockProperties.doesNotBlockMovement() }
    fun notSolid() = build { blockProperties.notSolid() }
    fun slipperiness(slipperiness: Float) = build { blockProperties.slipperiness(slipperiness) }
    fun speedFactor(factor: Float) = build { blockProperties.speedFactor(factor) }
    fun jumpFactor(factor: Float) = build { blockProperties.jumpFactor(factor) }
    fun sound(soundType: SoundType) = build { blockProperties.sound(soundType) }
    fun lightValue(lightValue: Int) = build { blockProperties.lightValue(lightValue) }
    fun hardnessAndResistance(hardness: Float, resistance: Float) = build { blockProperties.hardnessAndResistance(hardness, resistance) }
    fun hardnessAndResistance(hardnessAndResistance: Float) = build { blockProperties.hardnessAndResistance(hardnessAndResistance) }
    fun tickRandomly() = build { blockProperties.tickRandomly() }
    fun variableOpacity() = build { blockProperties.variableOpacity() }
    fun harvestLevel(harvestLevel: Int) = build { blockProperties.harvestLevel(harvestLevel) }
    fun harvestTool(harvestTool: ToolType) = build { blockProperties.harvestTool(harvestTool) }
    fun noDrops() = build { blockProperties.noDrops() }
    fun lootFrom(other: Block) = build { blockProperties.lootFrom(other) }
    //endregion

    //region item properties
    var itemProperties: Item.Properties = Item.Properties()

    fun food(food: Food) = build { itemProperties.food(food) }
    fun maxStackSize(maxStackSize: Int) = build { itemProperties.maxStackSize(maxStackSize) }
    fun defaultMaxDamage(maxDamage: Int) = build { itemProperties.defaultMaxDamage(maxDamage) }
    fun maxDamage(maxDamage: Int) = build { itemProperties.maxDamage(maxDamage) }
    fun containerItem(containerItem: Item) = build { itemProperties.containerItem(containerItem) }
    fun rarity(rarity: Rarity) = build { itemProperties.rarity(rarity) }
    fun setNoRepair() = build { itemProperties.setNoRepair() }
    fun addToolType(type: ToolType, level: Int) = build { itemProperties.addToolType(type, level) }
    fun setISTER(ister: Supplier<Callable<ItemStackTileEntityRenderer>>) = build { itemProperties.setISTER(ister) }
    //endregion

    internal val blockInstance: Block by lazy {
        val constructor = blockConstructor ?: throw IncompleteBuilderException(listOf("constructor"))
        val instance = constructor.invoke(this)

        instance.registryName = registryName

        instance
    }

    internal val itemInstance: Item? by lazy {
        if(!hasItem) return@lazy null
        BlockItem(blockInstance, itemProperties).setRegistryName(registryName)
    }

    internal fun verifyComplete() {
        val missing = mapOf("constructor" to blockConstructor).filter { it.value == null }.keys
        if(missing.isNotEmpty()) {
            throw IncompleteBuilderException(missing.toList())
        }
    }

    private inline fun build(block: () -> Unit): BlockSpec {
        block()
        return this
    }

    companion object {
        private val blockPropertiesMaterialMirror = Mirror.reflectClass<Block.Properties>()
            .getDeclaredField(mapSrgName("field_200953_a")) // materials
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
