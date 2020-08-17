package com.teamwizardry.librarianlib.foundation

import net.minecraft.item.DyeColor

object FoundationUtils {
    /**
     * Gets the english name of a dye
     */
    @JvmStatic
    fun dyeName(color: DyeColor): String {
        return when(color) {
            DyeColor.WHITE -> "White"
            DyeColor.ORANGE -> "Orange"
            DyeColor.MAGENTA -> "Magenta"
            DyeColor.LIGHT_BLUE -> "Light Blue"
            DyeColor.YELLOW -> "Yellow"
            DyeColor.LIME -> "Lime"
            DyeColor.PINK -> "Pink"
            DyeColor.GRAY -> "Gray"
            DyeColor.LIGHT_GRAY -> "Light Gray"
            DyeColor.CYAN -> "Cyan"
            DyeColor.PURPLE -> "Purple"
            DyeColor.BLUE -> "Blue"
            DyeColor.BROWN -> "Brown"
            DyeColor.GREEN -> "Green"
            DyeColor.RED -> "Red"
            DyeColor.BLACK -> "Black"
        }
    }
}