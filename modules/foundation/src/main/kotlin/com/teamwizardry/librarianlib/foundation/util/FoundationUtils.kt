package com.teamwizardry.librarianlib.foundation.util

import net.minecraft.item.DyeColor

public object FoundationUtils {
    /**
     * Gets the english name of a dye
     */
    @JvmStatic
    public fun dyeName(color: DyeColor): String {
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