package com.teamwizardry.librarianlib.facade.components.minecraft

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.component.GuiLayer
import com.teamwizardry.librarianlib.facade.component.GuiDrawContext
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.utilities.eventbus.Event
import net.minecraft.item.ItemStack

class ItemStackLayer(stack: ItemStack, x: Int, y: Int): GuiLayer(x, y, 16, 16) {
    constructor(stack: ItemStack): this(stack, 0, 0)
    constructor(x: Int, y: Int): this(ItemStack.EMPTY, x, y)
    constructor(): this(ItemStack.EMPTY, 0, 0)

    val stack_im: IMValue<ItemStack> = IMValue(stack)
    var stack: ItemStack by stack_im

    class QuantityTextEvent(var text: String?): Event()

    override fun draw(context: GuiDrawContext) {
        val stack = this.stack
        if (!stack.isEmpty) {
            val str: String? =
                if(stack.count == 1)
                    null
                else
                    BUS.fire(QuantityTextEvent(stack.count.toString())).text

            val itemRender = Client.minecraft.itemRenderer
            itemRender.zLevel = -130f

            val fr = (stack.item.getFontRenderer(stack) ?: Client.minecraft.fontRenderer)
            context.pushGlMatrix()
            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0)
            itemRender.renderItemOverlayIntoGUI(fr, stack, 0, 0, str)
            context.popGlMatrix()

            itemRender.zLevel = 0.0f
        }
    }
}