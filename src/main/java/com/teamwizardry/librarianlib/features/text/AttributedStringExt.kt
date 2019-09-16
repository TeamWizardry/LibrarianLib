@file:JvmName("AttributedStringUtils")
package com.teamwizardry.librarianlib.features.text

import games.thecodewarrior.bitfont.typesetting.AttributedString
import games.thecodewarrior.bitfont.typesetting.MutableAttributedString
import games.thecodewarrior.bitfont.typesetting.font
import games.thecodewarrior.bitfont.utils.Attribute
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color

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
private val shadowColors = mapOf(
    '0' to Color(0x000000),
    '1' to Color(0x00002a),
    '2' to Color(0x002a00),
    '3' to Color(0x002a2a),
    '4' to Color(0x2a0000),
    '5' to Color(0x2a002a),
    '6' to Color(0x3f2a00),
    '7' to Color(0x2a2a2a),
    '8' to Color(0x151515),
    '9' to Color(0x15153f),
    'a' to Color(0x153f15),
    'b' to Color(0x153f3f),
    'c' to Color(0x3f1515),
    'd' to Color(0x3f153f),
    'e' to Color(0x3f3f15),
    'f' to Color(0x3f3f3f)
)

@ExperimentalBitfont
private class Formatting<T: Any>(val attributedString: MutableAttributedString, val i: () -> Int, val mapGen: (T) -> Map<Attribute<*>, Any>) {
    var value: T? = null
        set(value) {
            end()
            field = value
            if(value != null) {
                start = i()
            }
        }

    private var start = 0

    fun end() {
        value?.also { attributedString.setAttributes(start, i(), mapGen(it)) }
    }
}

@ExperimentalBitfont
fun attributedStringFromMC(mcString: String): AttributedString {
    val split = mcString.splitWithDelimiters("§.")
    val attributed = MutableAttributedString(mcString.replace("§[^§]".toRegex(), "").replace("§§", "§"))

    var i = 0

    val color = Formatting<Color>(attributed, { i }) {
        mapOf(
            Attribute.color to it
        )
    }
    val obfuscated = Formatting<Boolean>(attributed, { i }) {
        mapOf(
            Attribute.obfuscated to it
        )
    }
    val underline = Formatting<Color>(attributed, { i }) {
        mapOf(
            Attribute.underline to it
        )
    }
    val bold = Formatting<Boolean>(attributed, { i }) {
        mapOf(
//            Attribute.font to Fonts.MCBitfontBold
        )
    }

    split.forEach { element ->
        if(element.startsWith("§") && element.length == 2) {
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

@ExperimentalBitfont
fun AttributedString.Companion.fromMC(mcString: String): AttributedString {
    return attributedStringFromMC(mcString)
}