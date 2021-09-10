package com.teamwizardry.librarianlib.facade.pastry.layers

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

public abstract class PastryTooltip: GuiLayer() {
    public val contents: GuiLayer = GuiLayer()
    public val background: PastryBackground = PastryBackground(PastryBackgroundStyle.BLACK_ROUND, 0, 0, 20, 20)

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

