package com.teamwizardry.librarianlib.facade.layer

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IRenderTypeState
import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.core.util.*
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.mixinCast
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.core.util.lerp.Lerpers
import com.teamwizardry.librarianlib.facade.layer.supporting.StencilUtil
import com.teamwizardry.librarianlib.facade.layer.supporting.*
import com.teamwizardry.librarianlib.facade.logger
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.value.IMValueBoolean
import com.teamwizardry.librarianlib.facade.value.RMValue
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Matrix3dView
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.fastCos
import com.teamwizardry.librarianlib.math.fastSin
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.EventBus
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.FacadeWidget
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.pastry.components.PastryBasicTooltip
import com.teamwizardry.librarianlib.facade.value.ChangeListener
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.facade.value.IMValueInt
import com.teamwizardry.librarianlib.facade.value.IMValueLong
import com.teamwizardry.librarianlib.facade.value.RMValueBoolean
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.facade.value.RMValueLong
import com.teamwizardry.librarianlib.mosaic.ISprite
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.util.yoga.Yoga.*
import java.awt.Color
import java.lang.Exception
import java.util.ConcurrentModificationException
import java.util.PriorityQueue
import java.util.function.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

/**
 * The fundamental building block of a LibrarianLib GUI. Generally a single unit of visual or organizational design.
 *
 * **Origins:**
 *
 * Vanilla GUIs very basic, where each frame you personally draw each texture and have to manually handle positioning
 * as the layout changes (though most likely it won't, because the math quickly becomes a pain). Vanilla does have the
 * [Button][net.minecraft.client.gui.widget.button.Button], which isn't processed by you, however LibrarianLib's
 * layers blow GuiButton clean out of the water with their versatility and ability to easily create highly complex and
 * dynamic interfaces.
 *
 * Over its evolution the GUI framework has been influenced largely by two things. It started out mostly inspired by
 * HTML's hierarchical nature, then later in its life it started to acquire many of the traits and structures from
 * Cocoa Touch. If you are familiar Cocoa Touch, many these concepts will be familiar to you.
 *
 * **Usage:**
 *
 * Layers are structured hierarchically, with each layer's children positioned in the layer's coordinate space.
 * Their relative transforms (position, rotation, and scale) can be accessed and modified using [pos], [rotation], and [scale].
 * Each layer implements [CoordinateSpace2D] and has methods to convert points between its local coordinate system and
 * another layer's local coordinate system.
 *
 * Each layer has its own event bus, which is used to hook into the many events fired by the GuiLayer/GuiComponent
 * itself or by one of its subclasses.
 *
 * Layers that have custom rendering (as opposed to consisting solely of other layers) can override the [draw] method
 * to draw their content.
 */
public open class GuiLayer(posX: Int, posY: Int, width: Int, height: Int): CoordinateSpace2D {
    public constructor(posX: Int, posY: Int): this(posX, posY, 0, 0)
    public constructor(): this(0, 0, 0, 0)

    /**
     * Called every frame after input events and before the layout pass
     */
    public open fun update() {
    }

    /**
     * Called after updates and before layouts are updated. This is where layouts should be modified based on changes to
     * data that may have occurred during the update phase
     */
    public open fun prepareLayout() {}

    /**
     * Called to lay out the children of this layer.
     *
     * This method is called before each frame if this layer's bounds have changed, children have been added/removed,
     * a child's frame has changed, or [markLayoutDirty] has been called on this layer.
     *
     * After this method completes, the children of this component will be checked for layout. This means that changes
     * made in one layer can ripple downward, but also that children can override the layout of their parent.
     * [isLayoutDirty] is reset to false after this layer and its children are laid out, so any changes while laying out
     * will not cause the layout to be recalculated on the next frame.
     *
     * The idea behind this method is that self-contained components/layers can lay out their children dynamically
     * themselves. Examples of such a component would be a self-contained list item, a component that spaces out its
     * children equally along its length, or simply a component that needs to resize its background layer
     * to fit its dimensions.
     *
     * This is called *after* Yoga layout has been applied.
     */
    public open fun layoutChildren() {}

    /**
     * Draws the layer's contents. This is the method to override when creating custom layer rendering.
     *
     * Unless necessary, layers shouldn't change any global GL state. However, as a precaution, there are a number of
     * state guarantees made before every call to [draw]. The authoritative list is provided as a "sample" in these
     * docs.
     *
     * The passed context's matrix can be modified. Any changes will be popped at the end. However, if any more matrices
     * are pushed in the draw method, they *must* be popped by the end.
     *
     * @sample glStateGuarantees
     */
    public open fun draw(context: GuiDrawContext) {}

    //region Animation
    private val animationTimeListeners = weakSetOf<AnimationTimeListener>()

    public fun <T: AnimationTimeListener> addAnimationTimeListener(listener: T): T {
        animationTimeListeners.add(listener)
        listener.updateTime(animationTime)
        return listener
    }

    public fun removeAnimationTimeListener(listener: AnimationTimeListener) {
        animationTimeListeners.remove(listener)
    }

    /**
     * The current time for animations, expressed in ticks since this layer was first added to the layer hierarchy.
     */
    public var animationTime: Float = 0f
        private set
    private var baseTime: Float = Float.NaN

    @JvmSynthetic
    internal fun updateAnimations(time: Float) {
        if (baseTime.isNaN())
            baseTime = floor(time)
        animationTime = time - baseTime

        while (scheduledEvents.isNotEmpty() && scheduledEvents.peek().time <= animationTime) {
            val event = scheduledEvents.poll()
            event.callback.run()
            if (event.interval > 0) {
                event.time += event.interval
                scheduledEvents.add(event)
            }
        }

        animationTimeListeners.forEach {
            it.updateTime(animationTime)
        }
        forEachChild { it.updateAnimations(time) }
    }

    private val scheduledEvents = PriorityQueue<ScheduledEvent>()

    /**
     * Run the specified callback once [animationTime] is >= [time], repeating with the passed [interval].
     */
    public fun schedule(time: Float, interval: Float, callback: Runnable) {
        scheduledEvents.add(ScheduledEvent(time, interval, callback))
    }

    /**
     * Run the specified callback once [animationTime] is >= [time]
     */
    public fun schedule(time: Float, callback: Runnable) {
        scheduledEvents.add(ScheduledEvent(time, 0f, callback))
    }

    /**
     * Run the specified callback after [time] ticks, repeating with the passed [interval].
     */
    public fun delay(time: Float, interval: Float, callback: Runnable) {
        schedule(animationTime + time, interval, callback)
    }

    /**
     * Run the specified callback after [time] ticks.
     */
    public fun delay(time: Float, callback: Runnable) {
        schedule(animationTime + time, callback)
    }

    private data class ScheduledEvent(var time: Float, val interval: Float, val callback: Runnable): Comparable<ScheduledEvent> {
        override fun compareTo(other: ScheduledEvent): Int {
            return this.time.compareTo(other.time)
        }
    }
    //endregion

    //region GuiValue
    //region RMValue

