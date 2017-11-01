package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.gui.component.supporting.*
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * The base class of every on-screen object. These can be nested within each other using [add]. Subcomponents will be
 * positioned relative to their parent, so modifications to the parent's [pos] will change their rendering position.
 *
 * # Summery
 *
 * - Events - Fire when something happens, allow you to change what happens or cancel it alltogether. Register on [BUS]
 * - Tags - Mark a component for retrieval later.
 * - Data - Store metadata in a component.
 *
 * # Detail
 *
 * ## Events
 *
 * More advanced functionality is achieved through event hooks on the component's [BUS]. All events are subclasses of
 * [Event] so a type hierarchy of that should show all events available to you. Only the child classes of [GuiComponent]
 * are fired by default, all others are either a part of a particular component class or require some action on the
 * user's part to initialize.
 *
 * ## Tags
 *
 * If you want to mark a component for retrieval later you can use [addTag] to add an arbitrary object as a tag.
 * Children with a specific tag can be retrieved later using [getByTag], or you can check if a component has a tag using
 * [hasTag]. Tags are stored in a HashSet, so any object that overrides the [hashCode] and [equals] methods will work by
 * value, but any object will work by identity. [Explanation here.](http://stackoverflow.com/a/1692882/1541907)
 *
 * ## Data
 *
 * If you need to store additional metadata in a component, this can be done with [setData]. The class passed in must be
 * the class of the data, and is used to reduce unchecked cast warnings and to ensure that the same key can be used with
 * multiple types of data. The key is used to allow multiple instances of the same data type to be stored in a component,
 * and is independent per class.
 * ```
 * component.setData(MyCoolObject.class, "foo", myInstance);
 * component.setData(MyCoolObject.class, "bar", anotherInstance);
 * component.setData(YourCoolObject.class, "foo", yourInstance);
 *
 * component.getData(MyCoolObject.class, "foo"); // => myInstance
 * component.getData(MyCoolObject.class, "bar"); // => anotherInstance
 * component.getData(YourCoolObject.class, "foo"); // => yourInstance
 * ```
 *
 */
@SideOnly(Side.CLIENT)
abstract class GuiComponent @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0) {
    /**
     * Draws the component, this is called between pre and post draw events.
     */
    open fun drawComponent(mousePos: Vec2d, partialTicks: Float) {}

    //region - Handlers
    /** Use this for advanced data manipulation and querying */
    @Suppress("LeakingThis") @JvmField val data = ComponentDataHandler(this)
    /** Use this for advanced tag manipulation and querying */
    @Suppress("LeakingThis") @JvmField val tags = ComponentTagHandler(this)
    /** Use this for advanced geometry manipulation and querying */
    @Suppress("LeakingThis") @JvmField val geometry = ComponentGeometryHandler(this)
    /** Use this for advanced parent-child relationship manipulation and querying */
    @Suppress("LeakingThis") @JvmField val relationships = ComponentRelationshipHandler(this)
    /** Use this for advanced rendering manipulation and querying */
    @Suppress("LeakingThis") @JvmField val render = ComponentRenderHandler(this)
    /** Internal handler for GUI events (mouse click, key press, etc.) */
    @Suppress("LeakingThis") @JvmField val guiEventHandler = ComponentGuiEventHandler(this)
    /** Use this to configure clipping */
    @Suppress("LeakingThis") @JvmField val clipping = ComponentClippingHandler(this)
    //endregion

    //region - Base component stuff
    @JvmField
    val BUS = EventBus()

    /**
     * Whether this component should be drawn or have events fire
     */
    var isVisible = true

    /**
     * Returns true if this component is invalid and it should be removed from its parent
     */
    var isInvalid = false
        protected set
    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    fun invalidate() {
        this.isInvalid = true
    }
    //endregion

    //region - GeometryHandler
    /**
     * The position of the component relative to its parent. This is the first operation performed by [transform]
     */
    var pos
            by geometry.transform::translate.delegate
    /**
     * The size of the in the local context
     */
    var size
            by geometry::size.delegate
    /**
     * The transformations to apply to this component
     */
    val transform
            by geometry::transform.delegate

