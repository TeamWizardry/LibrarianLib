package com.teamwizardry.librarianlib.client.util

import net.minecraft.client.renderer.GlStateManager

class Color {

    val r: Float
    val g: Float
    val b: Float
    val a: Float
    val h: Float
    val s: Float
    val v: Float

    constructor(r: Float, g: Float, b: Float, a: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
        val hsv = FloatArray(3)
        java.awt.Color.RGBtoHSB((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), hsv)
        this.h = hsv[0]
        this.s = hsv[1]
        this.v = hsv[2]
    }

    constructor(r: Float, g: Float, b: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = 1f
        val hsv = FloatArray(3)
        java.awt.Color.RGBtoHSB((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), hsv)
        this.h = hsv[0]
        this.s = hsv[1]
        this.v = hsv[2]
    }

    fun glColor() {
        GlStateManager.color(r, g, b, a)
    }

    fun hexRGBA(): Int {
        return (r * 255).toInt() shl 24 or ((g * 255).toInt() shl 16) or ((b * 255).toInt() shl 8) or (a * 255).toInt()
    }

    fun hexARGB(): Int {
        return (a * 255).toInt() shl 24 or ((r * 255).toInt() shl 16) or ((g * 255).toInt() shl 8) or (b * 255).toInt()
    }

    companion object {

        @JvmStatic val BLACK = rgb(0x000000)
        @JvmStatic val WHITE = rgb(0xFFFFFF)
        @JvmStatic val RED = rgb(0xFF0000)
        @JvmStatic val GREEN = rgb(0x00FF00)
        @JvmStatic val BLUE = rgb(0x0000FF)
        @JvmStatic val TRANSPARENT = argb(0x00FFFFFF)

        fun argb(color: Int): Color {
            val a = (color shr 24 and 0xff) / 255f
            val r = (color shr 16 and 0xff) / 255f
            val g = (color shr 8 and 0xff) / 255f
            val b = (color and 0xff) / 255f
            return Color(r, g, b, a)
        }

        fun rgba(color: Int): Color {
            val r = (color shr 24 and 0xff) / 255f
            val g = (color shr 16 and 0xff) / 255f
            val b = (color shr 8 and 0xff) / 255f
            val a = (color and 0xff) / 255f
            return Color(r, g, b, a)
        }

        fun rgb(color: Int): Color {
            val r = (color shr 16 and 0xff) / 255f
            val g = (color shr 8 and 0xff) / 255f
            val b = (color and 0xff) / 255f
            return Color(r, g, b)
        }
    }

}
