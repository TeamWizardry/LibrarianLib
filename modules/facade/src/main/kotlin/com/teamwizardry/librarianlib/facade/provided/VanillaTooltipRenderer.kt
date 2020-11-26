package com.teamwizardry.librarianlib.facade.provided

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.util.text.StringTextComponent

/**
 * Public access to the protected tooltip rendering methods in [Screen]
 */
public object VanillaTooltipRenderer {
    @JvmStatic
    public fun renderTooltip(text: String, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(text, mouseX, mouseY)
    }

    @JvmStatic
    public fun renderTooltip(lines: List<String>, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(lines, mouseX, mouseY)
    }

    @JvmStatic
    public fun renderTooltip(text: List<String>, mouseX: Int, mouseY: Int, font: FontRenderer) {
        TooltipProvider.renderTooltip(text, mouseX, mouseY, font)
    }

    @JvmStatic
    public fun renderTooltip(stack: ItemStack, mouseX: Int, mouseY: Int) {
        TooltipProvider.renderTooltip(stack, mouseX, mouseY)
    }

    private object TooltipProvider: Screen(StringTextComponent("")) {
        init {
            this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        private fun initIfNeeded() {
            if (width != Client.window.scaledWidth || height != Client.window.scaledHeight)
                this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
        }

        public override fun renderTooltip(
            p_renderTooltip_1_: String,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderTooltip(
            p_renderTooltip_1_: List<String>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        }

        public override fun renderTooltip(
            p_renderTooltip_1_: List<String>,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int,
            font: FontRenderer
        ) {
            initIfNeeded()
            super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, font)
        }

        public override fun renderTooltip(
            p_renderTooltip_1_: ItemStack,
            p_renderTooltip_2_: Int,
            p_renderTooltip_3_: Int
        ) {
            initIfNeeded()
            super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        }
    }
}