    /**
     * Creates an RMValue and registers it for animation updates. Automatically detects the lerper based on the reified
     * type.
     */
    @JvmSynthetic
    public inline fun <reified T> rmValue(initialValue: T): RMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T>?
        return rmValue(initialValue, lerper, null)
    }

    /**
     * Creates an RMValue and registers it for animation updates. Automatically detects the lerper based on the passed
     * type.
     */
    public fun <T> rmValue(initialValue: T, type: Class<T>): RMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect(type))?.value as Lerper<T>?
        return rmValue(initialValue, lerper, null)
    }

    /**
     * Creates an RMValue and registers it for animation updates.
     */
    public fun <T> rmValue(initialValue: T, lerper: Lerper<T>): RMValue<T> = rmValue(initialValue, lerper, null)

    /**
     * Creates an RMValue and registers it for animation updates. Automatically detects the lerper based on the reified
     * type.
     */
    @JvmSynthetic
    public inline fun <reified T> rmValue(initialValue: T, change: ChangeListener<T>): RMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T>?
        return rmValue(initialValue, lerper, change)
    }

    /**
     * Creates an RMValue and registers it for animation updates. Automatically detects the lerper based on the passed
     * type.
     */
    public fun <T> rmValue(initialValue: T, change: ChangeListener<T>, type: Class<T>): RMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect(type))?.value as Lerper<T>?
        return rmValue(initialValue, lerper, change)
    }

    /**
     * Creates an RMValue and registers it for animation updates.
     */
    public fun <T> rmValue(initialValue: T, lerper: Lerper<T>?, change: ChangeListener<T>?): RMValue<T> {
        return addAnimationTimeListener(RMValue(initialValue, lerper, change))
    }

    /**
     * Creates an RMValueBoolean and registers it for animation updates.
     */
    @JvmOverloads
    public fun rmBoolean(initialValue: Boolean, change: ChangeListener.Boolean? = null): RMValueBoolean {
        return addAnimationTimeListener(RMValueBoolean(initialValue, change))
    }

    /**
     * Creates an RMValueDouble and registers it for animation updates.
     */
    @JvmOverloads
    public fun rmDouble(initialValue: Double, change: ChangeListener.Double? = null): RMValueDouble {
        return addAnimationTimeListener(RMValueDouble(initialValue, change))
    }

    /**
     * Creates an RMValueInt and registers it for animation updates.
     */
    @JvmOverloads
    public fun rmInt(initialValue: Int, change: ChangeListener.Int? = null): RMValueInt {
        return addAnimationTimeListener(RMValueInt(initialValue, change))
    }

    /**
     * Creates an RMValueLong and registers it for animation updates.
     */
    @JvmOverloads
    public fun rmLong(initialValue: Long, change: ChangeListener.Long? = null): RMValueLong {
        return addAnimationTimeListener(RMValueLong(initialValue, change))
    }
    //endregion

    //region IMValue
    /**
     * Create an IMValue, automatically detecting lerpers if they exist and registering it for animation updates.
     */
    public inline fun <reified T> imValue(callback: Supplier<T>): IMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T>?
        return addAnimationTimeListener(IMValue(callback, lerper))
    }

    /**
     * Create an IMValue, automatically detecting lerpers if they exist and registering it for animation updates.
     */
    public fun <T> imValue(callback: Supplier<T>, type: Class<T>): IMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect(type))?.value as Lerper<T>?
        return addAnimationTimeListener(IMValue(callback, lerper))
    }

    /**
     * Create an IMValue, automatically detecting lerpers if they exist and registering it for animation updates.
     */
    @JvmSynthetic
    public inline fun <reified T> imValue(initialValue: T): IMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T>?
        return addAnimationTimeListener(IMValue(initialValue, lerper))
    }

    /**
     * Create an IMValue, automatically detecting lerpers if they exist and registering it for animation updates.
     */
    public fun <T> imValue(initialValue: T, type: Class<T>): IMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect(type))?.value as Lerper<T>?
        return addAnimationTimeListener(IMValue(initialValue, lerper))
    }

    /**
     * Create an IMValue with a null initial value, automatically detecting lerpers if they exist and registering it for
     * animation updates. This is needed because otherwise there are [resolution errors](https://youtrack.jetbrains.com/issue/KT-13683)
     * when not using the experimental type inference.
     */
    @JvmSynthetic
    public inline fun <reified T> imValue(): IMValue<T?> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T?>?
        return addAnimationTimeListener(IMValue(null, lerper))
    }

    /**
     * Creates an IMValueBoolean and registers it for animation updates.
     */
    public fun imBoolean(initialValue: Boolean): IMValueBoolean {
        return addAnimationTimeListener(IMValueBoolean(initialValue))
    }

    /**
     * Creates an RMValueBoolean and registers it for animation updates.
     */
    public fun imBoolean(callback: BooleanSupplier): IMValueBoolean {
        return addAnimationTimeListener(IMValueBoolean(callback))
    }

    /**
     * Creates an IMValueDouble and registers it for animation updates.
     */
    public fun imDouble(initialValue: Double): IMValueDouble {
        return addAnimationTimeListener(IMValueDouble(initialValue))
    }

    /**
     * Creates an RMValueDouble and registers it for animation updates.
     */
    public fun imDouble(callback: DoubleSupplier): IMValueDouble {
        return addAnimationTimeListener(IMValueDouble(callback))
    }

    /**
     * Creates an IMValueInt and registers it for animation updates.
     */
    public fun imInt(initialValue: Int): IMValueInt {
        return addAnimationTimeListener(IMValueInt(initialValue))
    }

    /**
     * Creates an RMValueInt and registers it for animation updates.
     */
    public fun imInt(callback: IntSupplier): IMValueInt {
        return addAnimationTimeListener(IMValueInt(callback))
    }

    /**
     * Creates an IMValueLong and registers it for animation updates.
     */
    public fun imLong(initialValue: Long): IMValueLong {
        return addAnimationTimeListener(IMValueLong(initialValue))
    }

    /**
     * Creates an RMValueLong and registers it for animation updates.
     */
    public fun imLong(callback: LongSupplier): IMValueLong {
        return addAnimationTimeListener(IMValueLong(callback))
    }
    //endregion
    //endregion

    //region LayerBaseHandler
    /**
     * Whether this component should be drawn. If this value is false, this component won't respond to input events.
     *
     * Drives [isVisible]
     */
    public val isVisible_im: IMValueBoolean = imBoolean(true)

    /**
     * Whether this component should be drawn. If this value is false, this component won't respond to input events.
     *
     * Driven by [isVisible_im]
     */
    public var isVisible: Boolean by isVisible_im
    //endregion

    //region LayerRelationshipHandler
    private val _children = mutableListOf<GuiLayer>()

    /**
     * A read-only list containing all the children of this layer. For safely iterating over this list use
     * [forEachChild] as it will prevent [ConcurrentModificationException]s and prevent crashes caused by removing
     * children while iterating.
     */
    public val children: List<GuiLayer> = _children.unmodifiableView()

    /**
     * The immediate parent of this layer, or null if this layer has no parent.
     */
    public var parent: GuiLayer? = null
        private set

    /**
     * A read-only set containing all the parents of this layer, recursively.
     */
    public val parents: Set<GuiLayer>
        get() = generateSequence(parent) { it.parent }.toSet()

    /**
     * The root of this component's hierarchy. i.e. the last layer found when iterating back through the parents. If this
     * component has no parent, returns this component.
     */
    public val root: GuiLayer
        get() = this.parent?.root ?: this

    /**
     * Adds children to this layer. Any layers that are already children of this layer will be ignored after logging a
     * warning.
     *
     * @throws LayerHierarchyException if adding one of the passed layers creates loops in the layer hierarchy
     * @throws LayerHierarchyException if one of the passed layers already had a parent that wasn't this layer
     */
    public fun add(vararg layers: GuiLayer) {
        for (layer in layers) {
            if (layer === this)
                throw LayerHierarchyException("Tried to add a layer to itself")

            if (layer.parent != null) {
                if (layer.parent == this) {
                    logger.warn("The passed layer was already a child of this layer", Exception())
                    continue
                } else {
                    throw LayerHierarchyException("The passed layer already had another parent")
                }
            }

            if (layer in parents) {
                throw LayerHierarchyException("Recursive layer hierarchy, the passed layer is an ancestor of this layer")
            }

            if (this.BUS.fire(GuiLayerEvents.AddChildEvent(layer)).isCanceled())
                return
            layer.BUS.fire(GuiLayerEvents.AddToParentEvent(this))
            _children.add(layer)
            markLayoutDirty()
            layer.parent = this
        }
    }

    /**
     * Checks whether this layer has the passed layer as a descendent
     */
    public operator fun contains(layer: GuiLayer): Boolean =
        generateSequence(layer) { it.parent }.any { it == this }

    /**
     * Removes the passed layer from this layer's children. If the passed layer has no parent this will log an error
     * and return immediately.
     *
     * @throws LayerHierarchyException if the passed layer has a parent that isn't this layer
     */
    public fun remove(layer: GuiLayer) {
        if (layer.parent == null) {
            logger.warn("The passed layer has no parent", Exception())
            return
        } else if (layer.parent != this) {
            throw LayerHierarchyException("This isn't the layer's parent")
        }

        if (this.BUS.fire(GuiLayerEvents.RemoveChildEvent(layer)).isCanceled())
            return
        layer.BUS.fire(GuiLayerEvents.RemoveFromParentEvent(this))
        layer.parent = null
        if (layer === _maskLayer)
            _maskLayer = null
        _children.remove(layer)
        markLayoutDirty()
    }

    /**
     * Removes the layer from its parent if it has one. Shorthand for `this.parent?.remove(this)`
     */
    public fun removeFromParent() {
        this.parent?.remove(this)
    }

    /**
     * The sort index and render order for the layer. Lower indices appear below higher indices.
     * Note that this does not affect the literal Z axis when rendering, this is purely a sort index.
     *
     * Use [GuiLayer.OVERLAY_Z] and [GuiLayer.UNDERLAY_Z] to create layers that appear on top or below _literally
     * everything else._
     */
    public var zIndex: Double = 0.0

    @JvmSynthetic
    internal fun zSort() {
        var outOfOrder = false
        var previousZ = Double.NaN
        for (layer in _children) {
            // comparison with initial NaN is always false
            if (layer.zIndex < previousZ) {
                outOfOrder = true
                break
            }
            previousZ = layer.zIndex
        }
        if (outOfOrder) {
            _children.sortBy { it.zIndex }
            markLayoutDirty()
        }
        forEachChild { it.zSort() }
    }

    /**
     * Iterates over children while allowing children to be added or removed. Any added children will not be iterated,
     * and any children removed while iterating will be excluded.
     *
     * @param includeMask Whether to include the mask layer
     * @param block The block to apply to each layer
     */
    @JvmSynthetic
    public inline fun forEachChild(includeMask: Boolean = true, block: (GuiLayer) -> Unit) {
        // calling `toList` just creates an array and then an ArrayList, so we just use the array
        for (child in children.toTypedArray()) {
            // a component may have been removed, in which case it won't be expecting any interaction
            if (child.parent !== this || (child === maskLayer && !includeMask))
                continue
            block(child)
        }
    }

    /**
     * Iterates over children while allowing children to be added or removed. Any added children will not be iterated,
     * and any children removed while iterating will be excluded.
     *
     * @param includeMask Whether to include the mask layer
     * @param block The block to apply to each layer
     */
    public fun forEachChild(includeMask: Boolean, block: Consumer<GuiLayer>) {
        // calling `toList` just creates an array and then an ArrayList, so we just use the array
        for (child in children.toTypedArray()) {
            // a component may have been removed, in which case it won't be expecting any interaction
            if (child.parent !== this || (child === maskLayer && !includeMask))
                continue
            block.accept(child)
        }
    }

    /**
     * Iterates over children while allowing children to be added or removed. Any added children will not be iterated,
     * and any children removed while iterating will be excluded.
     *
     * This method includes the mask layer.
     *
     * @param block The block to apply to each layer
     */
    public fun forEachChild(block: Consumer<GuiLayer>) {
        forEachChild(true, block)
    }

    /**
     * Creates an object designed to allow easily inspecting the layer hierarchy in the debugger
     */
    public val debuggerTree: DebuggerTree get() = DebuggerTree(this, children.map { it.debuggerTree })

    //endregion

    //region LayerGeometryHandler
    /**
     * The bounding rectangle of this layer in its parent's coordinate space. The "outer" edge. Setting this value will
     * not respect rotation.
     */
    public var frame: Rect2d
        get() = parentSpace?.let { this.convertRectTo(bounds, it) } ?: bounds
        set(value) {
            val current = this.frame
            if (value.size == current.size) {
                pos += value.pos - current.pos
            } else {
                pos = value.pos + value.size * anchor
                val scale = scale2d
                size = vec(
                    if (scale.x == 0.0) size.x else value.width / scale.x,
                    if (scale.y == 0.0) size.y else value.height / scale.y
                )
            }
        }

    /**
     * The bounding rectangle of this layer in its own coordinate space. The "inner" edge.
     */
    public val bounds: Rect2d
        get() = Rect2d(vec(0, 0), size)

    /**
     * The size of the layer in its own coordinate space
     */
    public val size_rm: RMValue<Vec2d> = rmValue(vec(width, height)) { old, new ->
        if (old != new) {
            markLayoutDirty()
            parent?.markLayoutDirty()
            matrixDirty = true
        }
    }

    /**
     * The size of the layer in its own coordinate space
     */
    public var size: Vec2d by size_rm

    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    public val pos_rm: RMValue<Vec2d> = rmValue(vec(posX, posY)) { old, new ->
        if (old != new) {
            parent?.markLayoutDirty()
            matrixDirty = true
        }
    }

    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    public var pos: Vec2d by pos_rm

    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    public val scale_rm: RMValue<Vec2d> = rmValue(vec(1, 1)) { old, new ->
        if (old != new) {
            parent?.markLayoutDirty()
            matrixDirty = true
        }
    }

    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    public var scale2d: Vec2d by scale_rm

    /**
     * The average scale between the X and Y axes. Setting this value sets both the X and Y scales to this value.
     */
    public var scale: Double
        get() = (scale2d.x + scale2d.y) / 2
        set(value) {
            scale2d = vec(value, value)
        }

    /**
     * The clockwise rotation in radians about the anchor.
     */
    public val rotation_rm: RMValueDouble = rmDouble(0.0) { old, new ->
        if (old != new) {
            parent?.markLayoutDirty()
            matrixDirty = true
        }
    }

    /**
     * The clockwise rotation in radians about the anchor.
     */
    public var rotation: Double by rotation_rm

    /**
     * The fractional anchor position in this layer's coordinate space.
     * (0, 0) is the top-left corner, (1, 1) is the bottom-right, and (0.5, 0.5) is the middle.
     *
     * Setting [pos] sets the position of the anchor, not the layer's origin.
     *
     * Setting [rotation] rotates around the anchor, not the layer origin.
     *
     * Setting [scale] scales around the anchor, not the layer origin.
     */
    public val anchor_rm: RMValue<Vec2d> = rmValue(Vec2d.ZERO) { old, new ->
        if (old != new) {
            parent?.markLayoutDirty()
            matrixDirty = true
        }
    }

    /**
     * The fractional anchor position in this layer's coordinate space.
     * (0, 0) is the top-left corner, (1, 1) is the bottom-right, and (0.5, 0.5) is the middle
     * outside the bounds of the layer.
     *
     * Setting [pos] sets the position of the anchor, not the layer's origin.
     *
     * Setting [rotation] rotates around the anchor, not the layer origin.
     *
     * Setting [scale] scales around the anchor, not the layer origin.
     */
    public var anchor: Vec2d by anchor_rm

    /**
     * The width of this layer as a double.
     *
     * Shorthand for `layer.size.x`
     *
     * @see widthf
     * @see widthi
     */
    public var width: Double
        get() = size.x
        set(value) {
            size = vec(value, size.y)
        }

    /**
     * The width of this layer as a float.
     *
     * Shorthand for `layer.size.xf`
     *
     * @see width
     * @see widthi
     */
    public var widthf: Float
        get() = size.xf
        set(value) {
            size = vec(value, size.y)
        }

    /**
     * The width of this layer as an int (truncating)
     *
     * Shorthand for `layer.size.xi`
     *
     * @see width
     * @see widthf
     */
    public var widthi: Int
        get() = size.xi
        set(value) {
            size = vec(value, size.y)
        }

    /**
     * The height of this layer as a double.
     *
     * Shorthand for `layer.size.y`
     *
     * @see heightf
     * @see heighti
     */
    public var height: Double
        get() = size.y
        set(value) {
            size = vec(size.x, value)
        }

    /**
     * The height of this layer as a float.
     *
     * Shorthand for `layer.size.yf`
     *
     * @see height
     * @see heighti
     */
    public var heightf: Float
        get() = size.yf
        set(value) {
            size = vec(size.x, value)
        }

    /**
     * The height of this layer as an int (truncating)
     *
     * Shorthand for `layer.size.yi`
     *
     * @see height
     * @see heightf
     */
    public var heighti: Int
        get() = size.yi
        set(value) {
            size = vec(size.x, value)
        }

    /**
     * The X position of this layer as a double.
     *
     * Shorthand for `layer.pos.x`
     *
     * @see xf
     * @see xi
     */
    public var x: Double
        get() = pos.x
        set(value) {
            pos = vec(value, pos.y)
        }

    /**
     * The X position of this layer as a float.
     *
     * Shorthand for `layer.pos.xf`
     *
     * @see x
     * @see xi
     */
    public var xf: Float
        get() = pos.xf
        set(value) {
            pos = vec(value, pos.y)
        }

    /**
     * The X position of this layer as an int (truncating)
     *
     * Shorthand for `layer.pos.xi`
     *
     * @see x
     * @see xf
     */
    public var xi: Int
        get() = pos.xi
        set(value) {
            pos = vec(value, pos.y)
        }

    /**
     * The Y position of this layer as a double.
     *
     * Shorthand for `layer.pos.y`
     *
     * @see yf
     * @see yi
     */
    public var y: Double
        get() = pos.y
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * The Y position of this layer as a float.
     *
     * Shorthand for `layer.pos.yf`
     *
     * @see y
     * @see yi
     */
    public var yf: Float
        get() = pos.yf
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * The Y position of this layer as an int (truncating)
     *
     * Shorthand for `layer.pos.yi`
     *
     * @see y
     * @see yf
     */
    public var yi: Int
        get() = pos.yi
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * Returns true if the passed point is inside this component, ignoring any clipping.
     */
    public open fun isPointInBounds(point: Vec2d): Boolean {
        return point in bounds
    }

    /**
     * Returns true if the passed point is outside this component's clipping mask.
     */
    @Suppress("UNUSED_PARAMETER")
    public fun isPointClipped(point: Vec2d): Boolean {
        if (clippingSprite != null) return false // we can't clip these
        if (clipToBounds) {
            if (point.x < 0 || point.x > size.x ||
                point.y < 0 || point.y > size.y) {
                return true
            }

            if (cornerRadius != 0.0) {
                if (point.x < cornerRadius && point.y < cornerRadius &&
                    point.squareDist(vec(cornerRadius, cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x < cornerRadius && point.y > size.y - cornerRadius &&
                    point.squareDist(vec(cornerRadius, size.y - cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x > size.x - cornerRadius && point.y > size.y - cornerRadius &&
                    point.squareDist(vec(size.x - cornerRadius, size.y - cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x > size.x - cornerRadius && point.y < cornerRadius &&
                    point.squareDist(vec(size.x - cornerRadius, cornerRadius)) > cornerRadius * cornerRadius)
                    return true
            }
        }
        return false
    }

    /**
     * Get the aggregate of this layer's contents recursively. The returned rect is in this layer's coordinates.
     *
     * - Any layers for which [includeOwnBounds] returns false will not count their own bounds in the calculation
     * (useful for things such as large mask wrappers which would bloat the content size).
     * - Any layers for which [includeChildren] returns false will not count their children's content bounds
     * (useful primarily in combination with returning false from [includeOwnBounds] to totally exclude a layer)
     *
     * Pseudocode implementation:
     * ```kotlin
     * var totalBounds = null
     * if(includeOwnBounds(this)) {
     *     <expand totalBounds to fit this component's bounds>
     * }
     * if(includeChildren(this)) {
     *     for(child in children) {
     *         <expand totalBounds to fit child's getContentsBounds (converting to this layer's coordinate space)>
     *     }
     * }
     * return totalBounds
     * ```
     *
     * @param includeOwnBounds A predicate to filter out which layers should count their own bounds
     * @param includeChildren A predicate to filter out which layers should count their children's bounds
     * @return The rect containing all the children that are included as per the above rules, or null if neither this
     * layer nor any of its children were included
     */
    public fun getContentsBounds(
        includeOwnBounds: Predicate<GuiLayer>,
        includeChildren: Predicate<GuiLayer>
    ): Rect2d? {
        var bounds: Rect2d? = null
        if (includeOwnBounds.test(this)) {
            bounds = this.bounds
        }
        if (includeChildren.test(this)) {
            for (child in children) {
                val subBounds = child.getContentsBounds(includeOwnBounds, includeChildren) ?: continue
                val subFrame = child.convertRectToParent(subBounds)
                bounds = bounds?.expandToFit(subFrame) ?: subFrame
            }
        }
        return bounds
    }

    override val parentSpace: CoordinateSpace2D?
        get() = parent ?: ScreenSpace

    private var matrixDirty = true
    private var _matrix = MutableMatrix3d()
    override val transform: Matrix3d = Matrix3dView(_matrix)
        get() {
            if (matrixDirty) {
                updateMatrix()
            }
            return field
        }

    private var _inverseMatrix = MutableMatrix3d()
    override val inverseTransform: Matrix3d = Matrix3dView(_inverseMatrix)
        get() {
            if (matrixDirty) {
                updateMatrix()
            }
            return field
        }

    private fun updateMatrix() {
        val inverseScale = vec(
            if (scale2d.x == 0.0) Double.POSITIVE_INFINITY else 1.0 / scale2d.x,
            if (scale2d.y == 0.0) Double.POSITIVE_INFINITY else 1.0 / scale2d.y
        )

        _matrix.set(Matrix3d.IDENTITY)
        _matrix.translate(pos)
        _matrix.rotate2d(rotation)
        _matrix.scale(scale2d.x, scale2d.y, 1.0)
        _matrix.translate(-anchor * size)

        _inverseMatrix.set(Matrix3d.IDENTITY)
        _inverseMatrix.translate(anchor * size)
        _inverseMatrix.scale(inverseScale.x, inverseScale.y, 1.0)
        _inverseMatrix.rotate2d(-rotation)
        _inverseMatrix.translate(-pos)

        matrixDirty = false
    }
    //endregion

    //region LayerRenderHandler

    /**
     * Clip the contents of this layer to its bounding box
     */
    public var clipToBounds: Boolean = false

    /**
     * If nonnull, this sprite is used for clipping. Any pixels that are completely transparent will be masked out.
     * This method of clipping does not support clipping mouseover checks.
     *
     * If [clippingSprite] is nonnull, it will override this sprite.
     */
    public var clippingSprite: ISprite? = null

    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    public val opacity_rm: RMValueDouble = rmDouble(1.0)

    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    public var opacity: Double by opacity_rm

    /**
     * How to apply the [maskLayer]. If [maskLayer] is null, this property has no effect.
     */
    public var maskMode: MaskMode = MaskMode.NONE

    private var _maskLayer: GuiLayer? = null

    /**
     * The layer to use when masking. Setting this property will automatically add or remove the layer as a child of
     * this layer. Note that removing this layer using the [remove] or [removeFromParent] method will reset this
     * property to null.
     */
    public var maskLayer: GuiLayer?
        get() = _maskLayer
        set(value) {
            if (value !== _maskLayer) {
                _maskLayer?.also {
                    this.remove(it)
                }
                _maskLayer = value
                value?.also {
                    this.add(it)
                }
            }
        }

    /**
     * The blend mode to use for this layer. Any value other than [BlendMode.NORMAL] will cause the layer to be rendered
     * to a texture using [RenderMode.RENDER_TO_FBO]
     */
    public var blendMode: BlendMode = BlendMode.NORMAL

    /**
     * What technique to use to render this layer
     */
    public var renderMode: RenderMode = RenderMode.DIRECT

    /**
     * What scaling factor to use when rasterizing this layer using [RenderMode.RENDER_TO_QUAD]
     */
    public var rasterizationScale: Int = 1

    /**
     * Whether this layer is being used as a mask by its parent.
     */
    public val isMask: Boolean
        get() = parent?.maskLayer === this

    private fun actualRenderMode(): RenderMode {
        if (renderMode != RenderMode.DIRECT)
            return renderMode
        if (opacity < 1.0 || maskMode != MaskMode.NONE || blendMode != BlendMode.NORMAL)
            return RenderMode.RENDER_TO_FBO
        return RenderMode.DIRECT
    }

    /**
     * An internal field created so containers to render different portions of the layer hierarchy at different times.
     * For example, rendering the foreground layer in a separate method. See [FacadeWidget.filterRendering] for details.
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    internal var skipRender: Boolean = false

    /**
     * Renders this layer and its sublayers. This method handles the internals of rendering a layer. Override [draw] for
     * custom rendering.
     */
    public fun renderLayer(context: GuiDrawContext) {
        if(skipRender) return

        context.matrix.push()
        context.matrix *= transform

        if (!isVisible) {
            renderSkeleton(context)
            context.matrix.pop()
            return
        }

        val enableClipping = clipToBounds
        if (enableClipping)
            StencilUtil.push { stencil(context) }

        val renderMode = actualRenderMode()
        if (renderMode == RenderMode.DIRECT) {
            renderDirect(context)
        } else {
            val flatContext = if (renderMode == RenderMode.RENDER_TO_QUAD) {
                GuiDrawContext(Matrix3dStack(), context.debugOptions, context.isInMask).also {
                    it.matrix.scale(max(1, rasterizationScale).toDouble())
                }
            } else {
                context
            }
            var maskFBO: Framebuffer? = null
            var layerFBO: Framebuffer? = null
            try {

                layerFBO = FramebufferPool.renderToFramebuffer {
                    clearBounds(flatContext)
                    renderDirect(flatContext)
                }
                val maskLayer = maskLayer
                if (maskMode != MaskMode.NONE && maskLayer != null) {
                    flatContext.isInMask = true
                    maskFBO = FramebufferPool.renderToFramebuffer {
                        clearBounds(flatContext)
                        maskLayer.renderLayer(flatContext)
                    }
                }

//                layerFilter?.filter(this.layer, layerFBO, maskFBO) TODO: add back filters?

                FlatLayerShader.layerImage.set(layerFBO.framebufferTexture)
                FlatLayerShader.maskImage.set(maskFBO?.framebufferTexture ?: 0)
                FlatLayerShader.alphaMultiply.set(opacity.toFloat())
                FlatLayerShader.maskMode.set(maskMode.ordinal)
                FlatLayerShader.renderMode.set(renderMode.ordinal)
                FlatLayerShader.blendMode = blendMode

                val maxU = (size.xf * rasterizationScale) / Client.window.framebufferWidth
                val maxV = (size.yf * rasterizationScale) / Client.window.framebufferHeight

                val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
                val vb = buffer.getBuffer(flatLayerRenderType)
                // why 1-maxV?
                vb.pos2d(context.matrix, 0, size.y).tex(0f, 1 - maxV).endVertex()
                vb.pos2d(context.matrix, size.x, size.y).tex(maxU, 1 - maxV).endVertex()
                vb.pos2d(context.matrix, size.x, 0).tex(maxU, 1f).endVertex()
                vb.pos2d(context.matrix, 0, 0).tex(0f, 1f).endVertex()
                buffer.finish()
                GlStateManager.activeTexture(GL13.GL_TEXTURE0)
                GlStateManager.disableTexture()
                GlStateManager.enableTexture()
            } finally {
                layerFBO?.also { FramebufferPool.releaseFramebuffer(it) }
                maskFBO?.also { FramebufferPool.releaseFramebuffer(it) }
            }
        }

        if (enableClipping)
            StencilUtil.pop { stencil(context) }

        drawDebugInfo(context)
        didLayout = false

        context.matrix.pop()
    }

    /**
     * Draw just this layer and its children
     */
    private fun renderDirect(context: GuiDrawContext) {
        context.matrix.assertEvenDepth {
            glStateGuarantees()
            context.matrix.push()
            context.matrix.assertEvenDepth {
                draw(context)
            }
            context.popGlMatrix()
            context.matrix.pop()
        }
        forEachChild(false) {
            it.renderLayer(context)
        }
    }

    /**
     * Clear this layer's bounding box in the current Framebuffer. This is used to avoid having to clear the entire
     * buffer when rendering to a texture
     */
    private fun clearBounds(context: GuiDrawContext) {
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(clearBufferRenderType)
        vb.pos2d(context.matrix, 0, size.y).endVertex()
        vb.pos2d(context.matrix, size.x, size.y).endVertex()
        vb.pos2d(context.matrix, size.x, 0).endVertex()
        vb.pos2d(context.matrix, 0, 0).endVertex()
        buffer.finish()
    }

    private fun stencil(context: GuiDrawContext) {
        val sp = clippingSprite
        if (sp != null) {
            sp.draw(context.matrix, 0f, 0f, widthf, heightf, animationTime.toInt(), Color.WHITE)
            return
        }

        val color = Color(1f, 0f, 1f, 0.5f)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(flatColorFanRenderType)

        val points = getBoundingBoxPoints()
        vb.pos2d(context.matrix, size.x / 2, size.y / 2).color(color).endVertex()
        points.reversed().forEach {
            vb.pos2d(context.matrix, it.x, it.y).color(color).endVertex()
        }

        buffer.finish()
    }

    public fun shouldDrawSkeleton(): Boolean = false

    public fun renderSkeleton(context: GuiDrawContext) {
        forEachChild { it.renderSkeleton(context) }

        if (context.debugOptions.showDebugBoundingBox && !context.isInMask &&
            GuiLayer.showDebugTilt && shouldDrawSkeleton()
        ) {
            RenderSystem.lineWidth(1f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            drawBoundingBox(context, Color(.75f, 0f, .75f, 1f))
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }
    }

    private fun drawDebugInfo(context: GuiDrawContext) {
        if (context.isInMask)
            return

        val options = context.debugOptions
        val debugColor = Color(.75f, 0f, .75f, 1f)
        if (options.showDebugBoundingBox) {
            RenderSystem.lineWidth(1f)
            drawBoundingBox(context, if (mouseOver) Color.WHITE else debugColor)
        }
        if (options.showClippedBoundingBoxes && clipToBounds) {
            RenderSystem.lineWidth(2f)
            drawBoundingBox(context, Color.RED)
        }

        if (options.highlightLayout && didLayout) {
            drawLayerOverlay(context, Color(1f, 0f, 0f, 0.1f))
        }
        if (options.highlightFractionalScale &&
            (abs(scale2d.x - scale2d.xi) > 0.001 || abs(scale2d.y - scale2d.yi) > 0.001)
        ) {
            RenderSystem.lineWidth(2f)
            drawBoundingBox(context, Color.RED)
        }
    }

    /**
     * Draws a flat colored box over this layer, rounding corners as necessary
     */
    private fun drawLayerOverlay(context: GuiDrawContext, color: Color) {

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(flatColorFanRenderType)

        val points = getBoundingBoxPoints()
        vb.pos2d(context.matrix, size.x / 2, size.y / 2).color(color).endVertex()
        points.reversed().forEach {
            vb.pos2d(context.matrix, it.x, it.y).color(color).endVertex()
        }

        buffer.finish()
    }

    /**
     * Draws a bounding box around the edge of this component
     */
    private fun drawBoundingBox(context: GuiDrawContext, color: Color) {
        val points = getBoundingBoxPoints()

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(debugBoundingBoxRenderType)

        points.forEach {
            vb.pos2d(context.matrix, it.x, it.y).color(color).endVertex()
        }

        buffer.finish()
    }

    public var cornerRadius: Double = 0.0

    /**
     * Creates a series of points defining the path the debug bounding box follows. For culling reasons this list
     * must be in clockwise order
     */
    protected fun getBoundingBoxPoints(): List<Vec2d> {
        val list = mutableListOf<Vec2d>()
        if (cornerRadius != 0.0) {
            val rad = cornerRadius
            val d = (PI / 2) / 16

            // top-left
            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastCos(angle) * rad, fastSin(angle) * rad)
                list.add(vec(rad - diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastSin(angle) * rad, fastCos(angle) * rad)
                list.add(vec(size.x - rad + diff.x, rad - diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastCos(angle) * rad, fastSin(angle) * rad)
                list.add(vec(size.x - rad + diff.x, size.y - rad + diff.y))
            }

            (0..16).forEach { i ->
                val angle = d * i
                val diff = vec(fastSin(angle) * rad, fastCos(angle) * rad)
                list.add(vec(rad - diff.x, size.y - rad + diff.y))
            }

            list.add(vec(0.0, rad))
        } else {
            list.add(vec(0.0, 0.0))
            list.add(vec(size.x, 0.0))
            list.add(vec(size.x, size.y))
            list.add(vec(0.0, size.y))
            list.add(vec(0.0, 0.0))
        }
        return list
    }
    //endregion

    //region Events

    /**
     * The event bus on which all events for this layer are fired.
     *
     * The built-in base events are located in [GuiLayerEvents]
     */
    @JvmField
    public val BUS: EventBus = EventBus()

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     */
    public inline fun <reified E: Event> hook(hook: Consumer<E>) {
        BUS.hook(hook)
    }

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     * @param priority the hook priority
     */
    public inline fun <reified E: Event> hook(priority: EventBus.Priority, hook: Consumer<E>) {
        BUS.hook(priority, hook)
    }

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     * @param priority the hook priority
     * @param receiveCanceled whether the hook should still receive canceled events
     */
    public inline fun <reified E: Event> hook(priority: EventBus.Priority, receiveCanceled: Boolean, hook: Consumer<E>) {
        BUS.hook(priority, receiveCanceled, hook)
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param priority the hook priority
     * @param receiveCanceled whether the hook should still receive canceled events
     * @param hook the event consumer
     */
    public fun <E: Event> hook(clazz: Class<E>, priority: EventBus.Priority, receiveCanceled: Boolean, hook: Consumer<E>) {
        BUS.hook(clazz, priority, receiveCanceled, hook)
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param priority the hook priority
     * @param hook the event consumer
     */
    public fun <E: Event> hook(clazz: Class<E>, priority: EventBus.Priority, hook: Consumer<E>) {
        BUS.hook(clazz, priority, hook)
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param hook the event consumer
     */
    public fun <E: Event> hook(clazz: Class<E>, hook: Consumer<E>) {
        BUS.hook(clazz, hook)
    }

    init {
        BUS.register(this)
    }

    //endregion

    //region Input

    /**
     * The cursor for when the mouse is over this layer, or `null` for the default cursor.
     */
    public var cursor: Cursor? = null

    /**
     * If [interactive] is false, this component and its descendents won't be considered for mouseover calculations
     * and won't receive input events
     */
    public var interactive: Boolean = true

    /**
     * If [ignoreMouseOverBounds] is true, this component's bounding box won't be taken into consideration for mouseover
     * calculations, however its children will be considered as usual.
     */
    public var ignoreMouseOverBounds: Boolean = false

    /**
     * If [propagatesMouseOver] is true (the default), the [mouseOver] state will be propagated to this layer's parent.
     * i.e. when true, if the mouse is over this component it will also count as over this component's parent.
     */
    public var propagatesMouseOver: Boolean = true

    /**
     * True if the current [mousePos] is inside the bounds of component. This ignores components that may be covering
     * this component.
     */
    public var mouseInside: Boolean = false
        private set

    /**
     * True if this component is [interactive] and the mouse is hovering over it or one of its children.
     */
    public var mouseOver: Boolean = false
        @JvmSynthetic
        internal set

    private var wasMouseOver: Boolean = false

    /**
     * The mouse position within this component
     */
    public var mousePos: Vec2d = vec(0, 0)
        private set

    /**
     * Performs a mouse hit test, returning the topmost layer the mouse is directly over (including this layer).
     */
    public fun hitTest(pos: Vec2d): GuiLayer? {
        return hitTest(convertPointToParent(pos), Matrix3dStack(), false)
    }

    /**
     * Computes the mouse position and its derived property values. If [isMousePos] is true, the hit test will be
     * treated as a change to the mouse position, updating [mousePos] and [mouseInside], and clearing [mouseOver].
     */
    @JvmSynthetic
    internal fun hitTest(rootPos: Vec2d, stack: Matrix3dStack, isMousePos: Boolean): GuiLayer? {
        stack.push()
        stack.reverseMul(inverseTransform)

        val mousePos = stack.transform(rootPos)
        if(isMousePos) this.mousePos = mousePos

        val clipped = isPointClipped(mousePos)
        val mouseInside = isPointInBounds(mousePos) && !clipped
        if(isMousePos) this.mouseInside = mouseInside

        if(isMousePos) this.mouseOver = false
        var mouseOverChild: GuiLayer? = null
        forEachChild { child ->
            mouseOverChild = child.hitTest(rootPos, stack, isMousePos) ?: mouseOverChild
        }
        stack.pop()

        if (!interactive || !isVisible || clipped)
            return null

        return when {
            mouseOverChild != null -> mouseOverChild
            mouseInside && !ignoreMouseOverBounds -> this
            else -> null
        }
    }

    /**
     * The set of buttons with a potential click in progress (when the button is pressed down while inside this layer)
     */
    private var clickingButtons = mutableSetOf<Int>()

    @JvmSynthetic
    internal fun triggerEvent(event: Event) {
        // We won't send the event to any layers added during the event handling. If we don't do this than the game can
        // infinitely recurse when a layer adds another of itself inside an event.
        val preChildren = children.toTypedArray()

        when (event) {
            is GuiLayerEvents.MouseEvent -> {
                if (!interactive)
                    return
                event.stack.push()
                event.stack.reverseMul(inverseTransform)
                BUS.fire(event)

                if (event is GuiLayerEvents.MouseDown && mouseOver) {
                    clickingButtons.add(event.button)
                }
                if (event is GuiLayerEvents.MouseUp && event.button in clickingButtons) {
                    clickingButtons.remove(event.button)
                    if (mouseOver) {
                        BUS.fire(when (event.button) {
                            0 -> GuiLayerEvents.MouseClick(event.rootPos)
                            1 -> GuiLayerEvents.MouseRightClick(event.rootPos)
                            else -> GuiLayerEvents.MouseOtherClick(event.rootPos, event.button)
                        })
                    }
                }
                if(event is GuiLayerEvents.MouseMove && wasMouseOver != mouseOver) {
                    if(mouseOver)
                        BUS.fire(GuiLayerEvents.MouseMoveOver(event.rootPos, event.lastRootPos))
                    else
                        BUS.fire(GuiLayerEvents.MouseMoveOff(event.rootPos, event.lastRootPos))
                    wasMouseOver = mouseOver
                }

                preChildren.forEach {
                    if (it.parent !== this)
                        return@forEach
                    it.triggerEvent(event)
                }
                event.stack.pop()
                return // early return because we already fired the event and iterated children
            }
            is GuiLayerEvents.KeyEvent -> {
                if (!interactive)
                    return
            }
            is GuiLayerEvents.Update -> {
                this.update()
            }
            is GuiLayerEvents.PrepareLayout -> {
                this.prepareLayout()
            }
        }
        BUS.fire(event)
        preChildren.forEach {
            if (it.parent !== this)
                return@forEach
            it.triggerEvent(event)
        }
    }

    //endregion

    //region Tooltips
    private val _tooltipTextLayer by lazy { PastryBasicTooltip() }

    /**
     * @see tooltipText
     */
    public val tooltipText_im: IMValue<String?> = imValue()

    /**
     * The text to display as a tooltip when the mouse is over this component. If [tooltip] is nonnull this value will
     * be ignored.
     */
    public var tooltipText: String? by tooltipText_im

    /**
     * @see tooltip
     */
    public val tooltip_rm: RMValue<GuiLayer?> = rmValue(null)

    /**
     * The layer to display as a tooltip when the mouse is over this component. If this value is null it will fall back
     * to [tooltipText].
     */
    public var tooltip: GuiLayer? by tooltip_rm

    /**
     * @see tooltipDelay
     */
    @Deprecated("UNIMPLEMENTED")
    public val tooltipDelay_im: IMValueInt = imInt(0)

    /**
     * How many ticks should the mouse have to hover over this component before the tooltip appears.
     */
    @Deprecated("UNIMPLEMENTED")
    public var tooltipDelay: Int by tooltipDelay_im

    public val tooltipLayer: GuiLayer?
        get() {
            tooltip?.also { return it }
            tooltipText?.also {
                _tooltipTextLayer.text = it
                return _tooltipTextLayer
            }
            return null
        }
    //endregion

    //region Layout
    /**
     * Whether this layer's layout needs to be updated this frame. This is set to true when this layer's size changes or
     * it is manually set using [markLayoutDirty]
     */
    public var isLayoutDirty: Boolean = false
        private set

    /**
     * Whether this layer's layout depends on the layout of its children, and thus should be marked dirty when they are.
     */
    public var dependsOnChildLayout: Boolean = false

    /**
     * Set to true when layout is run
     */
    private var didLayout: Boolean = false

    /**
     * Mark this layer's layout dirty flag.
     */
    public fun markLayoutDirty() {
        isLayoutDirty = true
        parent?.also {
            if (it.dependsOnChildLayout)
                it.markLayoutDirty()
        }
    }

    /**
     * Clears this layer's layout dirty flag.
     */
    public fun clearLayoutDirty() {
        isLayoutDirty = false
    }

    /**
     * Run a layout pass for this layer and its children.
     */
    public fun runLayout() {
        this.updateYogaLayout()
        this.updateDirtyLayout(GuiLayerEvents.LayoutChildren())
    }

    private fun updateDirtyLayout(event: GuiLayerEvents.LayoutChildren) {
        if (isLayoutDirty) {
            didLayout = true
            layoutChildren()
            BUS.fire(event)
        }
        forEachChild { it.updateDirtyLayout(event) }
        isLayoutDirty = false
    }

    @JvmSynthetic
    internal fun clearAllDirtyLayout() {
        clearLayoutDirty()
        forEachChild { it.clearAllDirtyLayout() }
    }

    private val layoutHooks = mutableListOf<Runnable>()

    public fun onLayout(hook: () -> Unit) {
        layoutHooks.add(Runnable(hook))
    }

    public fun onLayout(hook: Runnable) {
        layoutHooks.add(hook)
    }

    @Hook
    private fun runLayoutHooks(e: GuiLayerEvents.LayoutChildren) {
        layoutHooks.forEach(Runnable::run)
    }
    //endregion

    //region Yoga

    public var useYoga: Boolean = false
    private val yogaNode: Long by GlResourceGc.track(this, YGNodeNewWithConfig(config)) { YGNodeFree(it) }
    public val yogaStyle: YogaStyle = YogaStyle(yogaNode)
    private val _yogaStyler: YogaStyler by lazy { YogaStyler(this) }

    /**
     * Enables Yoga layout on this layer and returns the yoga styler. If yoga was previously disabled, this method also
     * copies the current layer size into the width/height yoga styles.
     */
    public fun yoga(): YogaStyler {
        if (!useYoga) {
            useYoga = true
            _yogaStyler.sizeFromCurrent()
        }
        return _yogaStyler
    }

    private var yogaChildren = listOf<GuiLayer>()
    private fun updateYogaChildren() {
        if (!useYoga) {
            YGNodeRemoveAllChildren(yogaNode)
            return
        }
        val newYogaChildren = _children.filter { it.useYoga }
        if (yogaChildren != newYogaChildren) {
            val childrenBuffer = BufferUtils.createPointerBuffer(newYogaChildren.size)
            newYogaChildren.forEach {
                childrenBuffer.put(it.yogaNode)
            }
            childrenBuffer.rewind()
            YGNodeSetChildren(yogaNode, childrenBuffer)
            yogaChildren = newYogaChildren
        }
    }

    private fun updateYogaLayout() {
        this.prepareYogaLayout()
        this.computeYogaLayout()
        this.applyYogaLayout()
    }

    private fun prepareYogaLayout() {
        updateYogaChildren()
        if (useYoga) {
            if (yogaStyle.lockWidth) {
                yogaStyle.minWidth.px = widthf
                yogaStyle.width.px = widthf
                yogaStyle.maxWidth.px = widthf
            }
            if (yogaStyle.lockHeight) {
                yogaStyle.minHeight.px = heightf
                yogaStyle.height.px = heightf
                yogaStyle.maxHeight.px = heightf
            }
        }
        forEachChild { it.prepareYogaLayout() }
    }

    private fun computeYogaLayout() {
        if (useYoga && parent?.useYoga != true) {
            YGNodeCalculateLayout(yogaNode, YGUndefined, YGUndefined, YGDirectionLTR)
        }
        forEachChild { it.computeYogaLayout() }
    }

    private fun applyYogaLayout() {
        // only set the pos/size of layers that use yoga and aren't roots
        if (useYoga && parent?.useYoga == true && YGNodeGetHasNewLayout(yogaNode)) {
            this.pos = vec(YGNodeLayoutGetLeft(yogaNode), YGNodeLayoutGetTop(yogaNode))
            this.size = vec(YGNodeLayoutGetWidth(yogaNode), YGNodeLayoutGetHeight(yogaNode))
        }
        YGNodeSetHasNewLayout(yogaNode, false)

        forEachChild { it.applyYogaLayout() }
    }

    //endregion

    public companion object {

        @JvmStatic
        public var showDebugTilt: Boolean = false

        /**
         * Tooltips should be added to the [root] layer with this [zIndex]. Nothing else should go above this level.
         */
        @JvmStatic
        public val TOOLTIP_Z: Double = 1e11

        /**
         * In order to make an overlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        public val OVERLAY_Z: Double = 1e10

        /**
         * In order to make a background layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        public val BACKGROUND_Z: Double = -1e9

        /**
         * In order to make an underlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        public val UNDERLAY_Z: Double = -1e10

        private val config = YGConfigNew()

        init {
            YGConfigSetUseWebDefaults(config, true)
        }

        private val debugBoundingBoxRenderType: RenderType = SimpleRenderTypes.flat(GL11.GL_LINE_LOOP)
        private val flatColorFanRenderType: RenderType = SimpleRenderTypes.flat(GL11.GL_TRIANGLE_FAN)
        private val flatLayerRenderType: RenderType = run {
            val renderState = RenderType.State.getBuilder()
                .build(false)
            mixinCast<IRenderTypeState>(renderState).addState(FlatLayerShader.renderState)

            SimpleRenderTypes.makeType("librarianlib.facade.flat_layer",
                DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, false, false, renderState
            )
        }

        private val clearBufferRenderType: RenderType = run {
            val renderState = RenderType.State.getBuilder()
                .depthTest(DefaultRenderStates.DEPTH_ALWAYS)
                .build(false)

            mixinCast<IRenderTypeState>(renderState).addState(FramebufferClearShader.renderState)

            SimpleRenderTypes.makeType("librarianlib.facade.clear_buffer",
                DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256, false, false, renderState
            )
        }

        private fun glStateGuarantees() {
            RenderSystem.enableTexture()
            RenderSystem.color4f(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            RenderSystem.shadeModel(GL11.GL_SMOOTH)
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.alphaFunc(GL11.GL_GREATER, 1 / 255f)
            RenderSystem.disableLighting()
        }
    }

    public class DebuggerTree(public val layer: GuiLayer, public val children: List<DebuggerTree>) {
        override fun toString(): String {
            return "(${layer.x}, ${layer.y}, ${layer.width}, ${layer.height}) $layer"
        }
    }
}
