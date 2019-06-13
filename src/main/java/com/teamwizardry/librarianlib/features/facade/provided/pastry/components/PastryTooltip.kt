package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

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
    private fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
        this.setNeedsLayout()
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val leftGap = 4
        val rightGap = 6
        val margin = 5
        this.pos = (parent as? GuiComponent)?.mousePos ?: this.pos

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

@UseExperimental(ExperimentalBitfont::class)
class PastryBasicTooltip: PastryTooltip() {
    private val textLayer = TextLayer(2, 1)

    val text_im: IMValue<String> = textLayer.text_im
    var text: String by text_im

    init {
        contents.add(textLayer)
        textLayer.color = Color.WHITE
        textLayer.wrap = true
    }

    override fun layoutContents(maxWidth: Double) {
        textLayer.width = maxWidth - 4
        textLayer.fitToText()

        contents.size = vec(textLayer.textFrame.maxX + 2, textLayer.frame.maxY + 1)
    }
}
