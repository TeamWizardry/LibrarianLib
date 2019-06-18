package com.teamwizardry.librarianlib.features.facade.component.supporting

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.value.IMValueBoolean

/**
 *
 */
interface ILayerBase {
    val isVisible_im: IMValueBoolean
    /**
     * Whether this component should be drawn. For [components][GuiComponent] this also disables input events.
     *
     * Driven by [isVisible_im]
     */
    var isVisible: Boolean

    /**
     * Whether this layer is in need of a layout update. Defaults to true upon layer creation. It can and should be
     * set using the zero-argument [setNeedsLayout] unless there is a good reason not to as it won't clobber existing
     * values and is clearer to read.
     */
    var needsLayout: Boolean

    /**
     * Used internally to propagate pre-frame events
     */
    fun callPreFrame()

    /**
     * Draws the layer's contents. This is the method to override when creating custom layer rendering
     *
     * The guaranteed GL states for this method are defined in the listed "sample" method
     *
     * @sample ILayerRendering.glStateGuarantees
     */
    fun draw(partialTicks: Float)

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
    fun layoutChildren()

    /**
     * Runs the layout process on this layer and its children if their [needsLayout] flag is true.
     */
    fun runLayoutIfNeeded()

    /**
     * Runs the layout process on this layer and its children regardless of their [needsLayout] status.
     */
    fun runLayout()

    /**
     * Marks this component to be laid out using [layoutChildren] before the next frame.
     */
    fun setNeedsLayout()
}

internal class LayerBaseHandler: ILayerBase {
    lateinit var layer: GuiLayer

    override val isVisible_im: IMValueBoolean = IMValueBoolean(true)
    override var isVisible by isVisible_im

    override var needsLayout: Boolean = true

    override fun callPreFrame() {
        layer.BUS.fire(GuiLayerEvents.PreFrameEvent())
        layer.children.forEach { it.callPreFrame() }
    }

    override fun draw(partialTicks: Float) {}

    override fun layoutChildren() {}

    override fun runLayoutIfNeeded() {
        if(needsLayout) {
            layer.layoutChildren()
            layer.BUS.fire(GuiLayerEvents.LayoutChildren())
        }
        layer.children.forEach { it.runLayoutIfNeeded() }
        layer.needsLayout = false
    }

    override fun runLayout() {
        layer.layoutChildren()
        layer.BUS.fire(GuiLayerEvents.LayoutChildren())
        layer.children.forEach { it.runLayout() }
        layer.needsLayout = false
    }

    override fun setNeedsLayout() {
        layer.needsLayout = true
    }
}