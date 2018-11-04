package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.value.RMValueInt
import java.lang.Exception
import java.util.*

interface ILayerRelationships {
    val zIndex_rm: RMValueInt
    var zIndex: Int

    val children: List<GuiLayer>
    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    val allChildren: List<GuiLayer>

    val parents: MutableSet<GuiLayer>
    val parent: GuiLayer?
    val root: GuiLayer

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(vararg components: GuiLayer?)

    /**
     * @return whether this component has [component] as a decendant
     */
    operator fun contains(component: GuiLayer): Boolean

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiLayer)

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    fun forEachChild(l: (GuiLayer) -> Unit)

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    fun <C : GuiLayer> getByClass(clazz: Class<C>): List<C>

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    fun <C : GuiLayer> getAllByClass(clazz: Class<C>): List<C>

    /**
     * Return false if [parent] is not a valid parent for this layer
     */
    fun canAddToParent(parent: GuiLayer): Boolean
}

/**
 * TODO: Document file ComponentRelationshipHandler
 *
 * Created by TheCodeWarrior
 */
class LayerRelationshipHandler: ILayerRelationships {
    lateinit var component: GuiLayer

    override val zIndex_rm = RMValueInt(0)
    override var zIndex by zIndex_rm

    internal val subLayers = mutableListOf<GuiLayer>()

    override val children: List<GuiLayer> = Collections.unmodifiableList(subLayers)

    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    override val allChildren: List<GuiLayer>
        get() {
            val list = mutableListOf<GuiLayer>()
            addChildrenRecursively(list)
            return Collections.unmodifiableList(list)
        }

    private fun addChildrenRecursively(list: MutableList<GuiLayer>) {
        list.addAll(subLayers)
        subLayers.forEach { it.relationships.addChildrenRecursively(list) }
    }

    override val parents = mutableSetOf<GuiLayer>()

    /** [GuiLayer.parent] */
    override var parent: GuiLayer? = null
        set(value) {
            parents.clear()
            if (value != null) {
                parents.addAll(value.parents)
                parents.add(value)
            }
            field = value
        }

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    override fun add(vararg components: GuiLayer?) {
        components.forEach { addInternal(it) }
        component.setNeedsLayout()
    }

    protected fun addInternal(component: GuiLayer?) {
        if (component == null) {
            LibrarianLog.error("Null component, ignoring")
            return
        }
        if (component === this.component)
            throw IllegalArgumentException("Immediately recursive component hierarchy")

        if (!component.canAddToParent(this.component)) {
            throw IllegalArgumentException("This is an invalid parent for the passed layer")
        }
        if (component.parent != null) {
            if (component.parent == this.component) {
                LibrarianLog.error("You tried to add the component to the same parent twice. Why?", Exception())
                return
            } else {
                throw IllegalArgumentException("Component already had a parent")
            }
        }

        if (component in parents) {
            throw IllegalArgumentException("Recursive component hierarchy")
        }

        if (this.component.BUS.fire(GuiLayerEvents.AddChildEvent(component)).isCanceled())
            return
        if (component.BUS.fire(GuiLayerEvents.AddToParentEvent(this.component)).isCanceled())
            return
        subLayers.add(component)
        component.parent = this.component
    }

    /**
     * @return whether this component has [component] as a decendant
     */
    override operator fun contains(component: GuiLayer): Boolean =
            component in subLayers || subLayers.any { component in it.relationships }

    /**
     * Removes the supplied component
     * @param component
     */
    override fun remove(component: GuiLayer) {
        if (component !in subLayers)
            return
        if (this.component.BUS.fire(GuiLayerEvents.RemoveChildEvent(component)).isCanceled())
            return
        if (component.BUS.fire(GuiLayerEvents.RemoveFromParentEvent(this.component)).isCanceled())
            return
        component.parent = null
        subLayers.remove(component)
    }

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    override fun forEachChild(l: (GuiLayer) -> Unit) {
        val copy = subLayers.toList()
        copy.forEach(l)
    }

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    override fun <C : GuiLayer> getByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addByClass(clazz, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    override fun <C : GuiLayer> getAllByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addAllByClass(clazz, list)
        return list
    }

    protected fun <C : GuiLayer> addAllByClass(clazz: Class<C>, list: MutableList<C>) {
        addByClass(clazz, list)
        subLayers.forEach { it.relationships.addAllByClass(clazz, list) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <C : GuiLayer> addByClass(clazz: Class<C>, list: MutableList<C>) {
        forEachChild { component ->
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
    }

    override fun canAddToParent(parent: GuiLayer): Boolean {
        return true
    }

    /** [GuiLayer.root] */
    override val root: GuiLayer
        get() {
            return component.parent?.root ?: this.component
        }
}
