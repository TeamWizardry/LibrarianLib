package com.teamwizardry.librarianlib.sprite

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.awt.Color
import java.awt.color.ColorSpace

class MutableColor: Color(255, 0, 255) {

    fun replaceColor(other: Color) {
        this.value = other.value
        this.frgbvalue = other.frgbvalue
        this.fvalue = other.fvalue
        this.falpha = other.falpha
        this.cs = other.cs
    }
}

private var Color.value by MethodHandleHelper.delegateForReadWrite<Color, Int>(Color::class.java, "value")
private var Color.frgbvalue by MethodHandleHelper.delegateForReadWrite<Color, FloatArray>(Color::class.java, "frgbvalue")
private var Color.fvalue by MethodHandleHelper.delegateForReadWrite<Color, FloatArray>(Color::class.java, "fvalue")
private var Color.falpha by MethodHandleHelper.delegateForReadWrite<Color, Float>(Color::class.java, "falpha")
private var Color.cs by MethodHandleHelper.delegateForReadWrite<Color, ColorSpace>(Color::class.java, "cs")

