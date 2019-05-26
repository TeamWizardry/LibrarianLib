package com.teamwizardry.librarianlib.features.neogui.component.supporting

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import java.util.*

interface IComponentTag {
    val tagList: MutableSet<Any>

    /** [GuiComponent.addTag] */
    fun addTag(tag: Any): Boolean

    /** [GuiComponent.removeTag] */
    fun removeTag(tag: Any): Boolean

    /** [GuiComponent.setTag] */
    fun setTag(tag: Any, shouldHave: Boolean): Boolean

    /** [GuiComponent.hasTag] */
    fun hasTag(tag: Any): Boolean

    /**
     * Returns a list of all children that have the tag [tag]
     */
    fun getByTag(tag: Any): List<GuiComponent>

    /**
     * Returns a list of all children and grandchildren etc. that have the tag [tag]
     */
    fun getAllByTag(tag: Any): List<GuiComponent>

    /**
     * Removes all components that have the supplied tag
     */
    fun removeByTag(tag: Any)
}

/**
 * TODO: Document file ComponentTagHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentTagHandler: IComponentTag {
    lateinit var component: GuiComponent

    private var tagStorage: MutableSet<Any> = HashSet()

    /** [GuiComponent.tagList] */
    override val tagList = Collections.unmodifiableSet<Any>(tagStorage)!!

    /** [GuiComponent.addTag] */
    override fun addTag(tag: Any): Boolean {
            if (tagStorage.add(tag))
                return true
        return false
    }

    /** [GuiComponent.removeTag] */
    override fun removeTag(tag: Any): Boolean {
            if (tagStorage.remove(tag))
                return true
        return false
    }

    /** [GuiComponent.setTag] */
    override fun setTag(tag: Any, shouldHave: Boolean): Boolean {
        if (shouldHave)
            return addTag(tag)
        else
            return removeTag(tag)
    }

    /** [GuiComponent.hasTag] */
    override fun hasTag(tag: Any): Boolean {
        return tagStorage.contains(tag)
    }

    /**
     * Returns a list of all children that have the tag [tag]
     */
    override fun getByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addByTag(tag, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that have the tag [tag]
     */
    override fun getAllByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addAllByTag(tag, list)
        return list
    }

    /**
     * Removes all components that have the supplied tag
     */
    override fun removeByTag(tag: Any) {
        component.subComponents.forEach {
            if(it.hasTag(tag)) {
                component.remove(it)
            }
        }
    }

    private fun addAllByTag(tag: Any, list: MutableList<GuiComponent>) {
        addByTag(tag, list)
        component.subComponents.forEach { it.tags.addAllByTag(tag, list) }
    }

    private fun addByTag(tag: Any, list: MutableList<GuiComponent>) {
        component.subComponents.filterTo(list) { it.hasTag(tag) }
    }
}
