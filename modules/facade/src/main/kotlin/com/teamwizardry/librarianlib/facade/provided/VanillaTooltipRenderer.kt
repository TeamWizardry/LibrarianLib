package com.teamwizardry.librarianlib.facade.provided

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.IReorderingProcessor
import net.minecraft.util.Language
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.ITextProperties
import net.minecraft.util.text.LanguageMap
import net.minecraft.util.text.StringTextComponent

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
        mouseY: Int,
        font: TextRenderer
    ) {
        TooltipProvider.renderToolTip(
            matrixStack,
            Language.getInstance().reorder(text.map { StringVisitable.plain(it) }),
            mouseX,
            mouseY,
            font
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

        public override fun renderTooltip(
            matrixStack: MatrixStack,
            text: Text,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(matrixStack, text, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderOrderedTooltip(
            matrixStack: MatrixStack,
            tooltips: List<OrderedText>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderOrderedTooltip(matrixStack, tooltips, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderToolTip(
            matrixStack: MatrixStack,
            tooltips: List<OrderedText>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int,
            font: TextRenderer
        ) {
            initIfNeeded()
            super.renderToolTip(matrixStack, tooltips, p_renderTooltip_2_, p_renderTooltip_3_, font)
        }

        public override fun renderTooltip(
            matrixStack: MatrixStack,
            p_renderTooltip_1_: ItemStack,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(matrixStack, p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        }
    }
}