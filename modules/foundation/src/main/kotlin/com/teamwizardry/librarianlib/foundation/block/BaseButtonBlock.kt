package com.teamwizardry.librarianlib.foundation.block

import com.teamwizardry.librarianlib.core.util.loc
import net.minecraft.block.AbstractButtonBlock
import net.minecraft.block.BlockState
import net.minecraft.block.StairsBlock
import net.minecraft.state.properties.Half
import net.minecraft.state.properties.StairsShape
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import java.util.*

public abstract class BaseButtonBlock(
    override val properties: FoundationBlockProperties,
    protected val textureName: String
) : AbstractButtonBlock(false, properties.vanillaProperties), IFoundationBlock {
    public abstract val pressDuration: Int

    override fun generateBlockState(gen: BlockStateProvider) {
        val texture = gen.modLoc("block/$textureName")

        val unpressedModel = gen.models()
            .withExistingParent(registryName!!.path, loc("block/button"))
            .texture("texture", texture)
        val pressedModel = gen.models()
            .withExistingParent(registryName!!.path + "_down", loc("block/button_pressed"))
            .texture("texture", texture)

        gen.horizontalFaceBlock(this, { state -> if(state.get(POWERED)) pressedModel else unpressedModel }, 180)
        gen.itemModels().singleTexture("block/${registryName!!.path}_inventory", loc("block/button_inventory"), texture)
    }

    override fun inventoryModelName(): String {
        return "block/${registryName!!.path}_inventory"
    }

    override fun powerBlock(state: BlockState, world: World, pos: BlockPos) {
        setPressed(state, world, pos, true, playSound = false)
    }

    override fun tick(state: BlockState, world: ServerWorld, pos: BlockPos, rand: Random?) {
        setPressed(state, world, pos, false, playSound = true)
    }

    protected fun setPressed(state: BlockState, world: World, pos: BlockPos, pressed: Boolean, playSound: Boolean) {
        if (pressed != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, pressed), 3)
            this.updateNeighbors(state, world, pos)
            if (pressed) {
                world.pendingBlockTicks.scheduleTick(BlockPos(pos), this, pressDuration)
            }
            if(playSound) {
                playSound(null, world, pos, pressed)
            }
        }
    }

    private fun updateNeighbors(state: BlockState, world: World, pos: BlockPos) {
        world.notifyNeighborsOfStateChange(pos, this)
        world.notifyNeighborsOfStateChange(pos.offset(getFacing(state).opposite), this)
    }
}