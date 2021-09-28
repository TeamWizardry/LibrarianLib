package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.state.RenderState
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.provided.VanillaTooltipRenderer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import net.minecraft.item.ItemStack
import java.awt.Color
import kotlin.math.max

public abstract class PastryTooltip(vanilla: Boolean = false): GuiLayer() {
    public val contents: GuiLayer = GuiLayer()
    public val borderSize: Int = if(vanilla) 3 else 2
    public val background: GuiLayer = if(vanilla)
        VanillaTooltipBackground()
    else
        PastryBackground(PastryBackgroundStyle.BLACK_ROUND, 0, 0, 20, 20)

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
        val screenMargin = 5 + borderSize
        this.pos = parent?.mousePos ?: this.pos

        val screenBounds = root.convertRectTo(root.bounds.offset(screenMargin, screenMargin, -screenMargin, -screenMargin), this)
        val maxSpace = max(-screenBounds.minX - leftCursorOffset, screenBounds.maxX - rightCursorOffset)

        layoutContents(maxSpace)

        contents.pos = vec(rightCursorOffset, 0)
        if (contents.frame.maxX > screenBounds.maxX) {
            contents.x = -(contents.width + leftCursorOffset)
        }

        background.frame = contents.frame.offset(-borderSize, -borderSize, borderSize, borderSize)
    }
}

public class PastryBasicTooltip(vanilla: Boolean = false): PastryTooltip(vanilla) {
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
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 4
        textLayer.fitToText(TextFit.VERTICAL_SHRINK)

        contents.size = vec(textLayer.width + 2, textLayer.height + 2)
    }
}

public class ItemStackTooltip: GuiLayer() {
    public val stack_im: IMValue<ItemStack?> = imValue()
    public var stack: ItemStack? by stack_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        stack?.also { stack ->
            VanillaTooltipRenderer.renderTooltip(context.transformStack, stack, rootMousePos.xi, rootMousePos.yi)
        }
    }
}

public class VanillaTooltip: GuiLayer() {
    public val text_im: IMValue<String?> = imValue()
    public var text: String? by text_im

    public val lines_im: IMValue<List<String>?> = imValue()
    public var lines: List<String>? by lines_im

    override fun draw(context: GuiDrawContext) {
        val rootMousePos = root.mousePos

        (lines ?: text?.let { listOf(it) })?.also { lines ->
            VanillaTooltipRenderer.renderTooltip(context.transformStack, lines, rootMousePos.xi, rootMousePos.yi)
        }
    }
}

public class VanillaTooltipBackground: GuiLayer() {
    override fun draw(context: GuiDrawContext) {
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val buffer = FlatColorRenderBuffer.SHARED

        // the middle
        buffer.pos(context.transform, minX + 1, minY, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, minX + 1, maxY, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, maxX - 1, maxY, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, maxX - 1, minY, 0).color(backgroundColor).endVertex()

        // the left edge
        buffer.pos(context.transform, minX + 0, minY + 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, minX + 0, maxY - 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, minX + 1, maxY - 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, minX + 1, minY + 1, 0).color(backgroundColor).endVertex()

        // the right edge
        buffer.pos(context.transform, maxX - 1, minY + 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, maxX - 1, maxY - 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, maxX + 0, maxY - 1, 0).color(backgroundColor).endVertex()
        buffer.pos(context.transform, maxX + 0, minY + 1, 0).color(backgroundColor).endVertex()

        // the top inlay
        buffer.pos(context.transform, minX + 1, minY + 1, 0).color(lightInlayColor).endVertex()
        buffer.pos(context.transform, minX + 1, minY + 2, 0).color(lightInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, minY + 2, 0).color(lightInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, minY + 1, 0).color(lightInlayColor).endVertex()

        // the bottom inlay
        buffer.pos(context.transform, minX + 1, maxY - 2, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, minX + 1, maxY - 1, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, maxY - 1, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, maxY - 2, 0).color(darkInlayColor).endVertex()

        // the left inlay
        buffer.pos(context.transform, minX + 1, minY + 2, 0).color(lightInlayColor).endVertex()
        buffer.pos(context.transform, minX + 1, maxY - 2, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, minX + 2, maxY - 2, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, minX + 2, minY + 2, 0).color(lightInlayColor).endVertex()

        // the right inlay
        buffer.pos(context.transform, maxX - 2, minY + 2, 0).color(lightInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 2, maxY - 2, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, maxY - 2, 0).color(darkInlayColor).endVertex()
        buffer.pos(context.transform, maxX - 1, minY + 2, 0).color(lightInlayColor).endVertex()

        RenderState.normal.apply()
        buffer.draw(Primitive.QUADS)
        RenderState.normal.cleanup()
    }

    private companion object {
        val backgroundColor = Color(16, 0, 16, 240)
        val lightInlayColor = Color(80, 0, 255, 80)
        val darkInlayColor = Color(40, 0, 127, 80)
    }
}

