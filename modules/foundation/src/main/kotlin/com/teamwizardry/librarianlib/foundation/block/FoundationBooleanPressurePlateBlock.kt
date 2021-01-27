package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * A configurable implementation of [BaseBooleanPressurePlateBlock]
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 *
 * @param entityPredicate A function to determine what entities to test for
 * @param resetTime The number of ticks the pressure plate takes to un-press
 * @param textureName The name of the block texture to use
 */
public class FoundationBooleanPressurePlateBlock(
    override val properties: FoundationBlockProperties,
    private val entityPredicate: Predicate<Entity>,
    private val resetTime: Int,
    textureName: String
) : BaseBooleanPressurePlateBlock(properties, textureName),
    IFoundationBlock {

    public constructor(
        properties: FoundationBlockProperties,
        resetTime: Int,
        textureName: String
    ): this(properties, { true }, resetTime, textureName)

    override fun getPoweredDuration(): Int {
        return resetTime
    }

    override fun computeRedstoneStrength(world: World, pos: BlockPos): Int {
        val entities = getEntitiesOnPressurePlate(
            world, pos,
            true
        )
        return if (entities.any { entityPredicate.test(it) }) 15 else 0
    }
}