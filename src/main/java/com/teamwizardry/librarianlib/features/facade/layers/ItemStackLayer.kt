package com.teamwizardry.librarianlib.features.facade.layers

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.facade.HandlerList
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.kotlin.plus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting

class ItemStackLayer(posX: Int, posY: Int) : FixedSizeLayer(posX, posY, 16, 16) {

    val stack_im: IMValue<ItemStack> = IMValue(ItemStack.EMPTY)
    var stack: ItemStack by stack_im

    class QuantityTextEvent(var text: String?): Event()

    override fun draw(partialTicks: Float) {
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableRescaleNormal()
        GlStateManager.pushMatrix()

        val stack = this.stack
        if (stack.isNotEmpty) {
            val str: String? =
                if(stack.count == 1)
                    null
                else
                    BUS.fire(QuantityTextEvent(stack.count.toString())).text

            val itemRender = Minecraft.getMinecraft().renderItem
            itemRender.zLevel = -130f

            GlStateManager.scale(size.xf / 16, size.yf / 16, 1f)

            val fr = (stack.item.getFontRenderer(stack) ?: Minecraft.getMinecraft().fontRenderer)
            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0)
            itemRender.renderItemOverlayIntoGUI(fr, stack, 0, 0, str)

            itemRender.zLevel = 0.0f
        }

        GlStateManager.popMatrix()
        GlStateManager.disableRescaleNormal()
        RenderHelper.disableStandardItemLighting()
    }
}