    /**
     * Whether the mouse is over this component. For one that ignores other components being in the way look at
     * [ComponentGeometryHandler.mouseOverNoOcclusion]
     */
    var mouseOver
            by geometry::mouseOver.delegate

    /**
     * Takes [pos], which is in our context (coordinate space), and transforms it to our parent's context
     *
     * [pos] defaults to (0, 0)
     */
    fun transformToParentContext(pos: Vec2d = Vec2d.ZERO)
            = geometry.transformToParentContext(pos)

    /**
     * Transforms [pos] (`Vec2d.ZERO` by default) in our context into `other`'s context (or the root context if null)
     */
    @JvmOverloads
    fun thisPosToOtherContext(other: GuiComponent?, pos: Vec2d = Vec2d.ZERO)
            = geometry.thisPosToOtherContext(other, pos)
    /**
     * Transforms [pos] from `other`'s context (or the root context if null) to our context
     */
    fun otherPosToThisContext(other: GuiComponent?, pos: Vec2d)
            = geometry.thisPosToOtherContext(other, pos)
    //endregion

    //region - RelationshipHandler
    /**
     * The sorting factor for the ordering of components for rendering. Higher = later
     */
    var zIndex
            by relationships::zIndex.delegate
    /**
     * Gets the root component
     */
    val root
            by relationships::root.delegate
    /**
     * An unmodifiable collection of all the direct children of this component
     */
    val children
            by relationships::children.delegate
    /**
     * The parent of this component, or null if it has no parent
     */
    val parent
            by relationships::parent.delegate

    fun add(vararg children: GuiComponent)
        = relationships.add(*children)
    //endregion

    //region - TagHandler
    /**
     * Do not use this to check if a component has a tag, as event hooks can add virtual tags to components. Use [hasTag] instead.
     *
     * Returns an unmodifiable set of all the tags this component has.
     *
     * You should use [addTag] and [removeTag] to modify the tag set.
     */
    val tagList
            by tags::tagList.delegate
    /**
     * Adds the passed tag to this component if it doesn't already have it.
     * @return true if the tag didn't exist and was added
     */
    fun addTag(tag: Any)
            = tags.addTag(tag)
    /**
     * Removes the passed tag to this component if it doesn't already have it.
     * @return true if the tag existed and was removed
     */
    fun removeTag(tag: Any)
            = tags.removeTag(tag)
    /**
     * Adds or removes the passed tag to this component if it isn't already in the correct state.
     * If [shouldHave] is true this method will add the tag if it doesn't exist, if it is false
     * this method will remove the tag if it does exist
     * @param tag The tag to add or remove
     * @param shouldHave The target state for [hasTag] after calling this method
     * @return True if the tag was added or removed
     */
    fun setTag(tag: Any, shouldHave: Boolean)
            = tags.setTag(tag, shouldHave)
    /**
     * Checks if the component has the tag specified.
     */
    fun hasTag(tag: Any)
            = tags.hasTag(tag)
    //endregion

    //region - DataHandler
    /**
     * Sets the value associated with the pair of keys [clazz] and [key]. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, key: String, value: D)
            = data.setData(clazz, key, value)
    /**
     * Removes the value associated with the pair of keys [clazz] and [key]
     */
    fun <D : Any> removeData(clazz: Class<D>, key: String)
            = data.removeData(clazz, key)
    /**
     * Returns the value associated with the pair of keys [clazz] and [key] if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    fun <D : Any> getData(clazz: Class<D>, key: String)
            = data.getData(clazz, key)
    /**
     * Checks if there is a value associated with the pair of keys [clazz] and [key]
     */
    fun <D : Any> hasData(clazz: Class<D>, key: String)
            = data.hasData(clazz, key)
    //endregion

    //region - RenderHandler
    /**
     * The animator for this component. Generally stored in the root component
     */
    var animator by render::animator.delegate
    /**
     * Add the passed animations to this component's animator
     */
    fun add(vararg animations: Animation<*>) = render.add(*animations)
    //endregion

    //region - Internal
    init {
        this.pos = vec(posX, posY)
        this.size = vec(width, height)

        @Suppress("LeakingThis")
        ComponentEventHookAnnotSearcher.search(this)
    }
    //endregion

}
