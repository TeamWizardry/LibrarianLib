package com.teamwizardry.librarianlib.gui.layers.minecraft

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.gui.layers.FixedSizeLayer
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack

class ItemStackLayer(stack: ItemStack, x: Int, y: Int) : FixedSizeLayer(x, y, 16, 16) {
    constructor(stack: ItemStack): this(stack, 0, 0)
    constructor(x: Int, y: Int): this(ItemStack.EMPTY, x, y)
    constructor(): this(ItemStack.EMPTY, 0, 0)

    val stack_im: IMValue<ItemStack> = IMValue(stack)
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