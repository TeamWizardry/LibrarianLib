package com.teamwizardry.librarianlib.facade.text

import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.MutableAttributedString
import dev.thecodewarrior.bitfont.typesetting.TextAttribute
import java.awt.Color

/**
 * Convert Minecraft formatting codes into Bitfont [attributed strings][AttributedString].
 */
public object BitfontFormatting {
    public val color: TextAttribute<Color> = TextAttribute.get("color")
    public val obfuscated: TextAttribute<Boolean> = TextAttribute.get("obfuscated")
    public val bold: TextAttribute<Boolean> = TextAttribute.get("bold")
    public val underline: TextAttribute<Color> = TextAttribute.get("underline")
    public val shadow: TextAttribute<Color> = TextAttribute.get("shadow")

    @JvmStatic
    public fun convertMC(mcString: String): AttributedString {
        if ("§" !in mcString)
            return AttributedString(mcString)
        val split = mcString.splitWithDelimiters("§.")
        val attributed = MutableAttributedString(mcString.replace("§[^§]".toRegex(), "").replace("§§", "§"))

        var i = 0

        val color = Formatting<Color>(attributed, { i }) {
            mapOf(
                color to it
            )
        }
        val obfuscated = Formatting<Boolean>(attributed, { i }) {
            mapOf(
                obfuscated to it
            )
        }
        val underline = Formatting<Color>(attributed, { i }) {
            mapOf(
                underline to it
            )
        }
        val bold = Formatting<Boolean>(attributed, { i }) {
            mapOf(
                bold to it
            )
        }

        split.forEach { element ->
            if (element.startsWith("§") && element.length == 2) {
                val code = element[1].toLowerCase()
                when (code) {
                    '§' -> i++
                    'r' -> {
                        color.value = null
                        obfuscated.value = null
                        underline.value = null
                        bold.value = null
                    }
                    'k' -> obfuscated.value = true
                    'n' -> underline.value = Color(0, 0, 0, 0)
                    'l' -> bold.value = true
                    in colors -> {
                        color.value = colors[code]
                    }
                }
            } else {
                i += element.length
            }
        }

        color.end()
        obfuscated.end()
        underline.end()
        bold.end()

        return attributed
    }

    private fun String.splitWithDelimiters(regex: String) = this.split("((?<=$regex)|(?=$regex))".toRegex())
    private val colors = mapOf(
        '0' to Color(0x000000),
        '1' to Color(0x0000aa),
        '2' to Color(0x00aa00),
        '3' to Color(0x00aaaa),
        '4' to Color(0xaa0000),
        '5' to Color(0xaa00aa),
        '6' to Color(0xffaa00),
        '7' to Color(0xaaaaaa),
        '8' to Color(0x555555),
        '9' to Color(0x5555ff),
        'a' to Color(0x55ff55),
        'b' to Color(0x55ffff),
        'c' to Color(0xff5555),
        'd' to Color(0xff55ff),
        'e' to Color(0xffff55),
        'f' to Color(0xffffff)
    )

    private class Formatting<T: Any>(val attributedString: MutableAttributedString, val i: () -> Int, val mapGen: (T) -> Map<TextAttribute<*>, Any>) {
        var value: T? = null
            set(value) {
                end()
                field = value
                if (value != null) {
                    start = i()
                }
            }

        private var start = 0

        fun end() {
            value?.also { attributedString.setAttributes(start, i(), mapGen(it)) }
        }
    }
}