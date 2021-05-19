package com.teamwizardry.librarianlib.facade.provided

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Language

/**
 * Public access to the protected tooltip rendering methods in [Screen]
 */
public object VanillaTooltipRenderer {
    @JvmStatic
    public fun renderTooltip(matrixStack: MatrixStack, text: String, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(matrixStack, LiteralText(text), mouseX, mouseY)
    }

    @JvmStatic
    public fun renderTooltip(
        matrixStack: MatrixStack,
        text: List<String>,
        mouseX: Int,
        mouseY: Int
    ) {
        TooltipProvider.renderOrderedTooltip(
            matrixStack,
            Language.getInstance().reorder(text.map { StringVisitable.plain(it) }),
            mouseX,
            mouseY
        )
    }

    @JvmStatic
    public fun renderTooltip(matrixStack: MatrixStack, stack: ItemStack, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(matrixStack, stack, mouseX, mouseY)
    }

    private object TooltipProvider : Screen(LiteralText("")) {
        init {
            this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        private fun initIfNeeded() {
            if (width != Client.window.scaledWidth || height != Client.window.scaledHeight)
                this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        public override fun renderTooltip(matrices: MatrixStack, stack: ItemStack, x: Int, y: Int) {
            super.renderTooltip(matrices, stack, x, y)
        }

        override fun renderTooltip(matrices: MatrixStack, text: Text, x: Int, y: Int) {
            super.renderTooltip(matrices, text, x, y)
        }

        override fun renderTooltip(matrices: MatrixStack, lines: MutableList<Text>, x: Int, y: Int) {
            super.renderTooltip(matrices, lines, x, y)
        }

        override fun renderOrderedTooltip(
            matrices: MatrixStack,
            lines: MutableList<out OrderedText>,
            x: Int,
            y: Int
        ) {
            initIfNeeded()
            super.renderOrderedTooltip(matrices, lines, x, y)
        }
    }
}