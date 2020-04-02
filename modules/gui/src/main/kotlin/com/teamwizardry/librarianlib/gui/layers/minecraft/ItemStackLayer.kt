package com.teamwizardry.librarianlib.gui.layers.minecraft

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.layers.FixedSizeLayer
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.utilities.eventbus.Event
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
        if (!stack.isEmpty) {
            val str: String? =
                if(stack.count == 1)
                    null
                else
                    BUS.fire(QuantityTextEvent(stack.count.toString())).text

            val itemRender = Client.minecraft.itemRenderer
            itemRender.zLevel = -130f

            GlStateManager.scalef(size.xf / 16, size.yf / 16, 1f)

            val fr = (stack.item.getFontRenderer(stack) ?: Client.minecraft.fontRenderer)
            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0)
            itemRender.renderItemOverlayIntoGUI(fr, stack, 0, 0, str)

            itemRender.zLevel = 0.0f
        }

        GlStateManager.popMatrix()
        GlStateManager.disableRescaleNormal()
        RenderHelper.disableStandardItemLighting()
    }
}