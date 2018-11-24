package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.awt.Color

class MutableColor: Color(255, 0, 255) {
    fun replaceColor(other: Color) {
        this.rgbField = other.rgbField
    }
}

private var Color.rgbField by MethodHandleHelper.delegateForReadWrite<Color, Int>(Color::class.java, "rgb")