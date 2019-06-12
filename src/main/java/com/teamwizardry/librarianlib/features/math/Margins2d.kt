package com.teamwizardry.librarianlib.features.math

data class Margins2d(val top: Double, val bottom: Double, val left: Double, val right: Double) {
    val width: Double get() = left + right
    val height: Double get() = top + bottom
}
