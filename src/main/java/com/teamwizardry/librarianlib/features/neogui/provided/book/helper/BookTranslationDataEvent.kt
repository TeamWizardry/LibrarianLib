package com.teamwizardry.librarianlib.features.neogui.provided.book.helper

import com.google.gson.JsonArray
import net.minecraftforge.fml.common.eventhandler.Event

class BookTranslationDataEvent(val providedData: JsonArray) : Event() {
    var stringValue: String = ""
}
