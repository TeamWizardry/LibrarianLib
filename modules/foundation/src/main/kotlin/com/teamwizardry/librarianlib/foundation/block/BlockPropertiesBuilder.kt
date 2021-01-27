package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.entity.EntityType
import net.minecraftforge.common.ToolType
import java.util.function.Function
import java.util.function.ToIntFunction
import kotlin.math.max

public interface BlockPropertiesBuilder<T : BlockPropertiesBuilder<T>> {
    public val blockProperties: FoundationBlockProperties

    public fun material(value: Material?): T = build {
        blockProperties.material = value
        if (blockProperties.mapColor == null && value != null)
            this.mapColor { value.color }
    }

    public fun mapColor(value: Function<BlockState, MaterialColor>?): T = build { blockProperties.mapColor = value }
    public fun mapColor(value: MaterialColor?): T =
        build { blockProperties.mapColor = value?.let { Function { value } } }

    public fun blocksMovement(value: Boolean): T = build {
        blockProperties.blocksMovement = value
        blockProperties.isSolid = (blockProperties.isSolid ?: true) && value
    }
    public fun doesNotBlockMovement(): T = build { this.blocksMovement(false) }

    public fun isSolid(value: Boolean): T = build { blockProperties.isSolid = value }
    public fun notSolid(): T = build { this.isSolid(false) }

    public fun isAir(value: Boolean): T = build { blockProperties.isAir = value }
    public fun setAir(): T = build { this.isAir(true) }

    public fun slipperiness(slipperiness: Float): T = build { blockProperties.slipperiness = slipperiness }

    public fun speedFactor(factor: Float): T = build { blockProperties.speedFactor = factor }

    public fun jumpFactor(factor: Float): T = build { blockProperties.jumpFactor = factor }

    public fun sound(soundType: SoundType?): T = build { blockProperties.soundType = soundType }

    public fun lightLevel(value: ToIntFunction<BlockState>): T = build { blockProperties.lightLevel = value }
    public fun lightLevel(value: Int): T = build { this.lightLevel { value } }

    public fun hardnessAndResistance(hardness: Float, resistance: Float): T =
        build { blockProperties.hardnessAndResistance = hardness to max(0.0f, resistance) }
    public fun hardnessAndResistance(hardnessAndResistance: Float): T =
        build { this.hardnessAndResistance(hardnessAndResistance, hardnessAndResistance) }

    public fun ticksRandomly(value: Boolean): T = build { blockProperties.ticksRandomly = value }
    public fun tickRandomly(): T = build { this.ticksRandomly(true) }

    public fun variableOpacity(value: Boolean): T = build { blockProperties.variableOpacity = value }
    public fun variableOpacity(): T = build { this.variableOpacity(true) }

    public fun harvestLevel(harvestLevel: Int): T = build { blockProperties.harvestLevel = harvestLevel }

    public fun harvestTool(harvestTool: ToolType?): T = build { blockProperties.harvestTool = harvestTool }

    public fun noDrops(value: Boolean): T = build { blockProperties.noDrops = value }
    public fun noDrops(): T = build { this.noDrops(true) }

    public fun lootFrom(block: Block?): T = build { blockProperties.lootFrom = block }

    public fun allowsSpawn(predicate: AbstractBlock.IExtendedPositionPredicate<EntityType<*>>): T =
        build { blockProperties.allowsSpawn = predicate }

    public fun isOpaque(predicate: AbstractBlock.IPositionPredicate): T = build { blockProperties.isOpaque = predicate }
    public fun suffocates(predicate: AbstractBlock.IPositionPredicate): T =
        build { blockProperties.suffocates = predicate }

    public fun blocksVision(predicate: AbstractBlock.IPositionPredicate): T =
        build { blockProperties.blocksVision = predicate }

    public fun needsPostProcessing(predicate: AbstractBlock.IPositionPredicate): T =
        build { blockProperties.needsPostProcessing = predicate }

    public fun emissiveRendering(predicate: AbstractBlock.IPositionPredicate): T =
        build { blockProperties.emissiveRendering = predicate }

