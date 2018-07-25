package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil

class ComponentMaterialList(book: IBookGui, structureRenderable: RenderableStructure?, structureDynamic: DynamicStructure?) : GuiComponent(1, 1, 16, 16) {

    companion object {
        fun silkDrop(state: IBlockState): ItemStack {
            return accessor.invoke(state.block, arrayOf(state)) as ItemStack
        }

        private val accessor = MethodHandleHelper.wrapperForMethod(Block::class.java, "getSilkTouchDrop", "func_180643_i", IBlockState::class.java)
    }

    var ticks = 0

    init {
        val betterIngredients = mutableMapOf<List<IBlockState>, Int>()

        if (structureRenderable != null) for (info in structureRenderable.blockInfos()) {
            if (info.blockState.block == Blocks.AIR) continue

            val list = mutableListOf(info.blockState)
            betterIngredients[list] = betterIngredients.getOrDefault(list, 0) + 1
        }

        if (structureDynamic != null) for (info in structureDynamic.packed.values) {
            if (info.validStates[0].block == Blocks.AIR) continue

            val list = info.validStates
            betterIngredients[list] = betterIngredients.getOrDefault(list, 0) + 1
        }

        val icon = ComponentSprite(book.materialIcon, 0, 0)
        icon.BUS.hook(GuiComponentEvents.PostDrawEvent::class.java) {
            if (mouseOver) render.setTooltip(listOf(I18n.format("${LibrarianLib.MODID}.false_item.materials")))
        }

        icon.transform.translateZ += 1000
        add(icon)

        val itemStacks = mutableListOf<List<ItemStack>>()
        for ((validStates, count) in betterIngredients) {

            val stacks = mutableListOf<ItemStack>()

            for (nextState in validStates) if (FluidRegistry.lookupFluidForBlock(nextState.block) != null) {
                val stack: ItemStack = FluidUtil.getFilledBucket(FluidStack(FluidRegistry.lookupFluidForBlock(nextState.block), 1))
                stacks.add(ItemStack(stack.item, count, stack.metadata, stack.tagCompound))
            } else
                stacks.add(silkDrop(nextState).apply { this.count = count })

            if (stacks.isNotEmpty())
                itemStacks.add(stacks.filter { it.isNotEmpty })
        }

        for ((row, stack) in itemStacks.sortedByDescending { it.size }.withIndex()) {
            val stackIcon = ComponentStack(0, 20 + row * 16)
            stackIcon.stack.func { stack[(ticks / 20) % stack.size] }
            stackIcon.transform.translateZ += 500
            add(stackIcon)
        }
    }
}
