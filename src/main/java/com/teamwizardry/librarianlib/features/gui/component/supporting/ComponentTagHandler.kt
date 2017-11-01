package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import java.util.*

/**
 * TODO: Document file ComponentTagHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentTagHandler(private val component: GuiComponent) {
    private var tagStorage: MutableSet<Any> = HashSet()

    /** [GuiComponent.tagList] */
    internal val tagList = Collections.unmodifiableSet<Any>(tagStorage)!!

    /** [GuiComponent.addTag] */
    fun addTag(tag: Any): Boolean {
        if (!component.BUS.fire(GuiComponentEvents.AddTagEvent(component, tag)).isCanceled())
            if (tagStorage.add(tag))
                return true
        return false
    }

    /** [GuiComponent.removeTag] */
    fun removeTag(tag: Any): Boolean {
        if (!component.BUS.fire(GuiComponentEvents.RemoveTagEvent(component, tag)).isCanceled())
            if (tagStorage.remove(tag))
                return true
        return false
    }

    /** [GuiComponent.setTag] */
    fun setTag(tag: Any, shouldHave: Boolean): Boolean {
        if(shouldHave)
            return addTag(tag)
        else
            return removeTag(tag)
    }

    /** [GuiComponent.hasTag] */
    fun hasTag(tag: Any): Boolean {
        return component.BUS.fire(GuiComponentEvents.HasTagEvent(component, tag, tagStorage.contains(tag))).hasTag
    }

    /**
     * Returns a list of all children that have the tag [tag]
     */
    fun getByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addByTag(tag, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that have the tag [tag]
     */
    fun getAllByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addAllByTag(tag, list)
        return list
    }

    /**
     * Removes all components that have the supplied tag
     */
    fun removeByTag(tag: Any) {
        component.relationships.components.removeAll { e ->
            var b = e.hasTag(tag)
            if (component.BUS.fire(GuiComponentEvents.RemoveChildEvent(component, e)).isCanceled())
                b = false
            if (e.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(e, component)).isCanceled())
                b = false
            if (b) {
                e.relationships.parent = null
            }
            b
        }
    }

    private fun addAllByTag(tag: Any, list: MutableList<GuiComponent>) {
        addByTag(tag, list)
        component.relationships.components.forEach { it.tags.addAllByTag(tag, list) }
    }

    private fun addByTag(tag: Any, list: MutableList<GuiComponent>) {
        component.relationships.components.filterTo(list) { it.hasTag(tag) }
    }
}
