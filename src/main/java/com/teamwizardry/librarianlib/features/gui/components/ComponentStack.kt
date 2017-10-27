package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.HandlerList
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting

open class ComponentStack(posX: Int, posY: Int) : GuiComponent(posX, posY, 16, 16) {

    val stack = Option<ComponentStack, ItemStack>(ItemStack.EMPTY)
    val enableTooltip = Option<ComponentStack, Boolean>(true)
    val quantityText = HandlerList<(ComponentStack, String?) -> String?>()
    val itemInfo = HandlerList<(ComponentStack, MutableList<String>) -> Unit>()

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableRescaleNormal()

        val stack = this.stack.getValue(this)
        if (stack.isNotEmpty) {
            var str = "" + stack.count
            str = quantityText.fireModifier(str, { h, v -> h(this, v) }) ?: ""

            val itemRender = Minecraft.getMinecraft().renderItem
            itemRender.zLevel = 200.0f

            itemRender.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi)
            itemRender.renderItemOverlayIntoGUI(stack.item.getFontRenderer(stack) ?: Minecraft.getMinecraft().fontRenderer, stack, 0, 0, str)

            itemRender.zLevel = 0.0f

            if (mouseOver && enableTooltip.getValue(this))
                drawTooltip(stack)
        }

        GlStateManager.disableRescaleNormal()
        RenderHelper.disableStandardItemLighting()
    }

    fun drawTooltip(stack: ItemStack) {
        val list = stack.getTooltip(Minecraft.getMinecraft().player,
                if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL)

        for (i in list.indices) {
            if (i == 0) {
                list[i] = stack.rarity.rarityColor + list[i]
            } else {
                list[i] = TextFormatting.GRAY + list[i]
            }
        }

        itemInfo.fireAll { h -> h(this, list) }

        val font = stack.item.getFontRenderer(stack)
        render.setTooltip(list, font ?: Minecraft.getMinecraft().fontRenderer)
    }

}
