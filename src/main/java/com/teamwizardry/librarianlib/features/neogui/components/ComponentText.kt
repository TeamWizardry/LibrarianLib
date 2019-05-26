package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.neogui.value.IMValue
import com.teamwizardry.librarianlib.features.neogui.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.neogui.value.IMValueInt
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import java.awt.Color

class ComponentText @JvmOverloads constructor(posX: Int, posY: Int, var horizontal: ComponentText.TextAlignH = ComponentText.TextAlignH.LEFT, var vertical: ComponentText.TextAlignV = ComponentText.TextAlignV.TOP) : GuiComponent(posX, posY) {

    /**
     * The text to draw
     */
    val text_im: IMValue<String> = IMValue("-NULL TEXT-")
    /**
     * The color of the text
     */
    val color_im: IMValue<Color> = IMValue(Color.BLACK)
    /**
     * The wrap width in pixels, -1 for no wrapping
     */
    val wrap_im: IMValueInt = IMValueInt(-1)
    /**
     * Whether to set the font renderer's unicode and bidi flags
     */
    val unicode_im: IMValueBoolean = IMValueBoolean(false)
    /**
     * Whether to set the bidirectional flag to true when unicode is enabled
     */
    val enableUnicodeBidi_im: IMValueBoolean = IMValueBoolean(true)
    /**
     * Whether to render a shadow behind the text
     */
    val shadow_im: IMValueBoolean = IMValueBoolean(false)

    var text: String by text_im
    var color: Color by color_im
    var wrap: Int by wrap_im
    var unicode: Boolean by unicode_im
    var enableUnicodeBidi: Boolean by enableUnicodeBidi_im
    var shadow: Boolean by shadow_im

    override fun draw(partialTicks: Float) {
        val fr = Minecraft.getMinecraft().fontRenderer

        val fullText = text
        val colorHex = color.rgb
        val enableFlags = unicode
        val dropShadow = shadow

        if (enableFlags) {
            if(enableUnicodeBidi)
                fr.bidiFlag = true
            fr.unicodeFlag = true
        }

        val x = 0
        var y = 0

        val lines: List<String>

        val wrap = this.wrap
        if (wrap == -1) {
            lines = listOf(fullText)
        } else {
            lines = fr.listFormattedStringToWidth(fullText, wrap)
        }


        val height = lines.size * fr.FONT_HEIGHT
        if (vertical == TextAlignV.MIDDLE) {
            y -= height / 2
        } else if (vertical == TextAlignV.BOTTOM) {
            y -= height
        }

        for ((i, line) in lines.withIndex()) {
            var lineX = x
            val lineY = y + i * fr.FONT_HEIGHT

            val textWidth = fr.getStringWidth(line)
            if (horizontal == TextAlignH.CENTER) {
                lineX -= textWidth / 2
            } else if (horizontal == TextAlignH.RIGHT) {
                lineX -= textWidth
            }

            fr.drawString(line, lineX.toFloat(), lineY.toFloat(), colorHex, dropShadow)
        }

        if (enableFlags) {
            if(enableUnicodeBidi)
                fr.bidiFlag = false
            fr.unicodeFlag = false
        }
    }

    fun sizeToText() {
        this.size = contentSize.size
    }

    val contentSize: BoundingBox2D
        get() {
            val wrap = this.wrap

            val size: Vec2d

            val fr = Minecraft.getMinecraft().fontRenderer

            val enableFlags = unicode

            if (enableFlags) {
                if(enableUnicodeBidi)
                    fr.bidiFlag = true
                fr.unicodeFlag = true
            }

            if (wrap == -1) {
                size = vec(fr.getStringWidth(text), fr.FONT_HEIGHT)
            } else {
                val wrapped = fr.listFormattedStringToWidth(text, wrap)
                size = vec(wrap, wrapped.size * fr.FONT_HEIGHT)
            }

            if (enableFlags) {
                if(enableUnicodeBidi)
                    fr.bidiFlag = false
                fr.unicodeFlag = false
            }

            return BoundingBox2D(Vec2d.ZERO, size)
        }

    enum class TextAlignH {
        LEFT, CENTER, RIGHT
    }

    enum class TextAlignV {
        TOP, MIDDLE, BOTTOM
    }

}
