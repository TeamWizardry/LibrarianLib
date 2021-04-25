package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.math.clamp
import net.minecraft.block.Block
import net.minecraft.block.PressurePlateBlock
import net.minecraft.block.RotatedPillarBlock
import net.minecraft.block.WeightedPressurePlateBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.Box
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation boolean pressure plates.
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 *
 * @param textureName The name of the block texture to use
 */
public abstract class BaseBooleanPressurePlateBlock(
    override val properties: FoundationBlockProperties,
    protected val textureName: String
) : PressurePlateBlock(Sensitivity.EVERYTHING, properties.vanillaProperties),
    IFoundationBlock {

    override fun generateBlockState(gen: BlockStateProvider) {
        val unpressedModel = gen.models()
            .withExistingParent(registryName!!.path, loc("block/pressure_plate_up"))
            .texture("texture", gen.modLoc("block/$textureName"))
        val pressedModel = gen.models()
            .withExistingParent(registryName!!.path + "_down", loc("block/pressure_plate_down"))
            .texture("texture", gen.modLoc("block/$textureName"))

        gen.getVariantBuilder(this)
            .partialState().with(POWERED, false)
            .modelForState().modelFile(unpressedModel).addModel()
            .partialState().with(POWERED, true)
            .modelForState().modelFile(pressedModel).addModel()
    }

    protected fun getEntitiesOnPressurePlate(
        world: World,
        pos: BlockPos,
        respectEntityDoesNotTriggerPressurePlate: Boolean
    ): List<Entity> {
        val entities = world.getEntitiesWithinAABBExcludingEntity(null as Entity?, PRESSURE_AABB.offset(pos))
        if (respectEntityDoesNotTriggerPressurePlate)
            entities.removeIf { it.doesEntityNotTriggerPressurePlate() }
        return entities
    }
}