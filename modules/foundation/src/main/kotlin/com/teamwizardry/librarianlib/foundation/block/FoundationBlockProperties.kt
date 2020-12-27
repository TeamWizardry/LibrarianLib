package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FireBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraft.world.storage.loot.LootTables
import net.minecraftforge.common.ToolType
import java.util.function.Supplier
import kotlin.math.max

/**
 * Similar to [Block.Properties], but adds flammability and the ability to compose properties using [applyFrom].
 */
public class FoundationBlockProperties {
    // Vanilla properties
    private var material: Material? = null
    private var mapColor: MaterialColor? = null
    private var blocksMovement: Boolean? = null // default: true
    private var soundType: SoundType? = null // default: SoundType.STONE
    private var lightValue: Int? = null // default: 0
    private var hardnessAndResistance: Pair<Float, Float>? = null // default: 0f, 0f
    private var ticksRandomly: Boolean? = null // default: false
    private var slipperiness: Float? = null // default: 0.6f
    private var speedFactor: Float? = null // default: 1.0f
    private var jumpFactor: Float? = null // default: 1.0f

    /** Sets loot table information  */
    private var noDrops: Boolean? = null // default: false
    private var isSolid: Boolean? = null // default: true
    private var variableOpacity: Boolean? = null // default: false
    private var harvestLevel: Int? = null // default: -1
    private var harvestTool: ToolType? = null
    private var lootFrom: Block? = null

    // Foundation properties
    private var flammability: Int? = null
    private var fireSpreadSpeed: Int? = null

    //region Builders
    public fun material(value: Material?): FoundationBlockProperties = build {
        this.material = value
        if(this.mapColor == null)
            this.mapColor = value?.color
    }

    public fun mapColor(value: MaterialColor?): FoundationBlockProperties = build { this.mapColor = value }

    public fun doesNotBlockMovement(): FoundationBlockProperties = build {
        this.blocksMovement(false)
    }

    public fun blocksMovement(value: Boolean): FoundationBlockProperties = build {
        this.blocksMovement = value
        this.isSolid = (isSolid ?: true) && value
    }

    public fun notSolid(): FoundationBlockProperties = build { this.isSolid(false) }

    public fun isSolid(value: Boolean): FoundationBlockProperties = build { this.isSolid = value }

    public fun slipperiness(slipperiness: Float): FoundationBlockProperties = build { this.slipperiness = slipperiness }

    public fun speedFactor(factor: Float): FoundationBlockProperties = build { this.speedFactor = factor }

    public fun jumpFactor(factor: Float): FoundationBlockProperties = build { this.jumpFactor = factor }

    public fun sound(soundType: SoundType?): FoundationBlockProperties = build { this.soundType = soundType }

    public fun lightValue(lightValue: Int): FoundationBlockProperties = build { this.lightValue = lightValue }

    public fun hardnessAndResistance(hardness: Float, resistance: Float): FoundationBlockProperties = build {
        this.hardnessAndResistance = hardness to max(0.0f, resistance)
    }

    public fun hardnessAndResistance(hardnessAndResistance: Float): FoundationBlockProperties = build {
        this.hardnessAndResistance(hardnessAndResistance, hardnessAndResistance)
    }

    public fun tickRandomly(): FoundationBlockProperties = build { this.ticksRandomly(true) }

    public fun ticksRandomly(value: Boolean): FoundationBlockProperties = build { this.ticksRandomly = value }

    public fun variableOpacity(): FoundationBlockProperties = build { this.variableOpacity(true) }

    public fun variableOpacity(value: Boolean): FoundationBlockProperties = build { this.variableOpacity = value }

    public fun harvestLevel(harvestLevel: Int): FoundationBlockProperties = build { this.harvestLevel = harvestLevel }

    public fun harvestTool(harvestTool: ToolType?): FoundationBlockProperties = build { this.harvestTool = harvestTool }

    public fun noDrops(): FoundationBlockProperties = build { this.noDrops(true) }

    public fun noDrops(value: Boolean): FoundationBlockProperties = build { this.noDrops = value }

    public fun lootFrom(block: Block?): FoundationBlockProperties = build { this.lootFrom = block }

