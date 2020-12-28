package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import net.minecraft.block.Block
import net.minecraft.block.PressurePlateBlock
import net.minecraft.block.RotatedPillarBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * A base class for Foundation pressure plates.
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 *
 * @param sensitivityFunction A function to determine when the pressure plate should be activated
 * @param resetTime The number of ticks the pressure plate takes to unpress. Defaults to 20
 * @param textureName The name of the block texture to use
 */
public open class BasePressurePlateBlock(
    override val properties: FoundationBlockProperties,
    private val sensitivityFunction: SensitivityFunction,
    private val resetTime: Int,
    private val textureName: String
) :
    PressurePlateBlock(Sensitivity.EVERYTHING, properties.vanillaProperties),
    IFoundationBlock {

    public constructor(
        properties: FoundationBlockProperties,
        sensitivityFunction: SensitivityFunction,
        textureName: String
    ): this(properties, sensitivityFunction, 20, textureName)

    override fun generateBlockState(gen: BlockStateProvider) {
        generatePressurePlateModel(gen, this, textureName)
    }

    override fun tickRate(worldIn: IWorldReader): Int {
        return resetTime
    }

    override fun computeRedstoneStrength(world: World, pos: BlockPos): Int {
        val entities = world.getEntitiesWithinAABBExcludingEntity(null as Entity?, PRESSURE_AABB.offset(pos))
        if(sensitivityFunction.respectsEntityDoesNotTriggerPressurePlate)
            entities.removeIf { it.doesEntityNotTriggerPressurePlate() }
        return if (sensitivityFunction.isActivated(world, pos, entities)) 15 else 0

    }

    public fun interface SensitivityFunction {
        /**
         * Whether this function respects [Entity.doesEntityNotTriggerPressurePlate]. Defaults to true.
         */
        public val respectsEntityDoesNotTriggerPressurePlate: Boolean
            get() = true

        /**
         * Compute whether the pressure plate should be activated based on the list of entities standing on it.
         */
        public fun isActivated(world: World, pos: BlockPos, entities: List<Entity>): Boolean

        public companion object {
            @JvmField
            public val EVERYTHING: SensitivityFunction = SensitivityFunction { _, _, entities ->
                entities.isNotEmpty()
            }

            @JvmField
            public val MOBS: SensitivityFunction = SensitivityFunction { _, _, entities ->
                entities.any { it is LivingEntity }
            }
        }
    }

    public companion object {
        @JvmStatic
        public fun generatePressurePlateModel(gen: BlockStateProvider, block: Block, textureName: String) {
            val unpressedModel = gen.models()
                .withExistingParent(block.registryName!!.path, loc("block/pressure_plate_up"))
                .texture("texture", gen.modLoc("block/$textureName"))
            val pressedModel = gen.models()
                .withExistingParent(block.registryName!!.path + "_down", loc("block/pressure_plate_down"))
                .texture("texture", gen.modLoc("block/$textureName"))

            gen.getVariantBuilder(block)
                .partialState().with(POWERED, false)
                .modelForState().modelFile(unpressedModel).addModel()
                .partialState().with(POWERED, true)
                .modelForState().modelFile(pressedModel).addModel()
        }
    }
}