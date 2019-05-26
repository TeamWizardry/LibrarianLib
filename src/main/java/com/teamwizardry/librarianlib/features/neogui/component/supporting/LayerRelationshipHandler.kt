package com.teamwizardry.librarianlib.features.neogui.component.supporting

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.neogui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.neogui.component.LayerHierarchyException
import com.teamwizardry.librarianlib.features.neogui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.kotlin.unmodifiableView
import java.lang.Exception
import java.util.*

interface ILayerRelationships {
    val zIndex_rm: RMValueDouble
    /**
     * The sort index and render order for the layer. Use [GuiLayer.OVERLAY_Z] and [GuiLayer.UNDERLAY_Z] to create
     * layers that appear on top or below _literally everything else._ In order to maintain this property, please
     * limit your z index offsets to ±1,000,000. That should be more than enough.
     *
     * Driven by [zIndex_rm]
     */
    var zIndex: Double

    /**
     * A read-only list containing all the children of this layer. For safely iterating over this list use
     * [forEachChild] as it will prevent [ConcurrentModificationException]s and prevent crashes caused by removing
     * children while iterating.
     */
    val children: List<GuiLayer>

    /**
     * Creates a read-only list containing this layer's children, their children, and so on. The children are added
     * in depth-first order.
     */
    val allChildren: List<GuiLayer>

    /**
     * A read-only set containing all the parents of this layer, recursively.
     */
    val parents: Set<GuiLayer>

    /**
     * The immediate parent of this layer, or null if this layer has no parent.
     */
    val parent: GuiLayer?

    /**
     * The root of this layer's hierarchy. i.e. the last layer found when iterating back through the parents
     */
    val root: GuiLayer

    /**
     * Adds children to this layer. Any layers that are already children of this layer will be ignored after logging a
     * warning.
     *
     * @throws LayerHierarchyException if adding one of the passed layers creates loops in the layer hierarchy
     * @throws LayerHierarchyException if one of the passed layers already had a parent that wasn't this layer
     * @throws LayerHierarchyException if one of the passed layers returns false when this layer is passed to its
     * [canAddToParent] method.
     */
    fun add(vararg layers: GuiLayer)

    /**
     * Checks whether this layer has the passed layer as a descendent
     */
    operator fun contains(layer: GuiLayer): Boolean

    /**
     * Removes the passed layer from this layer's children. If the passed layer has no parent this will log an error
     * and return immediately.
     *
     * @throws LayerHierarchyException if the passed layer has a parent that isn't this layer
     */
    fun remove(layer: GuiLayer)

    /**
     * Removes the layer from its parent if it has one. Shorthand for `this.parent?.remove(this)`
     */
    fun removeFromParent()

    /**
     * Iterates over children while allowing children to be added or removed. Any added children will not be iterated,
     * and any children removed while iterating will be excluded.
     */
    fun forEachChild(l: (GuiLayer) -> Unit)

    /**
     * Return false if [parent] is not a valid parent for this layer. This is used for the default component behavior
     * of throwing an error if added to a layer (this is most likely accidental so it is considered an error)
     */
    fun canAddToParent(parent: GuiLayer): Boolean
}

class LayerRelationshipHandler: ILayerRelationships {
    lateinit var layer: GuiLayer

    override val zIndex_rm = RMValueDouble(1.0)
    override var zIndex by zIndex_rm

    internal val subLayers = mutableListOf<GuiLayer>()

    override val children: List<GuiLayer> = subLayers.unmodifiableView()

    override val allChildren: List<GuiLayer>
        get() {
            val list = mutableListOf<GuiLayer>()
            addChildrenRecursively(list)
            return list.unmodifiableView()
        }

    private fun addChildrenRecursively(list: MutableList<GuiLayer>) {
        layer.children.forEach {
            list.add(it)
            it.relationships.addChildrenRecursively(list)
        }
    }

    override val parents: Set<GuiLayer>
        get() {
            val set = mutableSetOf<GuiLayer>()
            var head = parent
            while(head != null) {
                set.add(head)
                head = head.parent
            }
            return set.unmodifiableView()
        }

    override var parent: GuiLayer? = null

    override fun add(vararg layers: GuiLayer) {
        layers.forEach { addInternal(it) }
        layer.setNeedsLayout()
    }

    protected fun addInternal(layer: GuiLayer) {
        if (layer === this.layer)
            throw LayerHierarchyException("Tried to add a layer to itself")

        if (!layer.canAddToParent(this.layer)) {
            throw LayerHierarchyException("This layer isn't a valid parent for the passed layer")
        }
        if (layer.parent != null) {
            if (layer.parent == this.layer) {
                LibrarianLog.warn(Exception(), "The passed layer was already a child of this layer")
                return
            } else {
                throw LayerHierarchyException("The passed layer already had another parent")
            }
        }

        if (layer in parents) {
            throw LayerHierarchyException("Recursive layer hierarchy, the passed layer is an ancestor of this layer")
        }

        if (this.layer.BUS.fire(GuiLayerEvents.AddChildEvent(layer)).isCanceled())
            return
        if (layer.BUS.fire(GuiLayerEvents.AddToParentEvent(this.layer)).isCanceled())
            return
        subLayers.add(layer)
        layer.setParentInternal(this.layer)
    }

    override operator fun contains(layer: GuiLayer): Boolean =
            layer in subLayers || subLayers.any { layer in it.relationships }

    override fun remove(layer: GuiLayer) {
        if(layer.parent == null) {
            LibrarianLog.warn(Exception(), "The passed layer has no parent")
            return
        } else if (layer.parent != this.layer) {
            throw LayerHierarchyException("This isn't the layer's parent")
        }

        if (this.layer.BUS.fire(GuiLayerEvents.RemoveChildEvent(layer)).isCanceled())
            return
        if (layer.BUS.fire(GuiLayerEvents.RemoveFromParentEvent(this.layer)).isCanceled())
            return
        layer.setParentInternal(null)
        subLayers.remove(layer)
    }

    override fun removeFromParent() {
        this.layer.parent?.remove(this.layer)
    }

    override fun forEachChild(l: (GuiLayer) -> Unit) {
        subLayers.toList().asSequence().filter {
            it.parent != null // a component may have been removed, in which case it won't be expecting any interaction
        }.forEach(l)
    }

    override fun canAddToParent(parent: GuiLayer): Boolean {
        return true
    }

    override val root: GuiLayer
        get() {
            return layer.parent?.root ?: this.layer
        }
}
