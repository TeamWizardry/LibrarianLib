package com.teamwizardry.librarianlib.facade.component

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.core.util.kotlin.weakSetOf
import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.core.util.lerp.Lerpers
import com.teamwizardry.librarianlib.facade.component.supporting.*
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
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.util.ConcurrentModificationException
import java.util.PriorityQueue
import java.util.function.Consumer
import kotlin.math.PI
import kotlin.math.floor

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
open class GuiLayer(posX: Int, posY: Int, width: Int, height: Int): CoordinateSpace2D {
    constructor(posX: Int, posY: Int): this(posX, posY, 0, 0)
    constructor(): this(0, 0, 0, 0)

    /**
     * Called every frame after input events and before the layout pass
     */
    open fun update() {

    }

    /**
     * Called to lay out the children of this layer.
     *
     * This method is called before each frame if this layer's bounds have changed, children have been added/removed,
     * a child's frame has changed, or [setNeedsLayout] has been called on this layer.
     *
     * After this method completes, the children of this component will be checked for layout. This means that changes
     * made in one layer can ripple downward, but also that children can override the layout of their parent.
     * [needsLayout] is reset to false after this layer and its children are laid out, so any changes while laying out
     * will not cause the layout to be recalculated on the next frame.
     *
     * The idea behind this method is that self-contained components/layers can lay out their children dynamically
     * themselves. Examples of such a component would be a self-contained list item, a component that spaces out its
     * children equally along its length, or simply a component that needs to resize its background layer
     * to fit its dimensions.
     */
    open fun layoutChildren() {}

    /**
     * Draws the layer's contents. This is the method to override when creating custom layer rendering.
     *
     * Unless necessary, layers shouldn't change any global GL state. However, as a precaution, there are a number of
     * state guarantees made before every call to [draw]. The authoritative list is provided as a "sample" in these
     * docs.
     *
     * @sample glStateGuarantees
     */
    open fun draw(context: GuiDrawContext) {

    }

    //region Animation
    private val animationTimeListeners = weakSetOf<AnimationTimeListener>()

    fun addAnimationTimeListener(listener: AnimationTimeListener) {
        animationTimeListeners.add(listener)
    }
    fun removeAnimationTimeListener(listener: AnimationTimeListener) {
        animationTimeListeners.remove(listener)
    }

    /**
     * The current time for animations, expressed in ticks since this layer was first added to the layer hierarchy.
     */
    var animationTime: Float = 0f
        private set
    private var baseTime: Float = Float.NaN

    internal fun updateAnimations(time: Float) {
        if(baseTime.isNaN())
            baseTime = floor(time)
        animationTime = time - baseTime

        while(scheduledEvents.isNotEmpty() && scheduledEvents.peek().time <= animationTime) {
            scheduledEvents.poll().event.run()
        }

        animationTimeListeners.forEach {
            it.updateTime(animationTime)
        }
        children.forEach { it.updateAnimations(time) }
    }

    inline fun <reified T> rmValue(initialValue: T, noinline change: (T, T) -> Unit = { _, _ -> }): RMValue<T> {
        @Suppress("UNCHECKED_CAST") val lerper = Lerpers.getOrNull(Mirror.reflect<T>())?.value as Lerper<T>?
        val value = RMValue(initialValue, lerper, change)
        addAnimationTimeListener(value)
        return value
    }

    private val scheduledEvents = PriorityQueue<ScheduledEvent>()

    /**
     * Run the specified callback once [animationTime] is >= [time]
     */
    fun schedule(time: Float, callback: Runnable) {
        scheduledEvents.add(ScheduledEvent(time, callback))
    }

    /**
     * Run the specified callback once [animationTime] is >= [time]
     */
    @JvmSynthetic
    inline fun schedule(time: Float, crossinline callback: () -> Unit) {
        schedule(time, Runnable { callback() })
    }

    /**
     * Run the specified callback after [time] ticks.
     */
    fun delay(time: Float, callback: Runnable) {
        schedule(animationTime + time, callback)
    }

