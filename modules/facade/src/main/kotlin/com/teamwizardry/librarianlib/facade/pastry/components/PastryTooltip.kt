package com.teamwizardry.librarianlib.facade.pastry.components

import com.mojang.blaze3d.systems.RenderSystem
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
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.util.text.StringTextComponent
import java.awt.Color
import kotlin.math.max

public abstract class PastryTooltip: GuiLayer() {
    public val contents: GuiLayer = GuiLayer()
    public val background: PastryBackground = PastryBackground(BackgroundTexture.BLACK, 0, 0, 20, 20)

    init {
        this.zIndex = TOOLTIP_Z
        this.add(background, contents)
        background.zIndex = -1.0
    }

    /**
     * Lay out [contents], ensuring its width is no larger than [maxWidth].
     */
    public abstract fun layoutContents(maxWidth: Double)

    @Hook
    private fun preFrame(e: GuiLayerEvents.Update) {
        this.markLayoutDirty()
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val leftCursorOffset = 4
        val rightCursorOffset = 6
        val screenMargin = 5
        this.pos = parent?.mousePos ?: this.pos

        val screenBounds = root.convertRectTo(root.bounds.offset(screenMargin, screenMargin, -screenMargin, -screenMargin), this)
        val maxSpace = max(-screenBounds.minX - leftCursorOffset, screenBounds.maxX - rightCursorOffset)

        layoutContents(maxSpace)

        contents.pos = vec(rightCursorOffset, 0)
        if (contents.frame.maxX > screenBounds.maxX) {
            contents.x = -(contents.width + leftCursorOffset)
        }

        background.frame = contents.frame.offset(-2, -2, 2, 2)
    }
}

public class PastryBasicTooltip: PastryTooltip() {
    private val textLayer = TextLayer(1, 1)

    public val text_im: IMValue<String?> = textLayer.text_im
    public var text: String? by text_im
    public var attributedText: AttributedString
        get() = textLayer.attributedText
        set(value) {
            textLayer.attributedText = value
        }

    init {
        contents.add(textLayer)
        textLayer.color = Color.WHITE
        textLayer.wrap = true
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 4
        textLayer.fitToText()

        contents.size = vec(textLayer.width + 2, textLayer.height + 2)
    }
}

public class ItemStackTooltip: GuiLayer() {
    public val stack_im: IMValue<ItemStack?> = imValue()
    public var stack: ItemStack? by stack_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        stack?.also { stack ->
            TooltipProvider.renderTooltip(stack, rootMousePos.xi, rootMousePos.yi)
        }
    }
}

public class VanillaTooltip: GuiLayer() {
    public val text_im: IMValue<String?> = imValue()
    public var text: String? by text_im

    public val lines_im: IMValue<List<String>?> = imValue()
    public var lines: List<String>? by lines_im

    public val font_im: IMValue<FontRenderer> = imValue(Client.fontRenderer)
    public var font: FontRenderer by font_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        (lines ?: text?.let { listOf(it) })?.also { lines ->
            TooltipProvider.renderTooltip(lines, rootMousePos.xi, rootMousePos.yi, font)
        }
    }
}

private object TooltipProvider: Screen(StringTextComponent("")) {
    init {
        this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
    }

    private fun initIfNeeded() {
        if (width != Client.window.scaledWidth || height != Client.window.scaledHeight)
            this.init(Client.minecraft, Client.window.scaledWidth, Client.window.scaledHeight)
    }

    private fun prepareTooltip() {
        initIfNeeded()
        val s = Client.guiScaleFactor
        RenderSystem.scaled(s, s, 1.0)
    }

    private fun cleanupTooltip() {
        val s = Client.guiScaleFactor
        RenderSystem.scaled(1 / s, 1 / s, 1.0)
    }

    override fun renderTooltip(p_renderTooltip_1_: String, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int) {
        prepareTooltip()
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        cleanupTooltip()
    }

    override fun renderTooltip(p_renderTooltip_1_: List<String>, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int) {
        prepareTooltip()
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
        cleanupTooltip()
    }

    override fun renderTooltip(p_renderTooltip_1_: List<String>, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int, font: FontRenderer) {
        prepareTooltip()
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, font)
        cleanupTooltip()
    }

    public override fun renderTooltip(p_renderTooltip_1_: ItemStack, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int) {
        initIfNeeded() // for some reason itemstack tooltips don't need to be scaled
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
    }
}
