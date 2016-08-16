package com.teamwizardry.librarianlib.gui

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.util.event.Event
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import java.util.*

/**
 * A component of a capability, such as a button, image, piece of text, list, etc.

 * @param  The class of this component. Used for setup()
 */
abstract class GuiComponent<T : GuiComponent<T>> @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0) : IGuiDrawable {

    val preDraw = Event<(T, mousePos: Vec2d, partialTicks: Float) -> Unit>()
    val postDraw = HandlerList<(T, mousePos: Vec2d, partialTicks: Float) -> Unit>()
    val preChildrenDraw = HandlerList<(T, mousePos: Vec2d, partialTicks: Float) -> Unit>()

    val mouseDown = HandlerList<(T, mousePos: Vec2d, button: EnumMouseButton) -> Boolean>()
    val mouseUp = HandlerList<(T, mousePos: Vec2d, button: EnumMouseButton) -> Boolean>()
    val mouseDrag = HandlerList<(T, mousePos: Vec2d, button: EnumMouseButton) -> Boolean>()
    val mouseClick = HandlerList<(T, mousePos: Vec2d, button: EnumMouseButton) -> Boolean>()

    val keyDown = HandlerList<(T, character: Char, keyCode: Int) -> Boolean>()
    val keyUp = HandlerList<(T, character: Char, keyCode: Int) -> Boolean>()

    val mouseIn = HandlerList<(T, mousePos: Vec2d) -> Boolean>()
    val mouseOut = HandlerList<(T, mousePos: Vec2d) -> Boolean>()
    val mouseWheel = HandlerList<(T, mousePos: Vec2d, direction: Int) -> Boolean>()

    val focus = HandlerList<(T) -> Unit>()
    val blur = HandlerList<(T) -> Unit>()

    val enable = HandlerList<(T) -> Unit>()
    val disable = HandlerList<(T) -> Unit>()

    val addChild = HandlerList<(T, child: GuiComponent<*>) -> Boolean>()
    val removeChild = HandlerList<(T, child: GuiComponent<*>) -> Boolean>()
    val matchesTag = HandlerList<(T, tag: Any) -> Boolean>()
    val addTag = HandlerList<(T, tag: Any) -> Boolean>()
    val removeTag = HandlerList<(T, tag: Any) -> Boolean>()

    val logicalSize = HandlerList<(T, box: BoundingBox2D?) -> BoundingBox2D?>()
    val transformChildPos = HandlerList<(T, child: GuiComponent<*>, childPos: Vec2d?) -> Vec2d?>()

    var zIndex = 0
    /**
     * Get the position of the component relative to it's parent
     */
    /**
     * Set the position of the component relative to it's parent
     */
    var pos: Vec2d
    /**
     * Get the size of the component
     */
    /**
     * Set the size of the component
     */
    var size: Vec2d

    var mouseOverThisFrame = false
    var mousePosThisFrame = Vec2d.ZERO
    var tags: MutableSet<Any> = HashSet<Any>()
        get() {
            return Collections.unmodifiableSet<Any>(field)
        }
        protected set

    var animationTicks = 0
    private var guiTicksLastFrame = TickCounter.ticks

    public var enabled = true
        get() {
            return field
        }
        set(value) {
            if (field != value) {
                if (value)
                    enable.fireAll({ h -> h(thiz()) })
                else
                    disable.fireAll({ h -> h(thiz()) })
            }
            field = value
        }
    /**
     * Whether this component should be drawn or have events fire
     */
    var isVisible = true