    // Foundation properties
    /**
     * Sets the chance that fire will spread and consume this block. 300 being a 100% chance, 0, being a 0% chance.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood planks/slabs/etc. = 20
     * - wood logs = 5
     * - leaves, wool = 60
     * - grass, ferns, dead bush, flowers = 100
     *
     * This property only works with [IFoundationBlock] blocks.
     */
    public fun flammability(value: Int): FoundationBlockProperties = build { this.flammability = value }

    /**
     * Used when fire is updating on a neighbor block. The higher the number returned, the faster fire will spread
     * around this block.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood = 5
     * - leaves, wool, carpet = 30
     * - grass, ferns, dead bush, flowers = 60
     *
     * This property only works with [IFoundationBlock] blocks.
     */
    public fun fireSpreadSpeed(value: Int): FoundationBlockProperties = build { this.fireSpreadSpeed = value }

    /**
     * Sets the fire behavior for this block.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood planks/slabs/fences/etc. = 5, 20
     * - wood logs = 5, 5
     * - leaves, wool = 30, 60
     * - grass, ferns, dead bush, flowers = 60, 100
     *
     * This property only works with [IFoundationBlock] blocks.
     *
     * @param flammability the chance that fire will spread and consume this block. 300 being a 100% chance, 0,
     * being a 0% chance.
     *
     * @param fireSpreadSpeed used when fire is updating on a neighbor block. The higher the number returned, the
     * faster fire will spread around this block.
     */
    public fun fireInfo(flammability: Int, fireSpreadSpeed: Int): FoundationBlockProperties = build {
        this.flammability = flammability
        this.fireSpreadSpeed = fireSpreadSpeed
    }
    //endregion

    /**
     * Applies the given properties to this [FoundationBlockProperties] object.
     */
    public fun applyFrom(other: FoundationBlockProperties): FoundationBlockProperties = build {
        this.material = other.material ?: this.material
        this.mapColor = other.mapColor ?: this.mapColor
        this.blocksMovement = other.blocksMovement ?: this.blocksMovement
        this.soundType = other.soundType ?: this.soundType
        this.lightValue = other.lightValue ?: this.lightValue
        this.hardnessAndResistance = other.hardnessAndResistance ?: this.hardnessAndResistance
        this.ticksRandomly = other.ticksRandomly ?: this.ticksRandomly
        this.slipperiness = other.slipperiness ?: this.slipperiness
        this.speedFactor = other.speedFactor ?: this.speedFactor
        this.jumpFactor = other.jumpFactor ?: this.jumpFactor
        this.noDrops = other.noDrops ?: this.noDrops
        this.isSolid = other.isSolid ?: this.isSolid
        this.variableOpacity = other.variableOpacity ?: this.variableOpacity
        this.harvestLevel = other.harvestLevel ?: this.harvestLevel
        this.harvestTool = other.harvestTool ?: this.harvestTool
        this.lootFrom = other.lootFrom ?: this.lootFrom

        this.flammability = other.flammability ?: this.flammability
    }

    /**
     * Creates a vanilla [Block.Properties] object based on this one
     */
    public val vanillaProperties: Block.Properties
        get() {
            val properties = Block.Properties.create(material ?: GENERIC_MATERIAL, mapColor ?: MaterialColor.AIR)
            if (blocksMovement == false)
                properties.doesNotBlockMovement()
            soundType?.also { properties.sound(it) }
            lightValue?.also { properties.lightValue(it) }
            hardnessAndResistance?.also { properties.hardnessAndResistance(it.first, it.second) }
            if (ticksRandomly == true)
                properties.tickRandomly()
            slipperiness?.also { properties.slipperiness(it) }
            speedFactor?.also { properties.speedFactor(it) }
            jumpFactor?.also { properties.jumpFactor(it) }
            if (noDrops == true)
                properties.noDrops()
            lootFrom?.also { properties.lootFrom(it) }
            if (isSolid == false)
                properties.notSolid()
            if (variableOpacity == true)
                properties.variableOpacity()
            harvestLevel?.also { properties.harvestLevel(it) }
            harvestTool?.also { properties.harvestTool(it) }
            return properties
        }

    public fun getFlammabilityImpl(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return flammability ?: 0
    }

    public fun getFireSpreadSpeedImpl(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return fireSpreadSpeed ?: 0
    }

    private inline fun build(block: () -> Unit): FoundationBlockProperties {
        block()
        return this
    }

    companion object {
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