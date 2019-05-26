package com.teamwizardry.librarianlib.features.text

import games.thecodewarrior.bitfont.editor.Key
import games.thecodewarrior.bitfont.editor.Modifier
import games.thecodewarrior.bitfont.editor.Modifiers
import games.thecodewarrior.bitfont.editor.MouseButton
import org.lwjgl.input.Keyboard

fun Key.Companion.fromLwjgl(lwjgl: Int): Key {
    return keymap[lwjgl] ?: Key.UNKNOWN
}

fun Modifiers.Companion.fromKeyboard() = Modifiers(
    *listOfNotNull(
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) Modifier.SHIFT else null,
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) Modifier.CONTROL else null,
        if(Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) Modifier.ALT else null,
        if(Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)) Modifier.SUPER else null,
        if(Keyboard.isKeyDown(Keyboard.KEY_CAPITAL)) Modifier.CAPS_LOCK else null,
        if(Keyboard.isKeyDown(Keyboard.KEY_NUMLOCK)) Modifier.NUM_LOCK else null
    ).toTypedArray()
)

fun MouseButton.Companion.fromLwjgl(lwjgl: Int): MouseButton {
    if(lwjgl !in 0 until MouseButton.values().size-1)
        return MouseButton.UNKNOWN
    return MouseButton.values()[lwjgl+1]
}

