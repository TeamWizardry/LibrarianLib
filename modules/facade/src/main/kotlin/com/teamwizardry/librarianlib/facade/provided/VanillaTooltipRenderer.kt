package com.teamwizardry.librarianlib.facade.provided

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.util.IReorderingProcessor
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
        TooltipProvider.renderTooltip(matrixStack, StringTextComponent(text), mouseX, mouseY)
    }

    @JvmStatic
    public fun renderTooltip(
        matrixStack: MatrixStack,
        text: List<String>,
        mouseX: Int,
        mouseY: Int,
        font: FontRenderer
    ) {
        TooltipProvider.renderToolTip(
            matrixStack,
            LanguageMap.getInstance().func_244260_a(text.map { ITextProperties.func_240652_a_(it) }),
            mouseX,
            mouseY,
            font
        )
    }

    @JvmStatic
    public fun renderTooltip(matrixStack: MatrixStack, stack: ItemStack, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(matrixStack, stack, mouseX, mouseY)
    }

    private object TooltipProvider : Screen(StringTextComponent("")) {
        init {
            this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        private fun initIfNeeded() {
            if (width != Client.window.scaledWidth || height != Client.window.scaledHeight)
                this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        public override fun renderTooltip(
            matrixStack: MatrixStack,
            text: ITextComponent,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(matrixStack, text, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderTooltip(
            matrixStack: MatrixStack,
            tooltips: List<IReorderingProcessor>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(matrixStack, tooltips, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderToolTip(
            matrixStack: MatrixStack,
            tooltips: List<IReorderingProcessor>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int,
            font: FontRenderer
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