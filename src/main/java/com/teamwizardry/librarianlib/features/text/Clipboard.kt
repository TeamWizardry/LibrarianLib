package com.teamwizardry.librarianlib.features.text

import games.thecodewarrior.bitfont.editor.utils.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.Toolkit.getDefaultToolkit
import javafx.scene.input.Clipboard.getSystemClipboard
import java.awt.Toolkit
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.StringSelection

object MCClipboard: Clipboard {
    override var contents: String?
        get() {
            try {
                val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)

                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return transferable.getTransferData(DataFlavor.stringFlavor) as String
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
        set(value) {
            try {
                val stringselection = StringSelection(value)
                Toolkit.getDefaultToolkit().systemClipboard.setContents(stringselection, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}