// Done from Bitfont->LWJGL because that way it will complain if I'm missing any keys
private val keymap = Key.values().associateBy {
    when(it) {
        Key.UNKNOWN -> null

        Key.SPACE -> Keyboard.KEY_SPACE
        Key.APOSTROPHE -> Keyboard.KEY_APOSTROPHE
        Key.COMMA -> Keyboard.KEY_COMMA
        Key.MINUS -> Keyboard.KEY_MINUS
        Key.PERIOD -> Keyboard.KEY_PERIOD
        Key.SLASH -> Keyboard.KEY_SLASH
        Key.NUM_0 -> Keyboard.KEY_0
        Key.NUM_1 -> Keyboard.KEY_1
        Key.NUM_2 -> Keyboard.KEY_2
        Key.NUM_3 -> Keyboard.KEY_3
        Key.NUM_4 -> Keyboard.KEY_4
        Key.NUM_5 -> Keyboard.KEY_5
        Key.NUM_6 -> Keyboard.KEY_6
        Key.NUM_7 -> Keyboard.KEY_7
        Key.NUM_8 -> Keyboard.KEY_8
        Key.NUM_9 -> Keyboard.KEY_9
        Key.SEMICOLON -> Keyboard.KEY_SEMICOLON
        Key.EQUAL -> Keyboard.KEY_EQUALS
        Key.A -> Keyboard.KEY_A
        Key.B -> Keyboard.KEY_B
        Key.C -> Keyboard.KEY_C
        Key.D -> Keyboard.KEY_D
        Key.E -> Keyboard.KEY_E
        Key.F -> Keyboard.KEY_F
        Key.G -> Keyboard.KEY_G
        Key.H -> Keyboard.KEY_H
        Key.I -> Keyboard.KEY_I
        Key.J -> Keyboard.KEY_J
        Key.K -> Keyboard.KEY_K
        Key.L -> Keyboard.KEY_L
        Key.M -> Keyboard.KEY_M
        Key.N -> Keyboard.KEY_N
        Key.O -> Keyboard.KEY_O
        Key.P -> Keyboard.KEY_P
        Key.Q -> Keyboard.KEY_Q
        Key.R -> Keyboard.KEY_R
        Key.S -> Keyboard.KEY_S
        Key.T -> Keyboard.KEY_T
        Key.U -> Keyboard.KEY_U
        Key.V -> Keyboard.KEY_V
        Key.W -> Keyboard.KEY_W
        Key.X -> Keyboard.KEY_X
        Key.Y -> Keyboard.KEY_Y
        Key.Z -> Keyboard.KEY_Z
        Key.LEFT_BRACKET -> Keyboard.KEY_LBRACKET
        Key.BACKSLASH -> Keyboard.KEY_BACKSLASH
        Key.RIGHT_BRACKET -> Keyboard.KEY_RBRACKET
        Key.GRAVE_ACCENT -> Keyboard.KEY_GRAVE
        Key.SECTION -> Keyboard.KEY_SECTION
        Key.WORLD_1 -> null
        Key.WORLD_2 -> null

        Key.KANA -> Keyboard.KEY_KANA
        Key.CONVERT -> Keyboard.KEY_CONVERT
        Key.NOCONVERT -> Keyboard.KEY_NOCONVERT
        Key.YEN -> Keyboard.KEY_YEN
        Key.CIRCUMFLEX -> Keyboard.KEY_CIRCUMFLEX
        Key.AT -> Keyboard.KEY_AT
        Key.COLON -> Keyboard.KEY_COLON
        Key.UNDERLINE -> Keyboard.KEY_UNDERLINE
        Key.KANJI -> Keyboard.KEY_KANJI
        Key.STOP -> Keyboard.KEY_STOP
        Key.AX -> Keyboard.KEY_AX
        Key.UNLABELED -> Keyboard.KEY_UNLABELED

        Key.ESCAPE -> Keyboard.KEY_ESCAPE
        Key.ENTER -> Keyboard.KEY_RETURN
        Key.TAB -> Keyboard.KEY_TAB
        Key.BACKSPACE -> Keyboard.KEY_BACK
        Key.INSERT -> Keyboard.KEY_INSERT
        Key.DELETE -> Keyboard.KEY_DELETE
        Key.CLEAR -> Keyboard.KEY_CLEAR
        Key.RIGHT -> Keyboard.KEY_RIGHT
        Key.LEFT -> Keyboard.KEY_LEFT
        Key.DOWN -> Keyboard.KEY_DOWN
        Key.UP -> Keyboard.KEY_UP
        Key.PAGE_UP -> Keyboard.KEY_PRIOR
        Key.PAGE_DOWN -> Keyboard.KEY_NEXT
        Key.HOME -> Keyboard.KEY_HOME
        Key.END -> Keyboard.KEY_END
        Key.CAPS_LOCK -> Keyboard.KEY_CAPITAL
        Key.SCROLL_LOCK -> Keyboard.KEY_SCROLL
        Key.NUM_LOCK -> Keyboard.KEY_NUMLOCK
        Key.PRINT_SCREEN -> null

        Key.F1 -> Keyboard.KEY_F1
        Key.F2 -> Keyboard.KEY_F2
        Key.F3 -> Keyboard.KEY_F3
        Key.F4 -> Keyboard.KEY_F4
        Key.F5 -> Keyboard.KEY_F5
        Key.F6 -> Keyboard.KEY_F6
        Key.F7 -> Keyboard.KEY_F7
        Key.F8 -> Keyboard.KEY_F8
        Key.F9 -> Keyboard.KEY_F9
        Key.F10 -> Keyboard.KEY_F10
        Key.F11 -> Keyboard.KEY_F11
        Key.F12 -> Keyboard.KEY_F12
        Key.F13 -> Keyboard.KEY_F13
        Key.F14 -> Keyboard.KEY_F14
        Key.F15 -> Keyboard.KEY_F15
        Key.F16 -> Keyboard.KEY_F16
        Key.F17 -> Keyboard.KEY_F17
        Key.F18 -> Keyboard.KEY_F18
        Key.F19 -> Keyboard.KEY_F19
        Key.F20 -> null
        Key.F21 -> null
        Key.F22 -> null
        Key.F23 -> null
        Key.F24 -> null
        Key.F25 -> null
        Key.KP_0 -> Keyboard.KEY_NUMPAD0
        Key.KP_1 -> Keyboard.KEY_NUMPAD1
        Key.KP_2 -> Keyboard.KEY_NUMPAD2
        Key.KP_3 -> Keyboard.KEY_NUMPAD3
        Key.KP_4 -> Keyboard.KEY_NUMPAD4
        Key.KP_5 -> Keyboard.KEY_NUMPAD5
        Key.KP_6 -> Keyboard.KEY_NUMPAD6
        Key.KP_7 -> Keyboard.KEY_NUMPAD7
        Key.KP_8 -> Keyboard.KEY_NUMPAD8
        Key.KP_9 -> Keyboard.KEY_NUMPAD9
        Key.KP_DECIMAL -> Keyboard.KEY_DECIMAL
        Key.KP_DIVIDE -> Keyboard.KEY_DIVIDE
        Key.KP_MULTIPLY -> Keyboard.KEY_MULTIPLY
        Key.KP_SUBTRACT -> Keyboard.KEY_SUBTRACT
        Key.KP_ADD -> Keyboard.KEY_ADD
        Key.KP_ENTER -> Keyboard.KEY_NUMPADENTER
        Key.KP_EQUAL -> Keyboard.KEY_NUMPADEQUALS
        Key.KP_COMMA -> Keyboard.KEY_NUMPADCOMMA

        Key.FUNCTION -> Keyboard.KEY_FUNCTION
        Key.LEFT_SHIFT -> Keyboard.KEY_LSHIFT
        Key.LEFT_CONTROL -> Keyboard.KEY_LCONTROL
        Key.LEFT_ALT -> Keyboard.KEY_LMENU
        Key.LEFT_SUPER -> Keyboard.KEY_LMETA
        Key.RIGHT_SHIFT -> Keyboard.KEY_RSHIFT
        Key.RIGHT_CONTROL -> Keyboard.KEY_RCONTROL
        Key.RIGHT_ALT -> Keyboard.KEY_RMENU
        Key.RIGHT_SUPER -> Keyboard.KEY_RMETA

        Key.PAUSE -> Keyboard.KEY_PAUSE
        Key.MENU -> Keyboard.KEY_APPS
        Key.POWER -> Keyboard.KEY_POWER
        Key.SLEEP -> Keyboard.KEY_SLEEP
        Key.SYSRQ -> Keyboard.KEY_SYSRQ
    }
}