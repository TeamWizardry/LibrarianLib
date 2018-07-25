package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil

class ComponentMaterialList(book: IBookGui, structureRenderable: RenderableStructure?, structureDynamic: DynamicStructure?) : GuiComponent(16, 16, 16, 16) {

    companion object {
        fun silkDrop(state: IBlockState): ItemStack {
            return accessor.invoke(state.block, arrayOf(state)) as ItemStack
        }

        private val accessor = MethodHandleHelper.wrapperForMethod(Block::class.java, "getSilkTouchDrop", "func_180643_i", IBlockState::class.java)
    }

    var ticks = 0

    init {
        val betterIngredients = mutableMapOf<List<IBlockState>, Int>()

        if (structureRenderable != null)
            for (info in structureRenderable.blockInfos()) {
                if (info.blockState.block == Blocks.AIR) continue

                val list = mutableListOf(info.blockState)
                betterIngredients[list] = betterIngredients.getOrDefault(list, 0) + 1
            }
        if (structureDynamic != null) {
            for ((_, info) in structureDynamic.packed) {
                if (info.validStates[0].block == Blocks.AIR) continue

                val list = info.validStates
                betterIngredients[list] = betterIngredients.getOrDefault(list, 0) + 1
            }
        }

        val text = ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        text.size = Vec2d(200.0, 16.0)
        text.text.setValue(" " + LibrarianLib.PROXY.translate("liblib.misc.structure_materials"))
        add(text)

        val lineBreak = ComponentSprite(book.lineBreak, (size.x / 2.0 - 177.0 / 2.0).toInt(), 30, 177, 2)
        add(lineBreak)

        var i = 0
        var row = 0

        val itemStacks = mutableListOf<List<ItemStack>>()
        for ((validStates, count) in betterIngredients) {

            val stacks = mutableListOf<ItemStack>()

            for (nextState in validStates) {
                if (FluidRegistry.lookupFluidForBlock(nextState.block) != null) {
                    var stack: ItemStack = FluidUtil.getFilledBucket(FluidStack(FluidRegistry.lookupFluidForBlock(nextState.block), 1))

                    stack = ItemStack(stack.item, count, stack.metadata, stack.tagCompound)

                    stacks.add(stack)
                } else
                    stacks.add(silkDrop(nextState).apply { this.count = count })
            }

            if (stacks.isNotEmpty())
                itemStacks.add(stacks.filter { it.isNotEmpty })
        }

        for (stack in itemStacks) {
            val componentStack = ComponentStack(i * 16, 20 + row * 16)
            componentStack.stack.func {
                stack[(ticks / 20) % stack.size]
            }
            add(componentStack)

            if (i++ > 4) {
                i = 0
                row++
            }
        }
    }
}
