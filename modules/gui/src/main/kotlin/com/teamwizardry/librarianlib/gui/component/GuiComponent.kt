package com.teamwizardry.librarianlib.gui.component

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.gui.component.supporting.*
import com.teamwizardry.librarianlib.gui.logger
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.IMValueBoolean
import com.teamwizardry.librarianlib.gui.value.RMValue
import com.teamwizardry.librarianlib.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.math.CoordinateSpace2D
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dView
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.fastCos
import com.teamwizardry.librarianlib.math.fastSin
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.utilities.eventbus.Event
import com.teamwizardry.librarianlib.utilities.eventbus.EventBus
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.util.ConcurrentModificationException
import java.util.function.Consumer
import kotlin.math.PI

/**
 * The fundamental building block of a LibrarianLib GUI. Generally a single unit of visual or organizational design.
 *
 * **Origins:**
 *
 * Vanilla GUIs very basic, where each frame you personally draw each texture and have to manually handle positioning
 * as the layout changes (though most likely it won't, because the math quickly becomes a pain). Vanilla does have the
 * [GuiButton][net.minecraft.client.gui.GuiButton], which isn't drawn or processed by you, however LibrarianLib's
 * layers and [components][GuiComponent] blow GuiButton clean out of the water with their versatility and ability to
 * easily create highly complex and dynamic interfaces.
 *
 * Over its evolution the GUI framework has been influenced largely by two things. It started out mostly inspired by
 * HTML's hierarchical nature, then later in its life it started to acquire many of the traits and structures from
 * Cocoa Touch. If you are familiar Cocoa Touch, many these concepts will be familiar to you, with layers and
 * components being CGLayer and UIView respectively.
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
 *
 * Note: The implementations of various responsibilities are in separate classes and implemented by GuiLayer via delegation.
 * This allows a large API to be accessible and overridable directly on the layer, while also allowing the various
 * responsibilities to stay separate.
 *
 * @see ILayerGeometry
 * @see ILayerRelationships
 * @see ILayerRendering
 * @see ILayerClipping
 * @See ILayerBase
 */
open class GuiComponent(posX: Int, posY: Int, width: Int, height: Int): CoordinateSpace2D {
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
     * Draws the layer's contents. This is the method to override when creating custom layer rendering
     *
     * The guaranteed GL states for this method are defined in the listed "sample" method
     *
     * @sample ILayerRendering.glStateGuarantees
     */
    open fun draw(context: GuiDrawContext) {

    }

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

    internal fun callUpdate() {
        update()
        BUS.fire(GuiComponentEvents.Update())
        forEachChild { it.callUpdate() }
    }
    //endregion

    //region LayerRelationshipHandler
    private val _children = mutableListOf<GuiComponent>()
    /**
     * A read-only list containing all the children of this layer. For safely iterating over this list use
     * [forEachChild] as it will prevent [ConcurrentModificationException]s and prevent crashes caused by removing
     * children while iterating.
     */
    val children: List<GuiComponent> = _children.unmodifiableView()

    /**
     * The immediate parent of this layer, or null if this layer has no parent.
     */
    var parent: GuiComponent? = null
        private set

    private val _parents = mutableSetOf<GuiComponent>()
    /**
     * A read-only set containing all the parents of this layer, recursively.
     */
    val parents: Set<GuiComponent> = _parents.unmodifiableView()

    /**
     * The root of this component's hierarchy. i.e. the last layer found when iterating back through the parents. If this
     * component has no parent, returns this component.
     */
    val root: GuiComponent
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
    fun add(vararg components: GuiComponent) {
        for(component in components) {
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

            if (this.BUS.fire(GuiComponentEvents.AddChildEvent(component)).isCanceled())
                return
            if (component.BUS.fire(GuiComponentEvents.AddToParentEvent(this)).isCanceled())
                return
            _children.add(component)
            component.parent = this
        }
    }

    /**
     * Checks whether this layer has the passed layer as a descendent
     */
    operator fun contains(component: GuiComponent): Boolean =
        component in children || children.any { component in it }

