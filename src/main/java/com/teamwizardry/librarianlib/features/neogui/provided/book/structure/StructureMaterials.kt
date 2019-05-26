package com.teamwizardry.librarianlib.features.neogui.provided.book.structure

import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil

/**
 * @author WireSegal
 * Created at 11:59 AM on 8/8/18.
 */
class StructureMaterials(packedBlockstateData: Map<List<IBlockState>, Int>) {

    constructor(structure: RenderableStructure?) : this(unspoolStructure(structure))
    constructor(structure: DynamicStructure?) : this(unspoolStructure(structure))

    companion object {
        fun unspoolStructure(structure: RenderableStructure?): Map<List<IBlockState>, Int> {
            val map = mutableMapOf<List<IBlockState>, Int>()
            if (structure != null) for (info in structure.blockInfos()) {
                if (info.blockState.block == Blocks.AIR) continue

                val list = mutableListOf(info.blockState)
                map[list] = map.getOrDefault(list, 0) + 1
            }

            return map
        }
        fun unspoolStructure(structure: DynamicStructure?): Map<List<IBlockState>, Int> {
            val map = mutableMapOf<List<IBlockState>, Int>()

            if (structure != null) for (info in structure.packed.values) {
                if (info.validStates.isEmpty() || info.validStates.any { it.block == Blocks.AIR }) continue

                val list = info.validStates
                map[list] = map.getOrDefault(list, 0) + 1
            }

            return map
        }


        fun silkDrop(state: IBlockState): ItemStack {
            return accessor.invoke(state.block, arrayOf(state)) as ItemStack
        }

        private val accessor = MethodHandleHelper.wrapperForMethod(Block::class.java, "getSilkTouchDrop", "func_180643_i", IBlockState::class.java)

    }

    val stacks: List<List<ItemStack>>

    init {
        val itemStacks = mutableListOf<List<ItemStack>>()
        for ((validStates, count) in packedBlockstateData) {

            val stateStacks = mutableListOf<ItemStack>()

            for (nextState in validStates) if (FluidRegistry.lookupFluidForBlock(nextState.block) != null) {
                val stack: ItemStack = FluidUtil.getFilledBucket(FluidStack(FluidRegistry.lookupFluidForBlock(nextState.block), 1))
                stateStacks.add(ItemStack(stack.item, count, stack.metadata, stack.tagCompound))
            } else
                stateStacks.add(silkDrop(nextState).apply { this.count = count })

            if (stateStacks.isNotEmpty())
                itemStacks.add(stateStacks.filter { it.isNotEmpty })
        }

        stacks = itemStacks.sortedByDescending { it.first().count }
    }
}
