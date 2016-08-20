package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.core.ClientTickHandler
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.event.EventBus
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.BoundingBox2D
import com.teamwizardry.librarianlib.common.util.math.Vec2d
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
 * A component of a capability, such as a button, image, piece of text, list, etc.

 * @param  The class of this component. Used for setup()
 */
@SideOnly(Side.CLIENT)
abstract class GuiComponent<T : GuiComponent<T>> @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0) : IGuiDrawable {

    @JvmField
    val BUS = EventBus()

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
    class ChildMouseOffsetEvent<T : GuiComponent<T>>(val component: T, val child: GuiComponent<*>, var offset: Vec2d) : Event()
    class MouseOffsetEvent<out T : GuiComponent<*>>(val component: T, var offset: Vec2d) : Event()
    class MouseOverEvent<T : GuiComponent<T>>(val component: T, val mousePos: Vec2d, var isOver: Boolean) : Event()

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

    var mouseOver = false
    var mousePosThisFrame = Vec2d.ZERO
    protected var tagStorage: MutableSet<Any> = HashSet<Any>()
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
    protected var components: MutableList<GuiComponent<*>> = ArrayList()
    var parent: GuiComponent<*>? = null
        private set

    init {
        this.pos = Vec2d(posX.toDouble(), posY.toDouble())
        this.size = Vec2d(width.toDouble(), height.toDouble())
    }

    /**
     * Draws the component, this is called between pre and post draw events
     */
    abstract fun drawComponent(mousePos: Vec2d, partialTicks: Float)

    /**
     * Transforms the position passed to be relative to the root component's position.
     */
    fun rootPos(pos: Vec2d): Vec2d {
        return parent?.rootPos(pos.add(this.pos)) ?: pos.add(this.pos)
    }

    /**
     * Adds a child to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(component: GuiComponent<*>?) {
        if (component == null) {
            LibrarianLog.error("You shouldn't be adding null components!")
            return
        }
        if (component === this)
            throw IllegalArgumentException("Can't add components to themselves!")

        if (component.parent != null)
            throw IllegalArgumentException("Component already had a parent!")
        if (BUS.fire(AddChildEvent(thiz(), component)).isCanceled())
            return
        if (component.BUS.fire(AddToParentEvent(component.thiz(), thiz())).isCanceled())
            return
        components.add(component)
        component.parent = this
        Collections.sort<GuiComponent<*>>(components, { a, b -> Integer.compare(a.zIndex, b.zIndex) })
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
    @Suppress("UNCHECKED_CAST")
    fun <C> getByClass(clazz: Class<C>): List<C> {
        val list = ArrayList<C>()
        for (component in components) {
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
        return list
    }

    //=============================================================================
    /* Events/checks */
    //=============================================================================

    /**
     * Allows the component to modify the position before it is passed to a child element.
     */
    fun transformChildPos(child: GuiComponent<*>, pos: Vec2d): Vec2d {
        return pos.sub(child.BUS.fire(MouseOffsetEvent(child.thiz(), BUS.fire(ChildMouseOffsetEvent(thiz(), child, child.pos)).offset)).offset)
    }

    open fun calculateMouseOver(mousePos: Vec2d) {
        this.mouseOver = false

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
        GlStateManager.color(1f, 1f, 1f)

        GlStateManager.pushMatrix()
        GlStateManager.pushAttrib()
        GlStateManager.translate(pos.x, pos.y, 0.0)

        BUS.fire(PreChildrenDrawEvent(thiz(), mousePos, partialTicks))

        for (component in components) {
            component.draw(transformChildPos(component, mousePos), partialTicks)
        }

        GlStateManager.popAttrib()
        GlStateManager.popMatrix()

        BUS.fire(PostDrawEvent(thiz(), mousePos, partialTicks))
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
            val childAABB = child.getLogicalSize()
            aabb = childAABB?.union(aabb) ?: aabb
        }

        aabb = BoundingBox2D(aabb.min.add(pos), aabb.max.add(pos))

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

    fun <D : Any> setData(clazz: Class<D>, key: String, value: D) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(SetDataEvent(thiz(), clazz, key, value)).isCanceled())
            data.get(clazz)?.put(key, value)
    }

    fun <D : Any> removeData(clazz: Class<D>, key: String) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(RemoveDataEvent(thiz(), clazz, key, getData(clazz, key))).isCanceled())
            data.get(clazz)?.remove(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <D> getData(clazz: Class<D>, key: String): D? {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GetDataEvent(thiz(), clazz, key, data.get(clazz)?.get(key) as D?)).value
    }

    @Suppress("UNCHECKED_CAST")
    fun <D> hasData(clazz: Class<D>, key: String): Boolean {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GetDataEvent(thiz(), clazz, key, data[clazz]?.get(key) as D?)).value != null
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
