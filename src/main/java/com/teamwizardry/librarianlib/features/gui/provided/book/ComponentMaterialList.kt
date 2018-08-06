package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
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

class ComponentMaterialList(book: IBookGui, private val structureRenderable: RenderableStructure?, private val structureDynamic: DynamicStructure?) : NavBarHolder(16, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32, book), IBookElement {

    override val bookParent: Book
        get() = book.bookParent

    override fun createComponent(book: IBookGui): GuiComponent {
        return ComponentMaterialList(book, structureRenderable, structureDynamic)
    }

    companion object {
        fun silkDrop(state: IBlockState): ItemStack {
            return accessor.invoke(state.block, arrayOf(state)) as ItemStack
        }

        private val accessor = MethodHandleHelper.wrapperForMethod(Block::class.java, "getSilkTouchDrop", "func_180643_i", IBlockState::class.java)
    }

    var ticks = 0

    init {
        val finalIngredients = mutableMapOf<List<IBlockState>, Int>()

        if (structureRenderable != null) for (info in structureRenderable.blockInfos()) {
            if (info.blockState.block == Blocks.AIR) continue

            val list = mutableListOf(info.blockState)
            finalIngredients[list] = finalIngredients.getOrDefault(list, 0) + 1
        }

        if (structureDynamic != null) for (info in structureDynamic.packed.values) {
            if (info.validStates[0].block == Blocks.AIR) continue

            val list = info.validStates
            finalIngredients[list] = finalIngredients.getOrDefault(list, 0) + 1
        }

        val itemStacks = mutableListOf<List<ItemStack>>()
        for ((validStates, count) in finalIngredients) {

            val stacks = mutableListOf<ItemStack>()

            for (nextState in validStates) if (FluidRegistry.lookupFluidForBlock(nextState.block) != null) {
                val stack: ItemStack = FluidUtil.getFilledBucket(FluidStack(FluidRegistry.lookupFluidForBlock(nextState.block), 1))
                stacks.add(ItemStack(stack.item, count, stack.metadata, stack.tagCompound))
            } else
                stacks.add(silkDrop(nextState).apply { this.count = count })

            if (stacks.isNotEmpty())
                itemStacks.add(stacks.filter { it.isNotEmpty })
        }

        itemStacks.mapIndexed { index, stack -> index to stack }.sortedByDescending { it.second.size }.forEach { (index, stack) ->
            val row = index % 10
            val column = index

            val stackIcon = ComponentStack(index / 10 * 16, index % 10 * 16)
            stackIcon.stack.func { stack[(ticks / 20) % stack.size] }
            stackIcon.transform.translateZ += 500
            add(stackIcon)
        }

    }
}
