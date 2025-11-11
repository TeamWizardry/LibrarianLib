package com.teamwizardry.librarianlib.facade.layers.minecraft

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.clamp
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import java.awt.Color
import kotlin.math.roundToInt

public class ItemStackLayer(stack: ItemStack, x: Int, y: Int): GuiLayer(x, y, 16, 16) {
    public constructor(stack: ItemStack): this(stack, 0, 0)
    public constructor(x: Int, y: Int): this(ItemStack.EMPTY, x, y)
    public constructor(): this(ItemStack.EMPTY, 0, 0)

    public val stack_im: IMValue<ItemStack> = imValue(stack)
    public var stack: ItemStack by stack_im

    public class QuantityTextEvent(
        /**
         * The text to display for the item quantity. Set to null to hide, or a custom arbitrary string.
         */
        public var text: String?,
    ): Event()
    public class ItemBarValueEvent(
        /**
         * Whether to show the item bar
         */
        public var show: Boolean,
        /**
         * The fill level in the range 0-1
         */
        public var fillLevel: Double,
        /**
         * The fill color
         */
        public var fillColor: Color,
    ): Event() {
        public fun hide() {
            show = false
        }

        public fun set(fillLevel: Double, fillColor: Color) {
            this.show = true
            this.fillLevel = fillLevel.clamp(0.0, 1.0)
            this.fillColor = fillColor
        }

        /**
         * Sets the fill level using the default vanilla color gradient
         */
        public fun set(fillLevel: Double) {
            set(fillLevel, defaultFillColorGradient(fillLevel))
        }

        public companion object {
            public fun defaultFillColorGradient(fillLevel: Double): Color {
                val clampedLevel = fillLevel.clamp(0.0, 1.0)
                // Source: Item.getItemBarColor()
                return Color(MathHelper.hsvToRgb(clampedLevel.toFloat() / 3.0f, 1.0f, 1.0f))
            }
        }
    }

    override fun draw(context: GuiDrawContext) {
        val stack = this.stack
        if (!stack.isEmpty) {
            val countString = BUS.fire(QuantityTextEvent(stack.count.takeIf { it != 1 }?.toString())).text
            val itemBarEvent = BUS.fire(ItemBarValueEvent(
                stack.isItemBarVisible,
                stack.itemBarStep / 13.0,
                Color(stack.itemBarColor)
            ))

            val itemRender = Client.minecraft.itemRenderer

            context.pushVanillaMatrix()
            context.vanillaContext.matrices.translate(0f, 0f, -125f) // +150 in item render code
            context.vanillaContext.drawItem(stack, 0, 0)

            if (countString != null) {
                // Source: DrawContext.drawItemInSlot
                context.pushVanillaMatrix()
                context.vanillaContext.matrices.translate(0f, 0f, 50f)
                context.vanillaContext.drawText(
                    Client.textRenderer,
                    countString,
                    19 - 2 - Client.textRenderer.getWidth(countString),
                    6 + 3,
                    0xFFFFFF,
                    true
                )
            }

            if (itemBarEvent.show) {
                val barWidth = 13
                val fillPixels = itemBarEvent.fillLevel.clamp(0.0, 1.0).roundToInt()
                val fillColor = itemBarEvent.fillColor.rgb or 0xFF000000.toInt()

                // Source: DrawContext.drawItemInSlot
                context.pushVanillaMatrix()
                context.vanillaContext.matrices.translate(2f, 13f, 50f)
                context.vanillaContext.fill(0, 0, barWidth, 2, 0xFF000000.toInt())
                context.vanillaContext.fill(0, 0, fillPixels, 1, fillColor)
            }

            context.popVanillaMatrix()
        }
    }
}