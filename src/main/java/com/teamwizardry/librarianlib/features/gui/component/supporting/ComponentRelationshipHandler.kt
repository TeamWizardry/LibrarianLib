package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import java.util.*

interface IComponentRelationship {
    /** [GuiComponent.zIndex] */
    var zIndex: Int
    /** [GuiComponent.children] */
    val children: Collection<GuiComponent>
    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    val allChildren: Collection<GuiComponent>
    val parents: MutableSet<GuiComponent>
    /** [GuiComponent.parent] */
    val parent: GuiComponent?
    /** [GuiComponent.root] */
    val root: GuiComponent

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(vararg components: GuiComponent?)

    /**
     * @return whether this component has [component] as a decendant
     */
    operator fun contains(component: GuiComponent): Boolean

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiComponent)

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    fun forEachChild(l: (GuiComponent) -> Unit)

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getByClass(clazz: Class<C>): List<C>

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getAllByClass(clazz: Class<C>): List<C>
}

/**
 * TODO: Document file ComponentRelationshipHandler
 *
 * Created by TheCodeWarrior
 */
open class ComponentRelationshipHandler(private val component: GuiComponent): IComponentRelationship {
    /** [GuiComponent.zIndex] */
    override var zIndex = 0
    internal val components = mutableListOf<GuiComponent>()
    /** [GuiComponent.children] */
    override val children: Collection<GuiComponent> = Collections.unmodifiableCollection(components)
    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    override val allChildren: Collection<GuiComponent>
        get() {
            val list = mutableListOf<GuiComponent>()
            addChildrenRecursively(list)
            return Collections.unmodifiableCollection(list)
        }

    private fun addChildrenRecursively(list: MutableList<GuiComponent>) {
        list.addAll(components)
        components.forEach { it.relationships.addChildrenRecursively(list) }
    }

    override val parents = mutableSetOf<GuiComponent>()

    /** [GuiComponent.parent] */
    override var parent: GuiComponent? = null
        set(value) {
            parents.clear()
            if (value != null) {
                parents.addAll(value.relationships.parents)
                parents.add(value)
            }
            field = value
        }

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    override fun add(vararg components: GuiComponent?) {
        components.forEach { addInternal(it) }
    }

    protected fun addInternal(component: GuiComponent?) {
        if (component == null) {
            LibrarianLog.error("Null component, ignoring")
            return
        }
        if (component === this.component)
            throw IllegalArgumentException("Immediately recursive component hierarchy")

        if (component.parent != null) {
            if (component.parent == this.component) {
                LibrarianLog.warn("You tried to add the component to the same parent twice. Why?")
                return
            } else {
                throw IllegalArgumentException("Component already had a parent")
            }
        }

        if (component in parents) {
            throw IllegalArgumentException("Recursive component hierarchy")
        }


        if (component.BUS.fire(GuiComponentEvents.AddChildEvent(this.component, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.AddToParentEvent(component, this.component)).isCanceled())
            return
        components.add(component)
        component.relationships.parent = this.component
    }

    /**
     * @return whether this component has [component] as a decendant
     */
    override operator fun contains(component: GuiComponent): Boolean =
            component in components || components.any { component in it.relationships }

    /**
     * Removes the supplied component
     * @param component
     */
    override fun remove(component: GuiComponent) {
        if (component !in components)
            return
        if (this.component.BUS.fire(GuiComponentEvents.RemoveChildEvent(this.component, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(component, this.component)).isCanceled())
            return
        component.relationships.parent = null
        components.remove(component)
    }

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    override fun forEachChild(l: (GuiComponent) -> Unit) {
        val copy = components.toList()
        copy.forEach(l)
    }

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    override fun <C : GuiComponent> getByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addByClass(clazz, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    override fun <C : GuiComponent> getAllByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addAllByClass(clazz, list)
        return list
    }

    protected fun <C : GuiComponent> addAllByClass(clazz: Class<C>, list: MutableList<C>) {
        addByClass(clazz, list)
        components.forEach { it.relationships.addAllByClass(clazz, list) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <C : GuiComponent> addByClass(clazz: Class<C>, list: MutableList<C>) {
        forEachChild { component ->
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
    }

    /** [GuiComponent.root] */
    override val root: GuiComponent
        get() {
            return parent?.root ?: this.component
        }
}
