package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import java.awt.Color
import kotlin.math.abs

public object DistinctColors {
    @JvmField
    public val red: Color = Color(0xe6194B)
    @JvmField
    public val green: Color = Color(0x3cb44b)
    @JvmField
    public val yellow: Color = Color(0xffe119)
    @JvmField
    public val blue: Color = Color(0x4363d8)
    @JvmField
    public val orange: Color = Color(0xf58231)
    @JvmField
    public val purple: Color = Color(0x911eb4)
    @JvmField
    public val cyan: Color = Color(0x42d4f4)
    @JvmField
    public val magenta: Color = Color(0xf032e6)
    @JvmField
    public val lime: Color = Color(0xbfef45)
    @JvmField
    public val pink: Color = Color(0xfabebe)
    @JvmField
    public val teal: Color = Color(0x469990)
    @JvmField
    public val lavender: Color = Color(0xe6beff)
    @JvmField
    public val brown: Color = Color(0x9A6324)
    @JvmField
    public val beige: Color = Color(0xfffac8)
    @JvmField
    public val maroon: Color = Color(0x800000)
    @JvmField
    public val mint: Color = Color(0xaaffc3)
    @JvmField
    public val olive: Color = Color(0x808000)
    @JvmField
    public val apricot: Color = Color(0xffd8b1)
    @JvmField
    public val navy: Color = Color(0x000075)
    @JvmField
    public val grey: Color = Color(0xa9a9a9)
    @JvmField
    public val white: Color = Color(0xffffff)
    @JvmField
    public val black: Color = Color(0x000000)

    @JvmField
    public val colors: List<Color> = listOf(
        red, green, yellow, blue, orange, purple, cyan, magenta, lime, pink, teal, lavender, brown, beige, maroon, mint,
        olive, apricot, navy, grey, white, black
    ).unmodifiableView()

    @JvmField
    public val named: Map<String, Color> = mapOf(
        "red" to red, "green" to green, "yellow" to yellow, "blue" to blue, "orange" to orange, "purple" to purple,
        "cyan" to cyan, "magenta" to magenta, "lime" to lime, "pink" to pink, "teal" to teal, "lavender" to lavender,
        "brown" to brown, "beige" to beige, "maroon" to maroon, "mint" to mint, "olive" to olive, "apricot" to apricot,
        "navy" to navy, "grey" to grey, "white" to white, "black" to black
    ).unmodifiableView()

    /**
     * Picks a color for the passed object, designed for color coding
     */
    @JvmStatic
    public fun forObject(value: Any?): Color {
        return colors[abs(value.hashCode()) % colors.size]
    }
}