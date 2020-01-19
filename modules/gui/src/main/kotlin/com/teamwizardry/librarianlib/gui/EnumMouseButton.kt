package com.teamwizardry.librarianlib.gui

enum class EnumMouseButton {

    LEFT, RIGHT, MIDDLE,
    BUTTON3, BUTTON4, BUTTON5, BUTTON6, BUTTON7, BUTTON8, BUTTON9, BUTTON10, BUTTON11, BUTTON12, BUTTON13, BUTTON14, BUTTON15, BUTTON16,
    UNKNOWN;

    val mouseCode: Int
        get() = if (this == UNKNOWN) -1 else ordinal

    companion object {

        fun getFromCode(code: Int): EnumMouseButton {
            if (code < 0 || code >= values().size) {
                return UNKNOWN
            }
            return values()[code]
        }
    }

}
