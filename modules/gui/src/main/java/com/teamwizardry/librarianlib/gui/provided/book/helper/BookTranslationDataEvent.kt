package com.teamwizardry.librarianlib.gui.provided.book.helper

import com.google.gson.JsonArray
import net.minecraftforge.fml.common.eventhandler.Event

class BookTranslationDataEvent(val providedData: JsonArray) : Event() {
    var stringValue: String = ""
}