    /**
     * Removes the passed layer from this layer's children. If the passed layer has no parent this will log an error
     * and return immediately.
     *
     * @throws LayerHierarchyException if the passed layer has a parent that isn't this layer
     */
    fun remove(component: GuiComponent) {
        if(component.parent == null) {
            logger.warn("The passed layer has no parent", Exception())
            return
        } else if (component.parent != this) {
            throw LayerHierarchyException("This isn't the layer's parent")
        }

        if (this.BUS.fire(GuiComponentEvents.RemoveChildEvent(component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(this)).isCanceled())
            return
        component.parent = null
        _children.remove(component)
    }

    /**
     * Removes the layer from its parent if it has one. Shorthand for `this.parent?.remove(this)`
     */
    fun removeFromParent() {
        this.parent?.remove(this)
    }

    /**
     * The sort index and render order for the layer. Use [GuiComponent.OVERLAY_Z] and [GuiComponent.UNDERLAY_Z] to create
     * layers that appear on top or below _literally everything else._ In order to maintain this property, please
     * limit your z index offsets to ±1,000,000. That should be more than enough.
     *
     * Drives [zIndex]
     */
    val zIndex_rm = RMValueDouble(1.0)
    /**
     * The sort index and render order for the layer. Use [GuiComponent.OVERLAY_Z] and [GuiComponent.UNDERLAY_Z] to create
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
    fun forEachChild(l: (GuiComponent) -> Unit) {
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
     * The bounding rectangle of this layer in its own coordinate space. The "inner" edge. Takes into account
     * [contentsOffset], so the rectangle's position may not be the origin
     */
    val bounds: Rect2d
        get() = Rect2d(vec(0, 0), size)

    /**
     * The size of the layer in its own coordinate space
     */
    val size_rm: RMValue<Vec2d> = RMValue(vec(width, height)) { old, new ->
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
    val pos_rm: RMValue<Vec2d> = RMValue(vec(posX, posY)) { old, new ->
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
    val scale_rm: RMValue<Vec2d> = RMValue(vec(1, 1)) { old, new ->
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
    val anchor_rm: RMValue<Vec2d> = RMValue(Vec2d.ZERO) { old, new ->
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
     * Returns true if the passed point is inside the bounds of this component. Testing for [GuiComponent.isPointClipped] in
     * this method's implementation is recommended in order to maintain normal clipping behavior.
     */
    fun isPointInBounds(point: Vec2d): Boolean {
        return point in bounds && !isPointClipped(point)
    }

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
        includeOwnBounds: (component: GuiComponent) -> Boolean,
        includeChildren: (component: GuiComponent) -> Boolean
    ): Rect2d? {
        var bounds: Rect2d? = null
        if (includeOwnBounds(this)) {
            bounds = bounds
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
    override val matrix: Matrix3d = Matrix3dView(_matrix)
        get() {
            if(matrixDirty) {
                updateMatrix()
            }
            return field
        }

    private var _inverseMatrix = MutableMatrix3d()
    override val inverseMatrix: Matrix3d = Matrix3dView(_inverseMatrix)
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
     * content in a layer use [GuiComponent.draw]
     */
    fun renderLayer(context: GuiDrawContext) {
        context.matrix.push()
        context.matrix *= matrix

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
                draw(context)
            }
            forEachChild {
//                if(it !is MaskLayer) // TODO: masking
                it.renderLayer(context)
            }
        }

//        popClipping(context)

        if (context.showDebugBoundingBox) {
            GlStateManager.lineWidth(GuiComponent.overrideDebugLineWidth ?: 1f)
            GlStateManager.color4f(.75f, 0f, .75f, 1f)
            drawDebugBoundingBox(context)
        }
//        if (GuiComponent.showLayoutOverlay && didLayout && !isInMask) {
//            GlStateManager.color4f(1f, 0f, 0f, 0.1f)
//            drawLayerOverlay(context)
//        }
//        didLayout = false

        context.matrix.pop()
    }

    fun shouldDrawSkeleton(): Boolean = false

    fun renderSkeleton(context: GuiDrawContext) {
        forEachChild { it.renderSkeleton(context) }

        if (context.showDebugBoundingBox && //!isInMask &&
            GuiComponent.showDebugTilt && shouldDrawSkeleton()) {
            GlStateManager.lineWidth(GuiComponent.overrideDebugLineWidth ?: 1f)
            GlStateManager.color4f(.75f, 0f, .75f, 1f)
            GL11.glEnable(GL11.GL_LINE_STIPPLE)
            GL11.glLineStipple(2, 0b0011_0011_0011_0011.toShort())
            drawDebugBoundingBox(context)
            GL11.glDisable(GL11.GL_LINE_STIPPLE)
        }
    }

    /**
     * Draws a flat colored box over this layer, rounding corners as necessary
     */
    fun drawLayerOverlay(context: GuiDrawContext) {
        GlStateManager.disableTexture()
        val points = createDebugBoundingBoxPoints(context)
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
    fun drawDebugBoundingBox(context: GuiDrawContext) {
        GlStateManager.disableTexture()
        val points = createDebugBoundingBoxPoints(context)
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        points.forEach { vb.pos(it.x, it.y, 0.0).endVertex() }
        tessellator.draw()
        GlStateManager.color4f(0f, 0f, 0f, 0.15f)
        if(GuiComponent.showDebugTilt) {
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
    fun createDebugBoundingBoxPoints(context: GuiDrawContext): List<Vec2d> {
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

    //region API

    /**
     * The event bus on which all events for this layer are fired.
     *
     * The built-in base events are located in [GuiComponentEvents]
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
    }
}
