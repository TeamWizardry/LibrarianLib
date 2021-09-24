package com.teamwizardry.librarianlib.facade.layers.text

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
import dev.thecodewarrior.bitfont.typesetting.SimpleTextContainer
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

public class BitfontContainerLayer(posX: Int, posY: Int, width: Int, height: Int) : GuiLayer(posX, posY, width, height) {
    public val container: TextContainer = SimpleTextContainer(1)

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public val color_im: IMValue<Color> = imValue(Color.BLACK)

    /**
     * The color of the text. (can be overridden by color attributes in the string)
     */
    public var color: Color by color_im

    /**
     * If and how this layer should automatically fit its size to the contained text.
     */
    public var textFitting: TextFit = TextFit.NONE

    /**
     * The logical bounds of the text
     */
    public var textBounds: Rect2d = rect(0, 0, 0, 0)
        private set

    public fun prepareTextContainer() {
        container.width = this.widthi
        container.height = this.heighti

        when (textFitting) {
            TextFit.NONE -> {
            }
            TextFit.VERTICAL_SHRINK, TextFit.VERTICAL -> {
                container.height = Int.MAX_VALUE
            }
            TextFit.HORIZONTAL -> {
                container.width = Int.MAX_VALUE
            }
            TextFit.BOTH -> {
                container.height = Int.MAX_VALUE
                container.width = Int.MAX_VALUE
            }
        }
    }

    public fun applyTextLayout() {
        var minX = 0
        var minY = 0
        var maxX = 0
        var maxY = 0
        container.lines.forEach { line ->
            minX = min(minX, line.posX)
            minY = min(minY, line.posY)

            maxX = max(maxX, line.posX + (line.clusters.maxOfOrNull { if(it.isBlank) 0 else it.baselineEnd } ?: 0))
            maxY = max(maxY, line.posY + line.height)
        }
        textBounds = rect(minX, minY, maxX, maxY)

        when (textFitting) {
            TextFit.NONE -> {
            }
            TextFit.VERTICAL -> {
                heighti = maxY
            }
            TextFit.HORIZONTAL -> {
                widthi = maxX
            }
            TextFit.VERTICAL_SHRINK, TextFit.BOTH -> {
                heighti = maxY
                widthi = maxX
            }
        }

        container.width = this.widthi
        container.height = this.heighti
    }

    override fun draw(context: GuiDrawContext) {
        BitfontRenderer.draw(context.transform, container, color)
    }
}
