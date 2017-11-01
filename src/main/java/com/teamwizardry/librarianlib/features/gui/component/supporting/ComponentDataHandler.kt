package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents

/**
 * TODO: Document file ComponentDataHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentDataHandler(private val component: GuiComponent) {
    private val data: MutableMap<Class<*>, MutableMap<String, Any>> = mutableMapOf()

    /**
     * Returns all valid data keys for [clazz]. Not guaranteed to be complete.
     */
    fun <D : Any> getAllDataKeys(clazz: Class<D>): Set<String> {
        if (!data.containsKey(clazz))
            return setOf()
        return component.BUS.fire(GuiComponentEvents.GetDataKeysEvent(component, clazz, data[clazz]?.keys?.toMutableSet() ?: mutableSetOf())).value
    }

    /**
     * Returns all classes for data that contain at least one value. Not guaranteed to be complete.
     */
    fun getAllDataClasses(): Set<Class<*>> {
        return component.BUS.fire(GuiComponentEvents.GetDataClassesEvent(component, data.entries.filter { it.value.isNotEmpty() }.map { it.key }.toMutableSet())).value
    }

    /** [GuiComponent.setData] */
    fun <D : Any> setData(clazz: Class<D>, key: String, value: D) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!component.BUS.fire(GuiComponentEvents.SetDataEvent(component, clazz, key, value)).isCanceled())
            data[clazz]?.put(key, value)
    }

    /** [GuiComponent.removeData] */
    fun <D : Any> removeData(clazz: Class<D>, key: String) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!component.BUS.fire(GuiComponentEvents.RemoveDataEvent(component, clazz, key, getData(clazz, key))).isCanceled())
            data[clazz]?.remove(key)
    }

    /** [GuiComponent.getData] */
    @Suppress("UNCHECKED_CAST")
    fun <D> getData(clazz: Class<D>, key: String): D? {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return component.BUS.fire(GuiComponentEvents.GetDataEvent(component, clazz, key, data[clazz]?.get(key) as D?)).value
    }

    /** [GuiComponent.hasData] */
    @Suppress("UNCHECKED_CAST")
    fun <D> hasData(clazz: Class<D>, key: String): Boolean {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return component.BUS.fire(GuiComponentEvents.GetDataEvent(component, clazz, key, data[clazz]?.get(key) as D?)).value != null
    }

    /**
     * Sets the value associated with the pair of keys [clazz] and `""`. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, value: D) {
        setData(clazz, "", value)
    }

    /**
     * Removes the value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> removeData(clazz: Class<D>) {
        removeData(clazz, "")
    }

    /**
     * Returns the value Associated with the pair of keys [clazz] and `""` if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    fun <D : Any> getData(clazz: Class<D>): D? {
        return getData(clazz, "")
    }

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> hasData(clazz: Class<D>): Boolean {
        return hasData(clazz, "")
    }
}
