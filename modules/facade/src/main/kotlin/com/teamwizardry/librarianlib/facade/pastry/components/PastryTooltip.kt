package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.util.text.StringTextComponent
import java.awt.Color
import kotlin.math.max

abstract class PastryTooltip: GuiLayer() {
    val contents = GuiLayer()
    val background = PastryBackground(BackgroundTexture.BLACK, 0, 0, 20, 20)

    init {
        this.zIndex = GuiLayer.TOOLTIP_Z
        this.add(contents)
        contents.add(background)
        background.zIndex = -1.0
    }

    /**
     * Lay out [contents], ensuring its width is no larger than [maxWidth].
     */
    abstract fun layoutContents(maxWidth: Double)

    @Hook
    private fun preFrame(e: GuiLayerEvents.Update) {
        this.markLayoutDirty()
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val leftGap = 4
        val rightGap = 6
        val margin = 5
        this.pos = parent?.mousePos ?: this.pos

        val rootBounds = root.convertRectTo(root.bounds.offset(margin, margin, -margin, -margin), this)
        val maxSpace = max(-rootBounds.minX - leftGap, rootBounds.maxX - rightGap)

        layoutContents(maxSpace)

        contents.pos = vec(rightGap, 0)
        if(contents.frame.maxX > rootBounds.maxX) {
            contents.x = -(contents.width + leftGap)
        }

        background.frame = contents.bounds.offset(-2, -2, 2, 2)
    }
}

class PastryBasicTooltip: PastryTooltip() {
    private val textLayer = TextLayer(2, 1)

    val text_im: IMValue<String?> = textLayer.text_im
    var text: String? by text_im
    var attributedText: AttributedString
        get() = textLayer.attributedText
        set(value) { textLayer.attributedText = value }

    init {
        contents.add(textLayer)
        textLayer.color = Color.WHITE
        textLayer.wrap = true
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 4
        textLayer.fitToText()

        contents.size = vec(textLayer.textBounds.maxX + 2, textLayer.frame.maxY + 1)
    }
}

class ItemStackTooltip: GuiLayer() {
    val stack_im: IMValue<ItemStack?> = imValue()
    var stack: ItemStack? by stack_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        stack?.also { stack ->
            TooltipProvider.renderTooltip(stack, rootMousePos.xi, rootMousePos.yi)
        }
    }
}

class VanillaTooltip: GuiLayer() {
    val text_im: IMValue<String?> = imValue()
    var text: String? by text_im

    val lines_im: IMValue<List<String>?> = imValue()
    var lines: List<String>? by lines_im

    val font_im: IMValue<FontRenderer> = imValue(Client.fontRenderer)
    var font: FontRenderer by font_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        (lines ?: text?.let { listOf(it) })?.also { lines ->
            TooltipProvider.renderTooltip(lines.toMutableList(), rootMousePos.xi, rootMousePos.yi, font)

        }
    }
}

private object TooltipProvider: Screen(StringTextComponent("")) {
    public override fun renderTooltip(p_renderTooltip_1_: ItemStack, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int) {
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
    }
}
