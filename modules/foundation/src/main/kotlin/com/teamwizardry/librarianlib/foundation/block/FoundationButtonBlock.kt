package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.util.*

/**
 * A configurable implementation of [BaseButtonBlock]
 *
 * Required textures:
 * - `<modid>:block/<textureName>.png`
 *
 * @param canBePressedByArrows Whether hitting the button with an arrow will press it
 * @param pressDuration The number of ticks the button takes to un-press
 * @param pressSoundEvent The sound event to play when pressing the button
 * @param unpressSoundEvent The sound event to play when the button un-presses
 * @param textureName The name of the block texture to use
 */
public class FoundationButtonBlock(
    properties: FoundationBlockProperties,
    private val canBePressedByArrows: Boolean,
    public override val pressDuration: Int,
    private val pressSoundEvent: SoundEvent,
    private val unpressSoundEvent: SoundEvent,
    textureName: String
) : BaseButtonBlock(properties, textureName) {

    override fun getSoundEvent(isOn: Boolean): SoundEvent {
        return if(isOn) pressSoundEvent else unpressSoundEvent
    }

    override fun tick(state: BlockState, world: ServerWorld, pos: BlockPos, rand: Random?) {
        if (state.get(POWERED)) {
            if (canBePressedByArrows) {
                setPressed(state, world, pos, isPressedByArrow(state, world, pos), playSound = true)
            } else {
                setPressed(state, world, pos, false, playSound = true)
            }
        }
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entityIn: Entity) {
        if (!world.isRemote && canBePressedByArrows && !state.get(POWERED)) {
            setPressed(state, world, pos, isPressedByArrow(state, world, pos), playSound = true)
        }
    }

    private fun isPressedByArrow(state: BlockState, world: World, pos: BlockPos): Boolean {
        return world.getEntitiesWithinAABB(
            AbstractArrowEntity::class.java, state.getShape(world, pos).boundingBox.offset(pos)
        ).isNotEmpty()
    }

}