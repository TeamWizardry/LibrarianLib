package com.teamwizardry.librarianlib.features.facade.component

import com.teamwizardry.librarianlib.features.facade.component.supporting.*
import com.teamwizardry.librarianlib.features.facade.components.RootComponent
import com.teamwizardry.librarianlib.features.facade.layers.ComponentBackedLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * The base class of every **interactive** on-screen object. Components behave identically to [GuiLayer] except that
 * they also handle user input, as opposed to being completely passive. Note that adding a [GuiComponent] as a child of
 * a [GuiLayer] is considered an error and an exception will be thrown
 *
 * All the events fired by [GuiLayer] are also fired by [GuiComponent], in addition to the events in
 * [GuiComponentEvents].
 */
@SideOnly(Side.CLIENT)
open class GuiComponent private constructor(
    posX: Int, posY: Int, width: Int, height: Int,
    internal val guiEventHandler: ComponentGuiEventHandler,
    internal val mouseHandler: ComponentMouseHandler,
    internal val focusHandler: ComponentFocusHandler,
    internal val tooltipHandler: ComponentTooltipHandler
) : GuiLayer(posX, posY, width, height),
    IComponentGuiEvent by guiEventHandler, IComponentMouse by mouseHandler,
    IComponentFocus by focusHandler, IComponentTooltip by tooltipHandler
{
    constructor(): this(0, 0, 0, 0)
    constructor(posX: Int, posY: Int): this(posX, posY, 0, 0)
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(
        posX, posY, width, height,
        ComponentGuiEventHandler(),
        ComponentMouseHandler(),
        ComponentFocusHandler(),
        ComponentTooltipHandler()
    )

    /**
     * Creates a component with the same frame as the given component that contains the given component. This
     * constructor resets the given component's position to (0, 0).
     */
    constructor(other: GuiLayer): this() {
        this.size = other.size
        this.pos = other.pos
        other.pos = vec(0, 0)
        this.add(other)
    }

    /**
     * An immutable copy of [children] that has been filtered to only include components.
     */
    open val subComponents: List<GuiComponent>
        get() = this.children.filterIsInstance<GuiComponent>()
    /**
     * The parent of this component as a [GuiComponent], or `null` if there isn't a parent or the parent isn't a
     * [GuiComponent] (as can happen with components created using [componentWrapper])
     */
    val parentComponent: GuiComponent?
        get() = super.parent as? GuiComponent?

    /**
     * The GUI this component is contained within, or the window if this component is in a [GuiWindow]
     */
    open val gui: RootComponent?
        get() = this.root as? RootComponent

    /**
     * When enabled, this component can be added as a child of a [GuiLayer] without an error being thrown.
     */
    var allowAddingToLayer = false
    private var wrapper: ComponentBackedLayer? = null

    override fun shouldDrawSkeleton(): Boolean = this.isPointInBounds(this.mousePos)

    override fun drawDebugBoundingBox() {

        GlStateManager.disableTexture2D()

        if(GuiLayer.showDebugTilt) {
            GlStateManager.glLineWidth(GuiLayer.overrideDebugLineWidth ?: 1f)
            GlStateManager.color(0f, 0f, 0f)
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
            if(GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                vb.pos(0.0, 0.0, 0.0).endVertex()
                vb.pos(mousePos.x, mousePos.y, 0.0).endVertex()
            }
            vb.pos(mousePos.x + 1.0, mousePos.y + 1.0, 0.0).endVertex()
            vb.pos(mousePos.x - 1.0, mousePos.y - 1.0, 0.0).endVertex()
            vb.pos(mousePos.x + 1.0, mousePos.y - 1.0, 0.0).endVertex()
            vb.pos(mousePos.x - 1.0, mousePos.y + 1.0, 0.0).endVertex()
            vb.pos(mousePos.x, mousePos.y, 0.0).endVertex()
            vb.pos(mousePos.x, mousePos.y, -100.0).endVertex()
            tessellator.draw()
        }

        GlStateManager.glLineWidth(overrideDebugLineWidth ?: 2f)
        GlStateManager.color(1f, 0f, 1f)
        if (this.mouseHit != null) GlStateManager.color(0.25f, 0.25f, 0.25f)
        if (mouseOver) GlStateManager.color(1f, 1f, 1f)

        super.drawDebugBoundingBox()
    }

    override fun canAddToParent(parent: GuiLayer): Boolean {
        return allowAddingToLayer || parent is GuiComponent
    }

    override fun debugInfo(): MutableList<String> {
        val list = super.debugInfo()
        addGuiComponentDebugInfo(list)
        return list
    }

    /**
     * Adds the base debug info for this component to the provided list. Used by [debugInfo]
     */
    fun addGuiComponentDebugInfo(list: MutableList<String>) {
        list.add("mouse: pos = $mousePos, hit = $mouseHit, over = $mouseOver, " +
            "pressed = ${pressedButtons.joinToString("+")}")
        list.add("mouse: opaque = $isOpaqueToMouse, propagate = $propagateMouse, disable = $disableMouseCollision")
        if(tagList.isNotEmpty())
            list.add("tags: [${tagList.joinToString(",")}]")
        val dataClasses = getAllDataClasses()
        if(dataClasses.isNotEmpty()) {
            list.add("data: {")
            dataClasses.forEach { clazz ->
                val keys = getAllDataKeys(clazz).toList()
                if(keys.size == 1) {
                    list.add("    :   ${clazz.simpleName}: ${keys[0]} = ${getData(clazz, keys[0])}")
                } else {
                    keys.forEach { key ->
                        list.add("    :   ${clazz.simpleName}:")
                        list.add("    :     $key = ${getData(clazz, key)}")
                    }
                }
            }
            list.add("data: }")
        }
    }

    /**
     * Wraps this component in a layer, effectively removing it from UI calculations. This can be helpful for
     * compatibility to convert existing rendering-only components to layers or for when a more complex component
     * is going to be used purely for visual appearance.
     */
    open fun layerWrapper(): GuiLayer {
        val wrapper = this.wrapper ?: ComponentBackedLayer(this)
        this.wrapper = wrapper
        return wrapper
    }

    init {
        @Suppress("LeakingThis")
        {
            guiEventHandler.component = this
            mouseHandler.component = this
            focusHandler.component = this
            tooltipHandler.component = this
        }()
    }

    companion object {
        var overrideDebugLineWidth: Float? = null
    }
}