    /**
     * Run the specified callback after [time] ticks.
     */
    @JvmSynthetic
    inline fun delay(time: Float, crossinline callback: () -> Unit) {
        schedule(animationTime + time, Runnable { callback() })
    }

    private data class ScheduledEvent(val time: Float, val event: Runnable): Comparable<ScheduledEvent> {
        override fun compareTo(other: ScheduledEvent): Int {
            return other.time.compareTo(this.time)
        }
    }
    //endregion

    //region LayerBaseHandler
    /**
     * Whether this component should be drawn. If this value is false, this component won't respond to input events.
     *
     * Drives [isVisible]
     */
    val isVisible_im: IMValueBoolean = IMValueBoolean(true)
    /**
     * Whether this component should be drawn. If this value is false, this component won't respond to input events.
     *
     * Driven by [isVisible_im]
     */
    var isVisible by isVisible_im
    //endregion

    //region LayerRelationshipHandler
    private val _children = mutableListOf<GuiLayer>()
    /**
     * A read-only list containing all the children of this layer. For safely iterating over this list use
     * [forEachChild] as it will prevent [ConcurrentModificationException]s and prevent crashes caused by removing
     * children while iterating.
     */
    val children: List<GuiLayer> = _children.unmodifiableView()

    /**
     * The immediate parent of this layer, or null if this layer has no parent.
     */
    var parent: GuiLayer? = null
        private set

    private val _parents = mutableSetOf<GuiLayer>()
    /**
     * A read-only set containing all the parents of this layer, recursively.
     */
    val parents: Set<GuiLayer> = _parents.unmodifiableView()

    /**
     * The root of this component's hierarchy. i.e. the last layer found when iterating back through the parents. If this
     * component has no parent, returns this component.
     */
    val root: GuiLayer
        get() = this.parent?.root ?: this

    /**
     * Adds children to this layer. Any layers that are already children of this layer will be ignored after logging a
     * warning.
     *
     * @throws LayerHierarchyException if adding one of the passed layers creates loops in the layer hierarchy
     * @throws LayerHierarchyException if one of the passed layers already had a parent that wasn't this layer
     * @throws LayerHierarchyException if one of the passed layers returns false when this layer is passed to its
     * [canAddToParent] method.
     */
    fun add(vararg layers: GuiLayer) {
        for(component in layers) {
            if (component === this)
                throw LayerHierarchyException("Tried to add a layer to itself")

            if (component.parent != null) {
                if (component.parent == this) {
                    logger.warn("The passed layer was already a child of this layer", Exception())
                    return
                } else {
                    throw LayerHierarchyException("The passed layer already had another parent")
                }
            }

            if (component in parents) {
                throw LayerHierarchyException("Recursive layer hierarchy, the passed layer is an ancestor of this layer")
            }

            if (this.BUS.fire(GuiLayerEvents.AddChildEvent(component)).isCanceled())
                return
            component.BUS.fire(GuiLayerEvents.AddToParentEvent(this))
            _children.add(component)
            component.parent = this
        }
    }

    /**
     * Checks whether this layer has the passed layer as a descendent
     */
    operator fun contains(layer: GuiLayer): Boolean =
        layer in children || children.any { layer in it }

    /**
     * Removes the passed layer from this layer's children. If the passed layer has no parent this will log an error
     * and return immediately.
     *
     * @throws LayerHierarchyException if the passed layer has a parent that isn't this layer
     */
    fun remove(layer: GuiLayer) {
        if(layer.parent == null) {
            logger.warn("The passed layer has no parent", Exception())
            return
        } else if (layer.parent != this) {
            throw LayerHierarchyException("This isn't the layer's parent")
        }

        if (this.BUS.fire(GuiLayerEvents.RemoveChildEvent(layer)).isCanceled())
            return
        layer.BUS.fire(GuiLayerEvents.RemoveFromParentEvent(this))
        layer.parent = null
        _children.remove(layer)
    }

    /**
     * Removes the layer from its parent if it has one. Shorthand for `this.parent?.remove(this)`
     */
    fun removeFromParent() {
        this.parent?.remove(this)
    }