    var focused = false
        get() {
            return field
        }
        set(value) {
            if (field != value) {
                if (value)
                    focus.fireAll({ h -> h(thiz()) })
                else
                    blur.fireAll({ h -> h(thiz()) })
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
    protected var keysDown: MutableMap<Key, Boolean> = HashMap<Key, Boolean>().withDefault({k -> false})
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
    protected var components: MutableList<GuiComponent<*>> = ArrayList()
    var parent: GuiComponent<*>? = null
        protected set

    init {
        this.pos = Vec2d(posX.toDouble(), posY.toDouble())
        this.size = Vec2d(width.toDouble(), height.toDouble())
    }

    /**
     * Draws the component, this is called between pre and post draw events
     */
    abstract fun drawComponent(mousePos: Vec2d, partialTicks: Float)

    /**
     * Setup the component without making temporary variables
     */
    fun setup(setup: (T) -> Unit): T {
        setup(thiz())
        return thiz()
    }

    /**
     * Transforms the position passed to be relative to this component's position.
     */
    open fun relativePos(pos: Vec2d): Vec2d {
        return pos.sub(this.pos)
    }

    /**
     * Transforms the position passed to be relative to the root component's position.
     */
    fun rootPos(pos: Vec2d): Vec2d {
        if (parent == null)
            return pos.add(this.pos)
        else
            return parent!!.rootPos(pos.add(this.pos))
    }

    /**
     * Adds a child to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(component: GuiComponent<*>?) {
        if (component == null) {
            LibrarianLog.warn("You shouldn't be addinng null components!")
            return
        }
        if (component === this)
            throw IllegalArgumentException("Can't add components recursivly!")
        if (component.parent != null)
            throw IllegalArgumentException("Component already had a parent!")

        components.add(component)
        component.parent = this
        Collections.sort<GuiComponent<*>>(components, { a, b -> Integer.compare(a.zIndex, b.zIndex) })
        addChild.fireAll({ h -> h(thiz(), component) })
    }

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiComponent<*>) {
        component.parent = null
        removeChild.fireAll({ h -> h(thiz(), component) })
        components.remove(component)
    }

    /**
     * Removes all components that have the supplied tag
     */
    fun removeByTag(tag: Any) {
        components.removeAll { e ->
            val b = e.hasTag(tag)
            if (b) {
                e.parent = null
                removeChild.fireAll({ h -> h(thiz(), e) })
            }
            b
        }
    }

    /**
     * Returns all the elements with a given tag
     */
    fun getByTag(tag: Any): List<GuiComponent<*>> {
        val list = ArrayList<GuiComponent<*>>()
        for (component in components) {
            if (component.hasTag(tag))
                list.add(component)
        }
        return list
    }

    /**
     * Returns all the elements that can be cast to the specified class
     */
    fun <C> getByClass(klass: Class<C>): List<C> {
        val list = ArrayList<C>()
        for (component in components) {
            if (klass.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
        return list
    }

    //=============================================================================
    init {/* Events/checks */
    }
    //=============================================================================

    /**
     * Allows the component to modify the position before it is passed to a child element.
     */
    open fun transformChildPos(child: GuiComponent<*>, pos: Vec2d): Vec2d {
        var pos = pos
        pos = child.relativePos(pos)
        return transformChildPos.fireModifier<Vec2d>(pos, { h, v -> h(thiz(), child, v) }) ?: pos
    }

    /**
     * Test if the mouse is over this component. mousePos is relative to the position of the element.
     */
    fun isMouseOver(mousePos: Vec2d): Boolean {
        if (!isVisible) return false
        var hover = false
        if (calculateOwnHover)
            hover = hover || mousePos.x >= 0 && mousePos.x < size.x && mousePos.y >= 0 && mousePos.y < size.y

        for (child in components) {
            hover = child.isMouseOver(transformChildPos(child, mousePos)) || hover
        }
        return hover
    }

    /**
     * Draw this component, don't override in subclasses unless you know what you're doing.
     * @param mousePos Mouse position relative to the position of this component
     * *
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    public override fun draw(mousePos: Vec2d, partialTicks: Float) {
        if (!isVisible) return

        if (isAnimating) {
            animationTicks += TickCounter.ticks - guiTicksLastFrame
            guiTicksLastFrame = TickCounter.ticks
        }

        val wasMouseOverLastFrame = mouseOverThisFrame
        mouseOverThisFrame = isMouseOver(mousePos)
        mousePosThisFrame = mousePos

        if (wasMouseOverLastFrame && !mouseOverThisFrame)
            mouseOut.fireAll({ e -> e(thiz(), mousePos) })
        if (!wasMouseOverLastFrame && mouseOverThisFrame)
            mouseIn.fireAll({ e -> e(thiz(), mousePos) })

        preDraw.fireAll({ e -> e(thiz(), mousePos, partialTicks) })

        drawComponent(mousePos, partialTicks)

        GlStateManager.pushMatrix()
        GlStateManager.pushAttrib()
        GlStateManager.translate(pos.x, pos.y, 0.0)

        val remove = ArrayList<GuiComponent<*>>()
        for (component in components) {
            if (component.isInvalid) {
                remove.add(component)
                continue
            }
            component.draw(transformChildPos(component, mousePos), partialTicks)
        }

        preChildrenDraw.fireAll({ e -> e(thiz(), mousePos, partialTicks) })

        for (rem in remove) {
            rem.parent = null
            removeChild.fireAll({ h -> h(thiz(), rem) })
        }
        components.removeAll(remove)

        GlStateManager.popAttrib()
        GlStateManager.popMatrix()

        postDraw.fireAll({ e -> e(thiz(), mousePos, partialTicks) })
    }

    /**
     * Called when the mouse is pressed. mousePos is relative to the position of this component.
     * @param mousePos
     * *
     * @param button
     */
    open fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        if (!isVisible) return
        if (mouseDown.fireCancel({ e -> e(thiz(), mousePos, button) }))
            return

        if (isMouseOver(mousePos))
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
    open fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        if (!isVisible) return
        val wasDown = mouseButtonsDown[button.ordinal]
        mouseButtonsDown[button.ordinal] = false

        if (mouseUp.fireCancel({ e -> e(thiz(), mousePos, button) }))
            return

        if (isMouseOver(mousePos) && wasDown) {
            mouseClick.fireCancel({ e -> e(thiz(), mousePos, button) }) // don't return here, if a click was handled we should still handle the mouseUp
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
        if (mouseDrag.fireCancel({ e -> e(thiz(), mousePos, button) }))
            return

        for (child in components) {
            child.mouseDrag(transformChildPos(child, mousePos), button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     * @param mousePos
     */
    open fun mouseWheel(mousePos: Vec2d, direction: Int) {
        if (!isVisible) return
        if (mouseWheel.fireCancel({ e -> e(thiz(), mousePos, direction) }))
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
        if (keyDown.fireCancel({ e -> e(thiz(), key, keyCode) }))
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

        if (keyUp.fireCancel({ e -> e(thiz(), key, keyCode) }))
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
    open fun getLogicalSize(): BoundingBox2D? {
        var aabb = contentSize

        for (child in components) {
            if (!child.isVisible) continue
            val childAABB = child.getLogicalSize()
            aabb = childAABB?.union(aabb) ?: aabb
        }

        aabb = BoundingBox2D(aabb.min.add(pos), aabb.max.add(pos))

        return logicalSize.fireModifier<BoundingBox2D>(if(outOfFlow) null else aabb, { t, v -> t(thiz(), v) })
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
            if (parent != null)
                return parent!!.root
            return this
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

    fun <D: Any> setData(klass: Class<D>, key: String, value: D) {
        if (!data.containsKey(klass))
            data.put(klass, mutableMapOf())
        data.get(klass)?.put(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <D> getData(klass: Class<D>, key: String): D {
        if (!data.containsKey(klass))
            data.put(klass, HashMap<String, Any>())
        return data.get(klass)?.get(key) as D
    }

    /**
     * Adds the passed tag to this component if it doesn't already have it. Tags are not case sensitive

     * @return true if the tag didn't exist and was added
     */
    fun addTag(tag: Any): Boolean {
        if (tags.add(tag)) {
            addTag.fireAll({ h -> h(thiz(), tag) })
            return true
        }
        return false
    }

    /**
     * Removes the passed tag to this component if it doesn't already have it. Tags are not case sensitive

     * @return true if the tag existed and was removed
     */
    fun removeTag(tag: Any): Boolean {
        if (tags.remove(tag)) {
            removeTag.fireAll({ h -> h(thiz(), tag) })
            return true
        }
        return false
    }

    /**
     * Checks if the component has the tag specified. Tags are not case sensitive
     */
    fun hasTag(tag: Any): Boolean {
        return tags.contains(tag)
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
