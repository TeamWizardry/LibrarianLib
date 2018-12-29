package com.teamwizardry.librarianlib.features.preferences

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.Event
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ModPreferences private constructor(val modid: String, configureGson: (GsonBuilder) -> Unit) {
    private val gson: Gson
    private val preferences = mutableMapOf<String, Preference<*>>()

    init {
        val gsonBuilder = GsonBuilder()
        configureGson(gsonBuilder)
        gson = gsonBuilder.create()
    }

    fun <T> create(name: String, token: TypeToken<T>, default: T): Preference<T> {
        @Suppress("UNCHECKED_CAST")
        return preferences.getOrPut(name) {
            Preference(default, token)
        } as Preference<T>
    }

    inline fun <reified T> create(name: String, default: T): Preference<T> {
        return create(name, object : TypeToken<T>() {}, default)
    }

    companion object {
        private val mods = mutableMapOf<String, ModPreferences>()

        @JvmStatic
        fun create(modid: String, configureGson: (GsonBuilder) -> Unit): ModPreferences {
            return mods.getOrPut(modid) { ModPreferences(modid, configureGson) }
        }
    }
}

class Preference<T>(val default: T, val token: TypeToken<T>): ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}