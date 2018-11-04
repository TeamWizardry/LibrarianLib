package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.LayerBackedComponent
import com.teamwizardry.librarianlib.features.gui.value.IMValueBoolean

interface ILayerBase {
    val isVisible_im: IMValueBoolean
    /**
     * Whether this component should be drawn or have events fire
     */
    var isVisible: Boolean
    /**
     * Returns true if the component is in need of a layout update. Defaults to true upon layer creation
     */
    var needsLayout: Boolean
    /**
     * Returns true if this component is invalid and it should be removed from its parent
     */
    var isInvalid: Boolean

    /**
     * Called immediately before any layers are rendered or sorted by zIndex and after [mouseOver] has been updated.
     */
    fun preFrame()

    /**
     * Calls [preFrame] on this layer, fires a [GuiLayerEvents.PreFrameEvent], then calls [callPreFrame] on each
     * child layer.
     */
    fun callPreFrame()

    /**
     * Draws the component, this is called between pre and post draw events.
     *
     * The only guranteed GL state when this method is called is the following:
     *
     * - GL_TEXTURE_2D - enabled
     * - GL_COLOR - (1, 1, 1, 1)
     */
    fun draw(partialTicks: Float)

    /**
     * Called to lay out the children of this layer. Unless overridden this is a no-op.
     *
     * This method is called before each frame if this component's bounds have changed, children are added/removed,
     * or [setNeedsLayout] has been called. After this method completes, the children of this component will be
     * checked for layout. This means that changes made in one layer can ripple downward. [needsLayout] is reset
     * to `false` after this method is called, so any changes inside it will not cause the layout to be recalculated
     * every frame.
     *
     * The idea behind this method is that self-contained components/layers can lay out their children dynamically
     * themselves. Examples of such a component would be a self-contained list item, a component that spaces out its
     * children equally along its length, or simply a component that needs to resize its background layer
     * to fit its dimensions.
     */
    fun layoutChildren()

    /**
     * Calls [layoutChildren] if [needsLayout] is true, then calls [runLayoutIfNeeded] on this layer's children
     * regardless of [needsLayout]'s value. [needsLayout] is reset to false after [layoutChildren] completes,
     * meaning size changes in that method won't cause a layout pass every frame.
     */
    fun runLayoutIfNeeded()

    /**
     * Calls [layoutChildren] then calls [runLayout] on this layer's children. [needsLayout] is reset to false
     * after [layoutChildren] completes, meaning size changes in that method won't cause a layout pass every frame.
     */
    fun runLayout()

    /**
     * Marks this component to be laid out using [layoutChildren] before the next frame.
     */
    fun setNeedsLayout()

    fun clearInvalid()
    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    fun invalidate()
}

internal class LayerBaseHandler: ILayerBase {
    lateinit var layer: GuiLayer

    /**
     * Called immediately before any layers are rendered or sorted by zIndex and after [mouseOver] has been updated.
     */
    override fun preFrame() {

    }

    /**
     * Calls [preFrame] on this layer, fires a [GuiLayerEvents.PreFrameEvent], then calls [callPreFrame] on each
     * child layer.
     */
    override fun callPreFrame() {
        layer.preFrame()
        layer.BUS.fire(GuiLayerEvents.PreFrameEvent())
        layer.children.forEach { it.callPreFrame() }
    }

    /**
     * Draws the component, this is called between pre and post draw events.
     *
     * The only guranteed GL state when this method is called is the following:
     *
     * - GL_TEXTURE_2D - enabled
     * - GL_COLOR - (1, 1, 1, 1)
     */
    override fun draw(partialTicks: Float) {}

    /**
     * Called to lay out the children of this layer. Unless overridden this is a no-op.
     *
     * This method is called before each frame if this component's bounds have changed, children are added/removed,
     * or [setNeedsLayout] has been called. After this method completes, the children of this component will be
     * checked for layout. This means that changes made in one layer can ripple downward. [needsLayout] is reset
     * to `false` after this method is called, so any changes inside it will not cause the layout to be recalculated
     * every frame.
     *
     * The idea behind this method is that self-contained components/layers can lay out their children dynamically
     * themselves. Examples of such a component would be a self-contained list item, a component that spaces out its
     * children equally along its length, or simply a component that needs to resize its background layer
     * to fit its dimensions.
     */
    override fun layoutChildren() {}

    /**
     * Calls [layoutChildren] if [needsLayout] is true, then calls [runLayoutIfNeeded] on this layer's children
     * regardless of [needsLayout]'s value. [needsLayout] is reset to false after [layoutChildren] completes,
     * meaning size changes in that method won't cause a layout pass every frame.
     */
    override fun runLayoutIfNeeded() {
        if(needsLayout) {
            layer.layoutChildren()
            layer.BUS.fire(GuiLayerEvents.LayoutChildren())
        }
        layer.children.forEach { it.runLayoutIfNeeded() }
        layer.needsLayout = false
    }

    /**
     * Calls [layoutChildren] then calls [runLayout] on this layer's children. [needsLayout] is reset to false
     * after [layoutChildren] completes, meaning size changes in that method won't cause a layout pass every frame.
     */
    override fun runLayout() {
        layer.layoutChildren()
        layer.BUS.fire(GuiLayerEvents.LayoutChildren())
        layer.children.forEach { it.runLayout() }
        layer.needsLayout = false
    }

    //region - Base component stuff

    override val isVisible_im: IMValueBoolean = IMValueBoolean(true)
    /**
     * Whether this component should be drawn or have events fire
     */
    override var isVisible by isVisible_im

    /**
     * Returns true if the component is in need of a layout update. Defaults to true upon layer creation
     */
    override var needsLayout: Boolean = true

    /**
     * Marks this component to be laid out using [layoutChildren] before the next frame.
     */
    override fun setNeedsLayout() {
        layer.needsLayout = true
    }

    /**
     * Returns true if this component is invalid and it should be removed from its parent
     */
    override var isInvalid = false

    @Deprecated("Directly set isInvalid", replaceWith = ReplaceWith("isInvalid = false"))
    override fun clearInvalid() { layer.isInvalid = false }

    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    @Deprecated("Directly set isInvalid", replaceWith = ReplaceWith("isInvalid = true"))
    override fun invalidate() {
        layer.isInvalid = true
    }
    //endregion
}