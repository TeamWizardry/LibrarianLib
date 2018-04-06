package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import io.netty.util.collection.LongObjectHashMap
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import java.util.ArrayDeque

class ComponentMaterialList(book: IBookGui, structureRenderable: RenderableStructure?, structureDynamic: DynamicStructure?) : GuiComponent(6, 16, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32) {

    init {
        val betterIngredients = mutableMapOf<List<IBlockState>, Int>()

        val world: IBlockAccess? = structureRenderable?.blockAccess ?: structureDynamic?.blockAccess

        if (structureRenderable != null)
            for (info in structureRenderable.blockInfos()) {
                if (info.blockState.block === Blocks.AIR) continue

                val realState = info.blockState

                var exists = false
                for ((states, count) in betterIngredients) {
                    if (states[0].block !== realState.block) continue

                    val newStates = mutableListOf<IBlockState>()
                    newStates.addAll(states)
                    betterIngredients.remove(states)
                    betterIngredients.put(newStates, count + 1)
                    exists = true
                    break
                }
                if (!exists) {
                    betterIngredients.put(mutableListOf<IBlockState>(realState), 1)
                }
            }
        if (structureDynamic != null) {
            for ((pack, info) in structureDynamic.packed) {
                if (info.validStates[0].block == Blocks.AIR) continue

                var exists = false
                for ((states, count) in betterIngredients) {
                    if (states[0] !== info.validStates[0].block) continue

                    val newStates = mutableListOf<IBlockState>()
                    newStates.addAll(states)
                    betterIngredients.remove(states)
                    betterIngredients.put(newStates, count + 1)
                    exists = true
                    break
                }
                if (!exists) {
                    val newStates = mutableListOf<IBlockState>()
                    newStates.addAll(info.validStates)
                    betterIngredients.put(newStates, 1)
                }
            }
        }

        val text = ComponentText(0, 3, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP)
        text.size = Vec2d(200.0, 16.0)
        text.text.setValue(" " + LibrarianLib.PROXY.translate("liblib.misc.structure_materials"))
        add(text)

        val lineBreak = ComponentSprite(book.lineBreak, (size.x / 2.0 - 177.0 / 2.0).toInt(), 30, 177, 2)
        add(lineBreak)

        if (world != null) {
            var i = 0
            var row = 0
            for ((validStates, count) in betterIngredients) {

                val nextState = validStates[0]
                val itemStacks = NonNullList.create<ItemStack>()

                if (FluidRegistry.lookupFluidForBlock(nextState.block) != null) {
                    var stack: ItemStack = FluidUtil.getFilledBucket(FluidStack(FluidRegistry.lookupFluidForBlock(nextState.block), 1))

                    stack = ItemStack(stack.item, count, stack.metadata, stack.tagCompound)

                    itemStacks.add(stack)
                } else {
                    nextState.block.getDrops(itemStacks, world, BlockPos.ORIGIN, nextState, 0)

                    if (itemStacks.isEmpty()) itemStacks.add(ItemStack(nextState.block, count))
                }

                for (stack in itemStacks) {
                    val componentStack = ComponentStack(i * 16, 20 + row * 16)
                    componentStack.stack.setValue(stack)
                    add(componentStack)

                    if (i++ > 4) {
                        i = 0
                        row++
                    }
                }
            }
        }
    }
}