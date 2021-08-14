package com.teamwizardry.librarianlib.facade.layers.minecraft

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import net.minecraft.item.ItemStack

public class ItemStackLayer(stack: ItemStack, x: Int, y: Int): GuiLayer(x, y, 16, 16) {
    public constructor(stack: ItemStack): this(stack, 0, 0)
    public constructor(x: Int, y: Int): this(ItemStack.EMPTY, x, y)
    public constructor(): this(ItemStack.EMPTY, 0, 0)

    public val stack_im: IMValue<ItemStack> = imValue(stack)
    public var stack: ItemStack by stack_im

    public class QuantityTextEvent(public var text: String?): Event()

    override fun draw(context: GuiDrawContext) {
        val stack = this.stack
        if (!stack.isEmpty) {
            val str: String? =
                if (stack.count == 1)
                    null
                else
                    BUS.fire(QuantityTextEvent(stack.count.toString())).text

            val itemRender = Client.minecraft.itemRenderer
            itemRender.zOffset = -130f

            context.pushModelViewMatrix()
            itemRender.renderInGuiWithOverrides(stack, 0, 0)
            itemRender.renderGuiItemOverlay(Client.minecraft.textRenderer, stack, 0, 0, str)
            context.popModelViewMatrix()

            itemRender.zOffset = 0.0f
        }
    }
}