    /**
     * The sort index and render order for the layer. Use [GuiLayer.OVERLAY_Z] and [GuiLayer.UNDERLAY_Z] to create
     * layers that appear on top or below _literally everything else._ In order to maintain this property, please
     * limit your z index offsets to ±1,000,000. That should be more than enough.
     *
     * Drives [zIndex]
     */
    val zIndex_rm = RMValueDouble(1.0)
    /**
     * The sort index and render order for the layer. Use [GuiLayer.OVERLAY_Z] and [GuiLayer.UNDERLAY_Z] to create
     * layers that appear on top or below _literally everything else._ In order to maintain this property, please
     * limit your z index offsets to ±1,000,000. That should be more than enough.
     *
     * Driven by [zIndex_rm]
     */
    var zIndex by zIndex_rm

    /**
     * Iterates over children while allowing children to be added or removed. Any added children will not be iterated,
     * and any children removed while iterating will be excluded.
     */
    fun forEachChild(l: (GuiLayer) -> Unit) {
        children.toList().asSequence().filter {
            it.parent != null // a component may have been removed, in which case it won't be expecting any interaction
        }.forEach(l)
    }


    //endregion

    //region LayerGeometryHandler
    /**
     * The bounding rectangle of this layer in its parent's coordinate space. The "outer" edge. Setting this value will
     * not respect rotation.
     */
    var frame: Rect2d
        get() = parentSpace?.let { this.convertRectTo(bounds, it) } ?: bounds
        set(value) {
            val current = this.frame
            if(value.size == current.size) {
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
    val bounds: Rect2d
        get() = Rect2d(vec(0, 0), size)

    /**
     * The size of the layer in its own coordinate space
     */
    val size_rm: RMValue<Vec2d> = rmValue(vec(width, height)) { old, new ->
        if(old != new) {
            matrixDirty = true
        }
    }
    /**
     * The size of the layer in its own coordinate space
     */
    var size: Vec2d by size_rm

    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    val pos_rm: RMValue<Vec2d> = rmValue(vec(posX, posY)) { old, new ->
        if(old != new) {
            matrixDirty = true
        }
    }
    /**
     * The position of the layer's anchor point in its parent's coordinate space.
     */
    var pos: Vec2d by pos_rm

    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    val scale_rm: RMValue<Vec2d> = rmValue(vec(1, 1)) { old, new ->
        if(old != new) {
            matrixDirty = true
        }
    }
    /**
     * The layer's scaling factor about the anchor.
     * A scale of 0 on either axis will make the inverse scale on that axis +Infinity.
     */
    var scale2d: Vec2d by scale_rm
    /**
     * The average scale between the X and Y axes. Setting this value sets both the X and Y scales to this value.
     */
    var scale: Double
        get() = (scale2d.x + scale2d.y) / 2
        set(value) { scale2d = vec(value, value) }

    /**
     * The clockwise rotation in radians about the anchor.
     */
    val rotation_rm: RMValueDouble = RMValueDouble(0.0) { old, new ->
        if(old != new) {
            matrixDirty = true
        }
    }
    /**
     * The clockwise rotation in radians about the anchor.
     */
    var rotation: Double by rotation_rm

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
    val anchor_rm: RMValue<Vec2d> = rmValue(Vec2d.ZERO) { old, new ->
        if(old != new) {
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
    var anchor: Vec2d by anchor_rm


    /**
     * The width of this layer as a double.
     *
     * Shorthand for `layer.size.x`
     *
     * @see widthf
     * @see widthi
     */
    var width: Double
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
    var widthf: Float
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
    var widthi: Int
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
    var height: Double
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
    var heightf: Float
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
    var heighti: Int
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
    var x: Double
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
    var xf: Float
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
    var xi: Int
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
    var y: Double
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
    var yf: Float
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
    var yi: Int
        get() = pos.yi
        set(value) {
            pos = vec(pos.x, value)
        }

    /**
     * Returns true if the passed point is inside this component, ignoring any clipping.
     */
    open fun isPointInBounds(point: Vec2d): Boolean {
        return point in bounds
    }

    /**
     * Returns true if the passed point is outside this component's clipping mask.
     */
    @Suppress("UNUSED_PARAMETER")
    fun isPointClipped(point: Vec2d): Boolean {
        return false //TODO
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
    fun getContentsBounds(
        includeOwnBounds: (layer: GuiLayer) -> Boolean,
        includeChildren: (layer: GuiLayer) -> Boolean
    ): Rect2d? {
        var bounds: Rect2d? = null
        if (includeOwnBounds(this)) {
            bounds = this.bounds
        }
        if (includeChildren(this)) {
            for (child in children) {
                val subBounds = child.getContentsBounds(includeOwnBounds, includeChildren) ?: continue
                val subFrame = child.convertRectToParent(subBounds)
                bounds = bounds?.expandToFit(subFrame) ?: subFrame
            }
        }
        return bounds
    }

    override val parentSpace: CoordinateSpace2D?
        get() = parent

    private var matrixDirty = true
    private var _matrix = MutableMatrix3d()
    override val transform: Matrix3d = Matrix3dView(_matrix)
        get() {
            if(matrixDirty) {
                updateMatrix()
            }
            return field
        }

    private var _inverseMatrix = MutableMatrix3d()
    override val inverseTransform: Matrix3d = Matrix3dView(_inverseMatrix)
        get() {
            if(matrixDirty) {
                updateMatrix()
            }
            return field
        }

    private fun updateMatrix() {
        val inverseScale = vec(
            if(scale2d.x == 0.0) Double.POSITIVE_INFINITY else 1.0/scale2d.x,
            if(scale2d.y == 0.0) Double.POSITIVE_INFINITY else 1.0/scale2d.y
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

    val tooltip_im: IMValue<List<String>?> = IMValue()

    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    val opacity_rm: RMValueDouble = RMValueDouble(1.0)
    /**
     * An opacity value in the range [0, 1]. If this is not equal to 1 the layer will be rendered to an FBO and drawn
     * to a texture. This process clips the layer to its bounds.
     */
    var opacity: Double by opacity_rm
    /**
     * How to apply [MaskLayer] masks (docs todo)
     */
    var maskMode: MaskMode = MaskMode.NONE
    /**
     * What technique to use to render this layer
     */
    var renderMode: RenderMode = RenderMode.DIRECT
    /**
     * What scaling factor to use when rasterizing this layer using [RenderMode.RENDER_TO_QUAD]
     */
    var rasterizationScale: Int = 1
    /**
     * A filter to apply to this layer. If this is not null this layer will be drawn to a texture and passed to the
     * filter.
     */
    var layerFilter: GuiLayerFilter? = null

    fun actualRenderMode(): RenderMode {
        if(renderMode != RenderMode.DIRECT)
            return renderMode
        if(opacity < 1.0 || maskMode != MaskMode.NONE || layerFilter != null)
            return RenderMode.RENDER_TO_FBO
        return RenderMode.DIRECT
    }

    /**
     * Renders this layer and its sublayers. This method handles the internals of rendering a layer, to simply render
     * content in a layer use [GuiLayer.draw]
     */
    fun renderLayer(context: GuiDrawContext) {
        context.matrix.push()
        context.matrix *= transform

        if(!isVisible) {
            renderSkeleton(context)
            context.matrix.pop()
            return
        }

//        pushClipping(context)

        val renderMode = actualRenderMode()
        if(renderMode != RenderMode.DIRECT) {
            TODO("Waiting on shaders")
            /*
            var maskFBO: Framebuffer? = null
            var layerFBO: Framebuffer? = null
            try {
                 layerFBO = GuiLayerFilter.useFramebuffer(renderMode == RenderMode.RENDER_TO_QUAD, max(1, rasterizationScale)) {
                    drawContent(partialTicks) {
                        // draw all the non-mask children to the current FBO (layerFBO)
                        layer.forEachChild {
                            if (it !is MaskLayer)
                                it.renderLayer(partialTicks)
                        }

                        // draw all the mask children to an FBO, if needed
                        if (maskMode != MaskMode.NONE)
                            maskFBO = GuiLayerFilter.useFramebuffer(false, 1) {
                                layer.forEachChild {
                                    if (it is MaskLayer)
                                        it.renderLayer(partialTicks)
                                }
                            }
                    }
                }

                layerFilter?.filter(this.layer, layerFBO, maskFBO)

                // load the shader
                LayerToTextureShader.alphaMultiply = opacity.toFloat()
                LayerToTextureShader.maskMode = maskMode
                LayerToTextureShader.renderMode = renderMode
                ShaderHelper.useShader(LayerToTextureShader)

                LayerToTextureShader.bindTextures(layerFBO.framebufferTexture, maskFBO?.framebufferTexture)

                val size = layer.size
                val maxU = (size.x * rasterizationScale) / Client.minecraft.displayWidth
                val maxV = (size.y * rasterizationScale) / Client.minecraft.displayHeight

                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                vb.pos(0.0, size.y, 0.0).tex(0.0, 1.0 - maxV).endVertex()
                vb.pos(size.x, size.y, 0.0).tex(maxU, 1.0 - maxV).endVertex()
                vb.pos(size.x, 0.0, 0.0).tex(maxU, 1.0).endVertex()
                vb.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
                tessellator.draw()

                ShaderHelper.releaseShader()
            } finally {
                layerFBO?.also { GuiLayerFilter.releaseFramebuffer(it) }
                maskFBO?.also { GuiLayerFilter.releaseFramebuffer(it) }
            }
            */
        } else {
            context.matrix.assertEvenDepth {
                glStateGuarantees()
                context.matrix.push()
                context.matrix.assertEvenDepth {
                    draw(context)
                }
                context.popGlMatrix()
                context.matrix.pop()
            }
            forEachChild {
//                if(it !is MaskLayer) // TODO: masking
                it.renderLayer(context)
            }
        }

//        popClipping(context)

        if (context.showDebugBoundingBox) {
            RenderSystem.lineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            RenderSystem.color4f(.75f, 0f, .75f, 1f)
            drawDebugBoundingBox(context)
        }
//        if (GuiComponent.showLayoutOverlay && didLayout && !isInMask) {
//            RenderSystem.color4f(1f, 0f, 0f, 0.1f)
//            drawLayerOverlay(context)
//        }
//        didLayout = false

        context.matrix.pop()
    }

    fun shouldDrawSkeleton(): Boolean = false

    fun renderSkeleton(context: GuiDrawContext) {
        forEachChild { it.renderSkeleton(context) }

        if (context.showDebugBoundingBox && //!isInMask &&
            GuiLayer.showDebugTilt && shouldDrawSkeleton()) {
            RenderSystem.lineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            RenderSystem.color4f(.75f, 0f, .75f, 1f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            drawDebugBoundingBox(context)
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }
    }

    /**
     * Draws a flat colored box over this layer, rounding corners as necessary
     */
    @Suppress("UNUSED_PARAMETER") // TODO: update to use matrices
    fun drawLayerOverlay(context: GuiDrawContext) {
        RenderSystem.disableTexture()
        val points = createDebugBoundingBoxPoints()
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)
        vb.pos(size.x/2, size.y/2, 0.0).endVertex()
        points.reversed().forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
    }

    /**
     * Draws a bounding box around the edge of this component
     */
    @Suppress("UNUSED_PARAMETER") // TODO: update to use matrices
    fun drawDebugBoundingBox(context: GuiDrawContext) {
        RenderSystem.disableTexture()
        val points = createDebugBoundingBoxPoints()
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        points.forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
        RenderSystem.color4f(0f, 0f, 0f, 0.15f)
        if(GuiLayer.showDebugTilt) {
            vb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
            points.forEach {
                vb.pos(it.x, it.y, -100.0).endVertex()
                vb.pos(it.x, it.y, 0.0).endVertex()
            }
            tessellator.draw()
        }
    }

    val cornerRadius = 0.0

    /**
     * Creates a series of points defining the path the debug bounding box follows. For culling reasons this list
     * must be in clockwise order
     */
    fun createDebugBoundingBoxPoints(): List<Vec2d> {
        val list = mutableListOf<Vec2d>()
        if(/*clipToBounds &&*/ cornerRadius != 0.0) {
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
    val BUS = EventBus()

    inline fun <reified  E : Event> hook(noinline hook: (E) -> Unit) = BUS.hook(hook)

    fun <E : Event> hook(clazz: Class<E>, hook: (E) -> Unit) = BUS.hook(clazz, hook)

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> hook(clazz: Class<E>, hook: Consumer<E>) = BUS.hook(clazz, hook)

    init {
        BUS.register(this)
    }

    //endregion

    //region Input

    /**
     * If [interactive] is false, this component and its descendents won't be considered for mouseover calculations
     * and won't receive input events
     */
    var interactive: Boolean = true

    /**
     * If [ignoreMouseOverBounds] is true, this component's bounding box won't be taken into consideration for mouseover
     * calculations, however its children will be considered as usual.
     */
    var ignoreMouseOverBounds: Boolean = false

    /**
     * True if the current [mousePos] is inside the bounds of component. This ignores components that may be covering
     * this component.
     */
    var mouseInside: Boolean = false
        private set

    /**
     * True if this component is [interactive] and the mouse is hovering over it or one of its children.
     */
    var mouseOver: Boolean = false
        internal set

    /**
     * The mouse position within this component
     */
    var mousePos: Vec2d = vec(0, 0)
        private set

    /**
     * Computes the mouse position, resets the `mouseOver` flag, and returns the component with the mouse over it, if
     * any.
     */
    internal fun computeMouseInfo(rootPos: Vec2d, stack: Matrix3dStack): GuiLayer? {
        stack.push()
        stack.reverseMul(inverseTransform)
        mousePos = stack.transform(rootPos)
        mouseInside = isPointInBounds(mousePos) && !isPointClipped(mousePos)
        mouseOver = false
        var mouseOverChild: GuiLayer? = null
        forEachChild { child ->
            val childMouseOver = child.computeMouseInfo(rootPos, stack)
            mouseOverChild = mouseOverChild ?: childMouseOver
        }
        stack.pop()
        if(!interactive)
            return null
        return when {
            mouseOverChild != null -> mouseOverChild
            mouseInside && !ignoreMouseOverBounds -> this
            else -> null
        }
    }

    internal fun triggerEvent(event: Event) {
        when(event) {
            is GuiLayerEvents.MouseEvent -> {
                if(!interactive)
                    return
                event.stack.push()
                event.stack.reverseMul(inverseTransform)
                BUS.fire(event)
                this.forEachChild {
                    it.triggerEvent(event)
                }
                event.stack.pop()
            }
            is GuiLayerEvents.KeyEvent -> {
                if(!interactive)
                    return
                BUS.fire(event)
                this.forEachChild {
                    it.triggerEvent(event)
                }
            }
            is GuiLayerEvents.Update -> {
                this.update()
                BUS.fire(event)
                this.forEachChild {
                    it.triggerEvent(event)
                }
            }
            else -> {
                BUS.fire(event)
                this.forEachChild {
                    it.triggerEvent(event)
                }
            }
        }
    }

    //endregion

    companion object {
        @JvmStatic
        var showDebugTilt = false

        @JvmStatic
        var showDebugBoundingBox = false

        @JvmStatic
        var showLayoutOverlay = false

        /**
         * The z index of tooltips. Overlays should not go above this level.
         */
        @JvmStatic
        val TOOLTIP_Z: Double = 1e11

        /**
         * In order to make an overlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        val OVERLAY_Z: Double = 1e10

        /**
         * In order to make a background layer, give the layer this [zIndex].
         */
        @JvmStatic
        val DIALOG_Z: Double = 1e9

        /**
         * In order to make a background layer, give the layer this [zIndex].
         */
        @JvmStatic
        val BACKGROUND_Z: Double = -1e9

        /**
         * In order to make an underlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        val UNDERLAY_Z: Double = -1e10

        @JvmStatic
        var overrideDebugLineWidth: Float? = null

        internal fun glStateGuarantees() {
            RenderSystem.enableTexture()
            RenderSystem.color4f(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            RenderSystem.shadeModel(GL11.GL_SMOOTH)
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.alphaFunc(GL11.GL_GREATER, 1/255f)
            RenderSystem.disableLighting()
        }
    }
}
