package com.teamwizardry.librarianlib.sprite

import net.minecraft.client.gui.FontRenderer

object TextWrapper {
    fun wrap(renderer: FontRenderer, list: MutableList<String>, str: String, initialLinePos: Int, width: Int) {

        val i = sizeStringToWidth(renderer, str, width - initialLinePos)
        if (str.length <= i) {
            list.add(str)
        } else {
            val s = str.substring(0, i)
            list.add(s)
            val c0 = str[i]
            val flag = c0.toInt() == 32 || c0.toInt() == 10
            val s1 = FontRenderer.getFormatFromString(s) + str.substring(i + if (flag) 1 else 0)

            wrap(renderer, list, s1, 0, width)
        }
    }

    private fun sizeStringToWidth(renderer: FontRenderer, str: String, wrapWidth: Int): Int {
        val i = str.length
        var j = 0
        var k = 0
        var l = -1

        var flag = false
        while (k < i) {
            val c0 = str[k]

            when (c0) {
                '\n' -> --k
                ' ' -> {
                    l = k
                    j += renderer.getCharWidth(c0)

                    if (flag) {
                        ++j
                    }
                }
                else -> {
                    j += renderer.getCharWidth(c0)
                    if (flag) {
                        ++j
                    }
                }
                '\u00a7' ->

                    if (k < i - 1) {
                        ++k
                        val c1 = str[k]

                        if (c1.toInt() != 108 && c1.toInt() != 76) {
                            if (c1.toInt() == 114 || c1.toInt() == 82 || isFormatColor(c1)) {
                                flag = false
                            }
                        } else {
                            flag = true
                        }
                    }
            }

            if (c0.toInt() == 10) {
                ++k
                l = k
                break
            }

            if (j > wrapWidth) {
                break
            }
            ++k
        }

        return if (k != i && l != -1 && l < k) l else k
    }

    fun isFormatColor(colorChar: Char): Boolean {
        return colorChar.toInt() >= 48 && colorChar.toInt() <= 57 || colorChar.toInt() >= 97 && colorChar.toInt() <= 102 || colorChar.toInt() >= 65 && colorChar.toInt() <= 70
    }
}
