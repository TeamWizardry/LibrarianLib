package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import java.awt.Color
import kotlin.math.abs

object DistinctColors {
    @JvmField
    val red = Color(0xe6194B)
    @JvmField
    val green = Color(0x3cb44b)
    @JvmField
    val yellow = Color(0xffe119)
    @JvmField
    val blue = Color(0x4363d8)
    @JvmField
    val orange = Color(0xf58231)
    @JvmField
    val purple = Color(0x911eb4)
    @JvmField
    val cyan = Color(0x42d4f4)
    @JvmField
    val magenta = Color(0xf032e6)
    @JvmField
    val lime = Color(0xbfef45)
    @JvmField
    val pink = Color(0xfabebe)
    @JvmField
    val teal = Color(0x469990)
    @JvmField
    val lavender = Color(0xe6beff)
    @JvmField
    val brown = Color(0x9A6324)
    @JvmField
    val beige = Color(0xfffac8)
    @JvmField
    val maroon = Color(0x800000)
    @JvmField
    val mint = Color(0xaaffc3)
    @JvmField
    val olive = Color(0x808000)
    @JvmField
    val apricot = Color(0xffd8b1)
    @JvmField
    val navy = Color(0x000075)
    @JvmField
    val grey = Color(0xa9a9a9)
    @JvmField
    val white = Color(0xffffff)
    @JvmField
    val black = Color(0x000000)

    @JvmField
    val colors: List<Color> = listOf(
        red, green, yellow, blue, orange, purple, cyan, magenta, lime, pink, teal, lavender, brown, beige, maroon, mint,
        olive, apricot, navy, grey, white, black
    ).unmodifiableView()

    @JvmField
    val named: Map<String, Color> = mapOf(
        "red" to red, "green" to green, "yellow" to yellow, "blue" to blue, "orange" to orange, "purple" to purple,
        "cyan" to cyan, "magenta" to magenta, "lime" to lime, "pink" to pink, "teal" to teal, "lavender" to lavender,
        "brown" to brown, "beige" to beige, "maroon" to maroon, "mint" to mint, "olive" to olive, "apricot" to apricot,
        "navy" to navy, "grey" to grey, "white" to white, "black" to black
    ).unmodifiableView()

    /**
     * Picks a color for the passed object, designed for color coding
     */
    @JvmStatic
    fun forObject(value: Any?): Color {
        return colors[abs(value.hashCode()) % colors.size]
    }
}