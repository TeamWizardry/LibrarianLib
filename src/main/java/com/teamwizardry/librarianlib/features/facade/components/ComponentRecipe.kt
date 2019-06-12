package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.provided.book.helper.TranslationHolder
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_SMOOTH
import java.awt.Color

class ComponentRecipe(posX: Int, posY: Int, width: Int, height: Int, mainColor: Color, keys: List<ResourceLocation>, arrow: Sprite, subtext: TranslationHolder?) : GuiComponent(posX, posY, width, height) {

    var time = 0

    val recipes = keys.mapNotNull { ForgeRegistries.RECIPES.getValue(it) }.filterNot { it.isDynamic }
    val grids = recipes.map { createRecipeGrid(it) }

    val recipe: IRecipe
        get() = recipes[MathHelper.floor(time / 90f) % recipes.size]
    val grid: Array<Ingredient>
        get() = grids[MathHelper.floor(time / 90f) % grids.size]

    @Hook
    fun tick(e: GuiComponentEvents.ComponentTickEvent) {
        if (!GuiScreen.isShiftKeyDown() && isVisible)
            time++
    }

    init {
        if (recipes.isNotEmpty()) {
            val output = ComponentItemStack(
                    (size.x / 2.0 - 8 + 40).toInt(), (size.y / 2.0 - 8).toInt() - 8)
            output.stack_im { recipe.recipeOutput }
            add(output)

            val x = (-8 + size.x / 2.0 - 24.0 - 16.toDouble()).toInt()
            val y = (-8 + size.y / 2.0 - 16.toDouble() - 8.0).toInt()

            for (row in 0 until 3) for (column in 0 until 3) {
                val stack = ComponentItemStack(x + row * 16, y + column * 16 + (column * 0.5).toInt())

                add(stack)

                stack.stack_im {
                    val stacks = grid[row * 3 + column].matchingStacks
                    if (stacks.isEmpty())
                        ItemStack.EMPTY
                    else
                        stacks[MathHelper.floor(time / 60.0f) % stacks.size]
                }
            }

            BUS.hook(GuiLayerEvents.PostDrawEvent::class.java) { event ->
                GlStateManager.pushMatrix()
                GlStateManager.enableBlend()
                GlStateManager.enableAlpha()
                GlStateManager.translate(
                        (size.x / 2.0 - arrow.width / 2.0 + 16.0).toInt().toFloat(), (size.y / 2.0 - arrow.height / 2.0 - 8 + 1).toInt().toFloat(), 0f)
                GlStateManager.color(0.25f, 0.25f, 0.25f, 1f)
                arrow.bind()
                arrow.draw(event.partialTicks.toInt(), 0f, 0f)
                GlStateManager.popMatrix()

                GlStateManager.pushMatrix()
                GlStateManager.enableBlend()
                GlStateManager.enableAlpha()
                GlStateManager.disableCull()
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.disableTexture2D()
                GlStateManager.shadeModel(GL_SMOOTH)

                val bandWidth = 1
                val excess = 6

                GlStateManager.translate(x - bandWidth / 2.0, y.toDouble(), 500.0)

                val color = mainColor.darker().darker()
                val fadeOff = Color(color.red, color.green, color.blue, 20)

                val tessellator = Tessellator.getInstance()
                val buffer = tessellator.buffer

                for (i in 1..2) {
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    buffer.pos((i * 16 + bandWidth).toDouble(), (0 - excess).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((i * 16).toDouble(), (0 - excess).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((i * 16).toDouble(), 24.0, 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    buffer.pos((i * 16 + bandWidth).toDouble(), 24.0, 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    tessellator.draw()

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    buffer.pos((i * 16 + bandWidth).toDouble(), (48 + excess).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((i * 16).toDouble(), (48 + excess).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((i * 16).toDouble(), 24.0, 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    buffer.pos((i * 16 + bandWidth).toDouble(), 24.0, 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    tessellator.draw()
                }

                for (i in 1..2) {
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    buffer.pos((0 - excess).toDouble(), (i * 16 + bandWidth).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((0 - excess).toDouble(), (i * 16).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos(24.0, (i * 16).toDouble(), 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    buffer.pos(24.0, (i * 16 + bandWidth).toDouble(), 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    tessellator.draw()

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    buffer.pos((48 + excess).toDouble(), (i * 16 + bandWidth).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos((48 + excess).toDouble(), (i * 16).toDouble(), 200.0).color(fadeOff.red, fadeOff.green, fadeOff.blue, fadeOff.alpha).endVertex()
                    buffer.pos(24.0, (i * 16).toDouble(), 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    buffer.pos(24.0, (i * 16 + bandWidth).toDouble(), 200.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
                    tessellator.draw()
                }

                GlStateManager.popMatrix()
                GlStateManager.enableTexture2D()
                RenderHelper.enableStandardItemLighting()
            }
        }

        if (subtext != null) {
            val text = ComponentText(size.xi / 2, size.yi * 3 / 4, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.TOP)
            text.text = subtext.toString()
            text.wrap = size.xi * 3 / 4
            add(text)
        }
    }

    private fun createRecipeGrid(recipe: IRecipe): Array<Ingredient> {
        val table = Array(9) { Ingredient.EMPTY }

        val actualWidth = if (recipe is IShapedRecipe) recipe.recipeWidth else 3
        val iterator = recipe.ingredients.iterator()

        for (column in 0 until 3) for (row in 0 until actualWidth) {
            if (!iterator.hasNext()) return table

            table[row * 3 + column + 3 - actualWidth] = iterator.next()
        }

        return table
    }
}
