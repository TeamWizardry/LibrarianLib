package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.core.ClientTickHandler
import com.teamwizardry.librarianlib.client.gui.GuiComponent.*
import com.teamwizardry.librarianlib.common.util.div
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.event.EventBus
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.BoundingBox2D
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.*

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
 * ## Logical size
 *
 * The logical size of a component is theoretically the amount of space a component takes up. This is used for general
 * flow, such as when a list is laid out, the logical height of each element is taken into account in order to stack them.
 *
 * ## Default events
 *
 * ### Common Events
 * - [ComponentTickEvent] - Fired each tick while the component is a part of a screen
 * - [PreDrawEvent] - Fired each frame before the component has been drawn
 * - [PreChildrenDrawEvent] - Fired each frame after the component has been drawn but before children have been drawn
 * - [PostDrawEvent] - Fired each frame after the component and its children have been drawn
 * - ---
 * - [MouseDownEvent] - Fired whenever the mouse is pressed
 * - [MouseUpEvent] - Fired whenever the mouse is released
 * - [MouseDragEvent] - Fired whenever the mouse is moved while a button is being pressed
 * - [MouseClickEvent] - Fired when the mouse is clicked within the component
 * - ---
 * - [KeyDownEvent] - Fired when a key is pressed
 * - [KeyUpEvent] - Fired when a key is released
 * - ---
 * - [MouseInEvent] - Fired when the mouse is moved into the component
 * - [MouseOutEvent] - Fired when the mouse is moved out of the component
 * - [MouseWheelEvent] - Fired when the mouse wheel is moved
 * - ---
 * - [FocusEvent] - Fired when the component gains focus
 * - [BlurEvent] - Fired when the component loses focus
 * - ---
 * - [EnableEvent] - Fired when this component is enabled
 * - [DisableEvent] - Fired when this component is disabled
 *
 * ### Seldom used events
 * - [AddChildEvent] - Fired before a child is added to the component
 * - [RemoveChildEvent] - Fired when a child is removed from the component
 * - [AddToParentEvent] - Fired when the component is added as a child to another component
 * - [RemoveFromParentEvent] - Fired when the component is removed from its parent
 * - ---
 * - [SetDataEvent] - Fired before data is set
 * - [RemoveDataEvent] - Fired before data is removed
 * - [GetDataEvent] - Fired when data is queried
 * - ---
 * - [HasTagEvent] - Fired when the component is checked for a tag
 * - [AddTagEvent] - Fired before a tag is added to the component
 * - [RemoveTagEvent] - Fired before a tag is removed from a component
 *
 * ### Advanced events
 * - [LogicalSizeEvent] - Fired when the logical size is queried
 * - [MouseOverEvent] - Fired when checking if the mouse is over this component
 */
@SideOnly(Side.CLIENT)
abstract class GuiComponent<T : GuiComponent<T>> @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0) : IGuiDrawable {

    @JvmField
    val BUS = EventBus()

    class ComponentTickEvent<T : GuiComponent<T>>(val component: T) : Event()