    /**
     * Sets the chance that fire will spread and consume blockProperties block. 300 being a 100% chance, 0, being a 0% chance.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood planks/slabs/etc. = 20
     * - wood logs = 5
     * - leaves, wool = 60
     * - grass, ferns, dead bush, flowers = 100
     *
     * This property only works with [IFoundationBlock] blocks.
     */
    public fun flammability(value: Int): T = build { blockProperties.flammability = value }

    /**
     * Used when fire is updating on a neighbor block. The higher the number returned, the faster fire will spread
     * around blockProperties block.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood = 5
     * - leaves, wool, carpet = 30
     * - grass, ferns, dead bush, flowers = 60
     *
     * This property only works with [IFoundationBlock] blocks.
     */
    public fun fireSpreadSpeed(value: Int): T = build { blockProperties.fireSpreadSpeed = value }

    /**
     * Sets the fire behavior for blockProperties block.
     *
     * Some example values (taken from [FireBlock.init]):
     * - wood planks/slabs/fences/etc. = 5, 20
     * - wood logs = 5, 5
     * - leaves, wool = 30, 60
     * - grass, ferns, dead bush, flowers = 60, 100
     *
     * This property only works with [IFoundationBlock] blocks.
     *
     * @param flammability the chance that fire will spread and consume blockProperties block. 300 being a 100% chance, 0,
     * being a 0% chance.
     *
     * @param fireSpreadSpeed used when fire is updating on a neighbor block. The higher the number returned, the
     * faster fire will spread around blockProperties block.
     */
    public fun fireInfo(flammability: Int, fireSpreadSpeed: Int): T = build {
        blockProperties.flammability = flammability
        blockProperties.fireSpreadSpeed = fireSpreadSpeed
    }

    /**
     * Applies the given properties to blockProperties [FoundationBlockProperties] object.
     */
    public fun applyFrom(other: FoundationBlockProperties): T = build {
        blockProperties.material = other.material ?: blockProperties.material
        blockProperties.mapColor = other.mapColor ?: blockProperties.mapColor
        blockProperties.blocksMovement = other.blocksMovement ?: blockProperties.blocksMovement
        blockProperties.soundType = other.soundType ?: blockProperties.soundType
        blockProperties.lightLevel = other.lightLevel ?: blockProperties.lightLevel
        blockProperties.hardnessAndResistance = other.hardnessAndResistance ?: blockProperties.hardnessAndResistance
        blockProperties.requiresTool = other.requiresTool ?: blockProperties.requiresTool
        blockProperties.ticksRandomly = other.ticksRandomly ?: blockProperties.ticksRandomly
        blockProperties.slipperiness = other.slipperiness ?: blockProperties.slipperiness
        blockProperties.speedFactor = other.speedFactor ?: blockProperties.speedFactor
        blockProperties.jumpFactor = other.jumpFactor ?: blockProperties.jumpFactor
        blockProperties.noDrops = other.noDrops ?: blockProperties.noDrops
        blockProperties.isSolid = other.isSolid ?: blockProperties.isSolid
        blockProperties.variableOpacity = other.variableOpacity ?: blockProperties.variableOpacity
        blockProperties.harvestLevel = other.harvestLevel ?: blockProperties.harvestLevel
        blockProperties.harvestTool = other.harvestTool ?: blockProperties.harvestTool
        blockProperties.lootFrom = other.lootFrom ?: blockProperties.lootFrom
        blockProperties.allowsSpawn = other.allowsSpawn ?: blockProperties.allowsSpawn
        blockProperties.isOpaque = other.isOpaque ?: blockProperties.isOpaque
        blockProperties.suffocates = other.suffocates ?: blockProperties.suffocates
        blockProperties.blocksVision = other.blocksVision ?: blockProperties.blocksVision
        blockProperties.needsPostProcessing = other.needsPostProcessing ?: blockProperties.needsPostProcessing
        blockProperties.emissiveRendering = other.emissiveRendering ?: blockProperties.emissiveRendering

        blockProperties.flammability = other.flammability ?: blockProperties.flammability
        blockProperties.fireSpreadSpeed = other.fireSpreadSpeed ?: blockProperties.fireSpreadSpeed
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun build(block: () -> Unit): T {
        block()
        return this as T
    }
}