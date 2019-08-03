package com.teamwizardry.librarianlib.gui.component.supporting

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import java.util.*

interface ILayerTag {
    val tagList: MutableSet<Any>

    /** [GuiLayer.addTag] */
    fun addTag(tag: Any): Boolean

    /** [GuiLayer.removeTag] */
    fun removeTag(tag: Any): Boolean

    /** [GuiLayer.setTag] */
    fun setTag(tag: Any, shouldHave: Boolean): Boolean

    /** [GuiLayer.hasTag] */
    fun hasTag(tag: Any): Boolean
}

/**
 * TODO: Document file LayerTagHandler
 *
 * Created by TheCodeWarrior
 */
class LayerTagHandler: ILayerTag {
    lateinit var layer: GuiLayer

    private var tagStorage: MutableSet<Any> = HashSet()

    /** [GuiLayer.tagList] */
    override val tagList = Collections.unmodifiableSet<Any>(tagStorage)!!

    /** [GuiLayer.addTag] */
    override fun addTag(tag: Any): Boolean {
            if (tagStorage.add(tag))
                return true
        return false
    }

    /** [GuiLayer.removeTag] */
    override fun removeTag(tag: Any): Boolean {
            if (tagStorage.remove(tag))
                return true
        return false
    }

    /** [GuiLayer.setTag] */
    override fun setTag(tag: Any, shouldHave: Boolean): Boolean {
        if (shouldHave)
            return addTag(tag)
        else
            return removeTag(tag)
    }

    /** [GuiLayer.hasTag] */
    override fun hasTag(tag: Any): Boolean {
        return tagStorage.contains(tag)
    }
}