    class PreDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event()
    class PostDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event(true)
    class PreChildrenDrawEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val partialTicks: Float) : Event()

    class MouseDownEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseUpEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseDragEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class MouseClickEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()

    class KeyDownEvent<T : GuiComponent<T>>(val component: T, val key: Char, val keyCode: Int) : EventCancelable()
    class KeyUpEvent<T : GuiComponent<T>>(val component: T, val key: Char, val keyCode: Int) : EventCancelable()

    class MouseInEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d) : Event()
    class MouseOutEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d) : Event()
    class MouseWheelEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, val direction: MouseWheelDirection) : EventCancelable()
    enum class MouseWheelDirection(@JvmField val ydirection: Int) {
        UP(+1), DOWN(-1);

        companion object {
            @JvmStatic
            fun fromSign(dir: Int): MouseWheelDirection {
                return if (dir >= 0) UP else DOWN
            }
        }
    }

    class FocusEvent<T : GuiComponent<T>>(val component: T) : Event()
    class BlurEvent<T : GuiComponent<T>>(val component: T) : Event()
    class EnableEvent<T : GuiComponent<T>>(val component: T) : Event()
    class DisableEvent<T : GuiComponent<T>>(val component: T) : Event()

    class AddChildEvent<T : GuiComponent<T>>(val component: T, val child: GuiComponent<*>) : EventCancelable()
    class RemoveChildEvent<T : GuiComponent<T>>(val component: T, val child: GuiComponent<*>) : EventCancelable()
    class AddToParentEvent<out T : GuiComponent<*>>(val component: T, val parent: GuiComponent<*>) : EventCancelable()
    class RemoveFromParentEvent<out T : GuiComponent<*>>(val component: T, val parent: GuiComponent<*>) : EventCancelable()

    class SetDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D) : EventCancelable()
    class RemoveDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D?) : EventCancelable()
    class GetDataEvent<T : GuiComponent<T>, D>(val component: T, val clazz: Class<D>, val key: String, val value: D?) : Event()

    class HasTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any, var hasTag: Boolean) : Event()
    class AddTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any) : EventCancelable()
    class RemoveTagEvent<T : GuiComponent<T>>(val component: T, val tag: Any) : EventCancelable()

    class LogicalSizeEvent<T : GuiComponent<T>>(val component: T, var box: BoundingBox2D?) : Event()
    class MouseOverEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, var isOver: Boolean) : Event()

    class MessageArriveEvent<T : GuiComponent<T>>(val component: T, val from: GuiComponent<*>, val message: Message) : Event()

    data class Message(val component: GuiComponent<*>, val data: Any, val rippleType: EnumRippleType)
    enum class EnumRippleType { NONE, UP, DOWN, ALL }

    var zIndex = 0
    /**
     * The position of the component relative to it's parent
     */
    var pos: Vec2d
    /**
     * The size of the component
     */
    var size: Vec2d
    /**
     * The left margin for logical alignment
     */
    var marginLeft: Double = 0.0
    /**
     * The right margin for logical alignment
     */
    var marginRight: Double = 0.0
    /**
     * The top margin for logical alignment
     */
    var marginTop: Double = 0.0
    /**
     * The bottom margin for logical alignment
     */
    var marginBottom: Double = 0.0

    var mouseOver = false
    var mousePosThisFrame = Vec2d.ZERO
    protected var tagStorage: MutableSet<Any> = HashSet<Any>()
    /**
     * Do not use this to check if a component has a tag, as event hooks can add virtual tags to components. Use [hasTag] instead.
     *
     * Returns an unmodifiable set of all the tags this component has.
     *
     * You should use [addTag] and [removeTag] to modify the tag set.
     */
    fun getTags() = Collections.unmodifiableSet<Any>(tagStorage)


    var animationTicks = 0
    private var guiTicksLastFrame = ClientTickHandler.ticks

    var enabled = true
        get() {
            return field
        }
        set(value) {
            if (field != value) {
                if (value)
                    BUS.fire(EnableEvent(thiz()))
                else
                    BUS.fire(DisableEvent(thiz()))
            }
            field = value
        }
    /**
     * Whether this component should be drawn or have events fire
     */
    var isVisible = true

    var focused = false
        set(value) {
            if (field != value) {
                if (value)
                    BUS.fire(FocusEvent(thiz()))
                else
                    BUS.fire(BlurEvent(thiz()))
            }
            field = value
        }
    /**
     * Returns true if this component is invalid and it should be removed from it's parent
     * @return
     */
    var isInvalid = false
        protected set
    var isAnimating = true

    protected var mouseButtonsDown = BooleanArray(EnumMouseButton.values().size)
    protected var keysDown: MutableMap<Key, Boolean> = HashMap<Key, Boolean>().withDefault({ k -> false })
    private val data: MutableMap<Class<*>, MutableMap<String, Any>> = mutableMapOf()

    var tooltipText: List<String>? = null
    var tooltipFont: FontRenderer? = null

    /**
     * Set whether the element should calculate hovering based on it's bounds as
     * well as it's children or if it should only calculate based on it's children.
     */
    var calculateOwnHover = true
    /**
     * True if the component shouldn't effect the logical size of it's parent. Causes logical size to return null.
     */
    var outOfFlow = false
    protected val components = mutableListOf<GuiComponent<*>>()
    val children: Collection<GuiComponent<*>> = Collections.unmodifiableCollection(components)
    var parent: GuiComponent<*>? = null
        private set(value) {
            parents.clear()
            if(value != null) {
                parents.addAll(value.parents)
                parents.add(value)
            }
            field = value
        }
    var parents: LinkedHashSet<GuiComponent<*>> = LinkedHashSet()

    /**
     * Amount to translate children, applied to both drawing and mouse position. Applied before [childScale]
     */
    var childTranslation: Vec2d = Vec2d.ZERO
    /**
     * Amount to scale children, applied to both drawing and mouse position. Applied after [childTranslation]
     */
    var childScale: Double = 1.0

    init {
        this.pos = vec(posX, posY)
        this.size = vec(width, height)
    }

    /**
     * Draws the component, this is called between pre and post draw events
     */
    abstract fun drawComponent(mousePos: Vec2d, partialTicks: Float)

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(vararg components: GuiComponent<*>?) {
        components.forEach { addInternal(it) }
    }

    protected fun addInternal(component: GuiComponent<*>?) {
        if (component == null) {
            LibrarianLog.error("Null component, ignoring")
            return
        }
        if (component === this)
            throw IllegalArgumentException("Immediately recursive component hierarchy")

        if (component.parent != null) {
            if (component.parent == this) {
                LibrarianLog.warn("You tried to add the component to the same parent twice. Why?")
                return
            } else {
                throw IllegalArgumentException("Component already had a parent")
            }
        }

        if (component in parents) {
            throw IllegalArgumentException("Recursive component hierarchy")
        }


            if (BUS.fire(AddChildEvent(thiz(), component)).isCanceled())
                return
        if (component.BUS.fire(AddToParentEvent(component.thiz(), thiz())).isCanceled())
            return
        components.add(component)
        component.parent = this
        components.sortBy { it.zIndex }
    }

    operator fun contains(component: GuiComponent<*>): Boolean {
        if (component in components)
            return true
        components.forEach { if (component in it) return true }
        return false
    }

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiComponent<*>) {
        if (component !in components)
            return
        if (BUS.fire(RemoveChildEvent(thiz(), component)).isCanceled())
            return
        if (component.BUS.fire(RemoveFromParentEvent(component.thiz(), thiz())).isCanceled())
            return
        component.parent = null
        components.remove(component)
    }

    /**
     * Removes all components that have the supplied tag
     */
    fun removeByTag(tag: Any) {
        components.removeAll { e ->
            var b = e.hasTag(tag)
            if (BUS.fire(RemoveChildEvent(thiz(), e)).isCanceled())
                b = false
            if (e.BUS.fire(RemoveFromParentEvent(e.thiz(), thiz())).isCanceled())
                b = false
            if (b) {
                e.parent = null
            }
            b
        }
    }

    /**
     * Returns a list of all children that have the tag [tag]
     */
    fun getByTag(tag: Any): List<GuiComponent<*>> {
        val list = mutableListOf<GuiComponent<*>>()
        addByTag(tag, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that have the tag [tag]
     */
    fun getAllByTag(tag: Any): List<GuiComponent<*>> {
        val list = mutableListOf<GuiComponent<*>>()
        addAllByTag(tag, list)
        return list
    }

    protected fun addAllByTag(tag: Any, list: MutableList<GuiComponent<*>>) {
        addByTag(tag, list)
        components.forEach { it.addAllByTag(tag, list) }
    }

    protected fun addByTag(tag: Any, list: MutableList<GuiComponent<*>>) {
        for (component in components) {
            if (component.hasTag(tag))
                list.add(component)
        }
    }

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    fun <C : GuiComponent<*>> getByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addByClass(clazz, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    fun <C : GuiComponent<*>> getAllByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addAllByClass(clazz, list)
        return list
    }

    protected fun <C : GuiComponent<*>> addAllByClass(clazz: Class<C>, list: MutableList<C>) {
        addByClass(clazz, list)
        components.forEach { it.addAllByClass(clazz, list) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <C : GuiComponent<*>> addByClass(clazz: Class<C>, list: MutableList<C>) {
        for (component in components) {
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
    }

    //=============================================================================
    /* Events/checks */
    //=============================================================================

    /**
     * Allows the component to modify the mouse position before it is passed to a child element.
     */
    fun transformChildPos(child: GuiComponent<*>, pos: Vec2d): Vec2d {
        //     [ translate to child's screen space ] [ subtract child pos to put origin at child origin ]
        return (pos - childTranslation) / childScale - child.pos
    }

    /**
     * Reverses [transformChildPos]
     */
    fun unTransformChildPos(child: GuiComponent<*>, pos: Vec2d): Vec2d {
        return (pos + child.pos) * childScale + childTranslation
    }

    /**
     * Recursivly reverses [transformChildPos], expressing the passed position relative to the root component.
     *
     * If [screenRoot] is set, then the root component's position is accounted for, making the position relative to the
     * GL context of the root caller. Generally meaning the pos is relative to the screen.
     */
    @JvmOverloads
    fun unTransformRoot(child: GuiComponent<*>, pos: Vec2d, screenRoot: Boolean = false): Vec2d {
        return parent?.unTransformRoot(this, unTransformChildPos(child, pos), screenRoot) ?: if(screenRoot) unTransformChildPos(child, pos) + this.pos else unTransformChildPos(child, pos)
    }

    open fun calculateMouseOver(mousePos: Vec2d) {
        this.mouseOver = false

        if (isVisible) {
            components.asReversed().forEach { child ->
                child.calculateMouseOver(transformChildPos(child, mousePos))
                if (mouseOver) {
                    child.mouseOver = false
                }
                if (child.mouseOver) {
                    mouseOver = true
                }

            }

            mouseOver = mouseOver || (calculateOwnHover &&
                    (mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y))
        }
        this.mouseOver = BUS.fire(MouseOverEvent(thiz(), mousePos, this.mouseOver)).isOver
    }

    private var wasMouseOver = false

    /**
     * Draw this component, don't override in subclasses unless you know what you're doing.
     * @param mousePos Mouse position relative to the position of this component
     * *
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        if (!isVisible) return

        if (isAnimating) {
            animationTicks += ClientTickHandler.ticks - guiTicksLastFrame
            guiTicksLastFrame = ClientTickHandler.ticks
        }

        components.removeAll { e ->
            var b = e.isInvalid
            if (BUS.fire(RemoveChildEvent(thiz(), e)).isCanceled())
                b = false
            if (e.BUS.fire(RemoveFromParentEvent(e.thiz(), thiz())).isCanceled())
                b = false
            if (b) {
                e.parent = null
            }
            b
        }

        if (wasMouseOver != this.mouseOver) {
            if (this.mouseOver) {
                BUS.fire(MouseInEvent(thiz(), mousePos))
            } else {
                BUS.fire(MouseOutEvent(thiz(), mousePos))
            }
        }
        wasMouseOver = this.mouseOver

        BUS.fire(PreDrawEvent(thiz(), mousePos, partialTicks))

        drawComponent(mousePos, partialTicks)

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.pushAttrib()
            GlStateManager.color(1f, 1f, 1f)
            if (!mouseOver) GlStateManager.color(1f, 0f, 1f)
            GlStateManager.disableTexture2D()
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            vb.pos(pos.x, pos.y, 0.0).endVertex()
            vb.pos(pos.x + size.x, pos.y, 0.0).endVertex()
            vb.pos(pos.x + size.x, pos.y + size.y, 0.0).endVertex()
            vb.pos(pos.x, pos.y + size.y, 0.0).endVertex()
            vb.pos(pos.x, pos.y, 0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.popAttrib()
        }

        GlStateManager.pushMatrix()
        GlStateManager.pushAttrib()
        GlStateManager.translate(pos.x + childTranslation.x, pos.y + childTranslation.y, 0.0)
        if(childScale != 1.0) // avoid unnecessary GL calls. Possibly microoptimization but meh.
            GlStateManager.scale(childScale, childScale, 1.0)

        BUS.fire(PreChildrenDrawEvent(thiz(), mousePos, partialTicks))

        for (component in components) {
            component.draw(transformChildPos(component, mousePos), partialTicks)
        }

        GlStateManager.popAttrib()
        GlStateManager.popMatrix()

        BUS.fire(PostDrawEvent(thiz(), mousePos, partialTicks))
    }

    open fun onTick() {}

    fun tick() {
        BUS.fire(ComponentTickEvent(thiz()))
        onTick()
        for (child in components) {
            child.tick()
        }
    }

    /**
     * Called when the mouse is pressed. mousePos is relative to the position of this component.
     * @param mousePos
     * *
     * @param button
     */
    open fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        if (!isVisible) return
        if (BUS.fire(MouseDownEvent(thiz(), mousePos, button)).isCanceled())
            return

        if (mouseOver)
            mouseButtonsDown[button.ordinal] = true

        for (child in components) {
            child.mouseDown(transformChildPos(child, mousePos), button)
        }
    }

    /**
     * Called when the mouse is released. mousePos is relative to the position of this component.
     * @param mousePos
     * *
     * @param button
     */
    fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        if (!isVisible) return
        val wasDown = mouseButtonsDown[button.ordinal]
        mouseButtonsDown[button.ordinal] = false

        if (BUS.fire(MouseUpEvent(thiz(), mousePos, button)).isCanceled())
            return

        if (mouseOver && wasDown) {
            BUS.fire(MouseClickEvent(thiz(), mousePos, button))
            // don't return here, if a click was handled we should still handle the mouseUp
        }

        for (child in components) {
            child.mouseUp(transformChildPos(child, mousePos), button)
        }
    }

    /**
     * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
     * @param mousePos
     * *
     * @param button
     */
    fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton) {
        if (!isVisible) return
        if (BUS.fire(MouseDragEvent(thiz(), mousePos, button)).isCanceled())
            return

        for (child in components) {
            child.mouseDrag(transformChildPos(child, mousePos), button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     * @param mousePos
     */
    fun mouseWheel(mousePos: Vec2d, direction: MouseWheelDirection) {
        if (!isVisible) return
        if (BUS.fire(MouseWheelEvent(thiz(), mousePos, direction)).isCanceled())
            return

        for (child in components) {
            child.mouseWheel(transformChildPos(child, mousePos), direction)
        }
    }

    /**
     * Called when a key is pressed in the parent component.
     * @param key The actual character that was pressed
     * *
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyPressed(key: Char, keyCode: Int) {
        if (!isVisible) return
        if (BUS.fire(KeyDownEvent(thiz(), key, keyCode)).isCanceled())
            return

        keysDown.put(Key.get(key, keyCode), true)

        for (child in components) {
            child.keyPressed(key, keyCode)
        }
    }

    /**
     * Called when a key is released in the parent component.
     * @param key The actual key that was pressed
     * *
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyReleased(key: Char, keyCode: Int) {
        if (!isVisible) return
        keysDown.put(Key.get(key, keyCode), false) // do this before so we don't have lingering keyDown entries

        if (BUS.fire(KeyUpEvent(thiz(), key, keyCode)).isCanceled())
            return

        for (child in components) {
            child.keyReleased(key, keyCode)
        }
    }

    /**
     * Returns `this` casted to `T`. Used to avoid unchecked cast warnings everywhere.
     */
    @Suppress("UNCHECKED_CAST")
    fun thiz(): T {
        return this as T
    }

    /**
     * The size of the component for layout. Often dynamically calculated
     */
    fun getLogicalSize(): BoundingBox2D? {
        var aabb = contentSize
        for (child in components) {
            if (!child.isVisible) continue
            val childAABB = child.getLogicalSize()?.scale(childScale)?.offset(childTranslation)
            aabb = childAABB?.union(aabb) ?: aabb
        }

        aabb = BoundingBox2D(aabb.min + pos - vec(marginLeft, marginTop), aabb.max + pos + vec(marginRight, marginBottom))

        return BUS.fire(LogicalSizeEvent(thiz(), if (outOfFlow) null else aabb)).box
    }

    /**
     * Gets the size of the content of this component.
     */
    protected open val contentSize: BoundingBox2D
        get() {
            return BoundingBox2D(Vec2d.ZERO, size)
        }

    /**
     * Gets the root component
     */
    val root: GuiComponent<*>
        get() {
            return parent?.root ?: this
        }

    /**
     * Sets the tooltip to be drawn, overriding the existing value. Pass null for the font to use the default font renderer.
     */
    fun setTooltip(text: List<String>, font: FontRenderer) {
        val component = root
        component.tooltipText = text
        component.tooltipFont = font
    }

    /**
     * Sets the tooltip to be drawn, overriding the existing value and using the default font renderer.
     */
    fun setTooltip(text: List<String>) {
        val component = root
        component.tooltipText = text
        component.tooltipFont = null
    }

    //=============================================================================
    init {/* Assorted info */
    }
    //=============================================================================

    open fun onMessage(from: GuiComponent<*>, message: Message) {}

    fun handleMessage(from: GuiComponent<*>, message: Message) {
        BUS.fire(MessageArriveEvent(thiz(), from, message))
        onMessage(from, message)

        if(message.rippleType != EnumRippleType.NONE) {
            if(message.rippleType == EnumRippleType.UP || message.rippleType == EnumRippleType.ALL) {
                parent?.let {
                    if(it != from) {
                        it.handleMessage(this, message)
                    }
                }
            }
            if(message.rippleType == EnumRippleType.DOWN || message.rippleType == EnumRippleType.ALL) {
                children.forEach {
                    if(it != from) {
                        it.handleMessage(this, message)
                    }
                }
            }
        }
    }

    fun sendMessage(data: Any, ripple: EnumRippleType) {

    }

    /**
     * Sets the value associated with the pair of keys [clazz] and [key]. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, key: String, value: D) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(SetDataEvent(thiz(), clazz, key, value)).isCanceled())
            data.get(clazz)?.put(key, value)
    }

    /**
     * Removes the value associated with the pair of keys [clazz] and [key]
     */
    fun <D : Any> removeData(clazz: Class<D>, key: String) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(RemoveDataEvent(thiz(), clazz, key, getData(clazz, key))).isCanceled())
            data.get(clazz)?.remove(key)
    }

    /**
     * Returns the value associated with the pair of keys [clazz] and [key] if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    @Suppress("UNCHECKED_CAST")
    fun <D> getData(clazz: Class<D>, key: String): D? {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GetDataEvent(thiz(), clazz, key, data.get(clazz)?.get(key) as D?)).value
    }

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and [key]
     */
    @Suppress("UNCHECKED_CAST")
    fun <D> hasData(clazz: Class<D>, key: String): Boolean {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GetDataEvent(thiz(), clazz, key, data[clazz]?.get(key) as D?)).value != null
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

    /**
     * Adds the passed tag to this component if it doesn't already have it. Tags are not case sensitive

     * @return true if the tag didn't exist and was added
     */
    fun addTag(tag: Any): Boolean {
        if (!BUS.fire(AddTagEvent(thiz(), tag)).isCanceled())
            if (tagStorage.add(tag))
                return true
        return false
    }

    /**
     * Removes the passed tag to this component if it doesn't already have it. Tags are not case sensitive

     * @return true if the tag existed and was removed
     */
    fun removeTag(tag: Any): Boolean {
        if (!BUS.fire(RemoveTagEvent(thiz(), tag)).isCanceled())
            if (tagStorage.remove(tag))
                return true
        return false
    }

    /**
     * Checks if the component has the tag specified. Tags are not case sensitive
     */
    fun hasTag(tag: Any): Boolean {
        return BUS.fire(HasTagEvent(thiz(), tag, tagStorage.contains(tag))).hasTag
    }

    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    fun invalidate() {
        this.isInvalid = true
    }

    fun resetAnimation() {
        animationTicks = 0
    }
}
