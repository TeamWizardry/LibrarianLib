package com.teamwizardry.librarianlib.features.text

import games.thecodewarrior.bitfont.editor.utils.Clipboard
import net.minecraft.client.gui.GuiScreen

object MCClipboard: Clipboard {
    override var contents: String?
        get() {
            return GuiScreen.getClipboardString()
        }
        set(value) {
            if(value == null) return
            GuiScreen.setClipboardString(value)
        }
}