package com.teamwizardry.librarianlib.features.gui.provided.book

import com.google.gson.JsonArray
import net.minecraftforge.fml.common.eventhandler.Event

class BookTranslationDataEvent(val providedData: JsonArray) : Event() {
    var stringValue: String = ""
}
