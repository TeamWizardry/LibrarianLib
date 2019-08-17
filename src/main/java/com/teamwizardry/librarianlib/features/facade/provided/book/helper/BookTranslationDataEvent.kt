package com.teamwizardry.librarianlib.features.facade.provided.book.helper

import com.google.gson.JsonArray
import net.minecraftforge.fml.common.eventhandler.Event

class BookTranslationDataEvent(val providedData: JsonArray) : Event() {
    var stringValue: String = ""
}
