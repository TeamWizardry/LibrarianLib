package com.teamwizardry.librarianlib.features.utilities

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.features.kotlin.JsonDsl
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * @author WireSegal
 * Created at 3:15 PM on 7/27/18.
 */

class FileDsl<T : IForgeRegistryEntry<*>>(val value: T, val map: MutableMap<String, JsonElement> = mutableMapOf()) {

    val key get() = value.key

    operator fun String.invoke(el: JsonElement) {
        map[this] = el
    }

    inline operator fun String.invoke(producer: JsonDsl.() -> Unit) {
        map[this] = jsonObject(producer)
    }

    infix fun String.to(el: JsonElement) = this(el)
    inline infix fun String.to(producer: JsonDsl.() -> Unit) = this(producer)
}
