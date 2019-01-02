package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.getValue
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.setValue
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.Minecraft

open class PastryWindow(width: Int, height: Int): PastryWindowBase(width, height) {
    val header = GuiComponent(0, 0, width, 0)
    val content = GuiComponent(0, 15, width, height)

    private var closeButton: SpriteLayer = SpriteLayer(null, 0, 0, 0, 0)
    private var minimizeButton: SpriteLayer = SpriteLayer(null, 0, 0, 0, 0)
    private var maximizeButton: SpriteLayer = SpriteLayer(null, 0, 0, 0, 0)
    private var headerBackground: SpriteLayer = SpriteLayer(null, 0, 0, 0, 0)
    private var bodyBackground: SpriteLayer = SpriteLayer(null, 0, 0, 0, 0)
    private var headerClickRegion: GuiComponent = GuiComponent(0, 0, 0, 0)
    private var titleText: TextLayer = TextLayer(0, 0, 0, 0)

    var title: String by titleText::text

    var style: Style = Style.DEFAULT
        set(value) {
            field = value
            setNeedsLayout()
        }
    var useShortHeader: Boolean = false
        set(value) {
            field = value
            setNeedsLayout()
        }
    private val shortHeaderHeight = 10
    private val tallHeaderHeight = 14

    private var draggingSide: Align2d? = null

    /**
     * The size of the edge for resizing. negative values are outside, positive inside.
     */
    private var edgeRange = -2.0 .. 1.0
    /**
     * The size of the corner for resizing in taxicab distance
     */
    private var cornerSize: Int = 5

    override var cursor: LibCursor?
        get() = (draggingSide ?: getMouseEdge(mousePos))?.let { frameCursor(it) }
        set(value) {}

    init {
        header.clipToBounds = true
        content.clipToBounds = true

        header.isOpaqueToMouse = false
        add(
            headerBackground, bodyBackground, headerClickRegion, titleText,
            header, content,
            closeButton.componentWrapper(), minimizeButton.componentWrapper(), maximizeButton.componentWrapper()
        )
        closeButton.componentWrapper().cursor = LibCursor.POINT
        minimizeButton.componentWrapper().cursor = LibCursor.POINT
        maximizeButton.componentWrapper().cursor = LibCursor.POINT

        titleText.align = Align2d.CENTER_TOP
        addDragHooks(headerClickRegion)

        var draggingButton: EnumMouseButton? = null

        BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            val edge = getMouseEdge(mousePos)
            if (draggingButton == null && draggingSide == null && edge != null) {
                draggingButton = event.button
                draggingSide = edge
                beginFrameDragOperation(edge)
                event.cancel()
            }
        }
        BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (draggingButton == event.button && draggingButton != null) {
                draggingButton = null
                draggingSide = null
                endFrameDragOperation()
                event.cancel()
            }
        }
        closeButton.BUS.hook<GuiComponentEvents.MouseClickEvent> { event ->
            this.close()
        }
    }

    override fun isPointInBounds(point: Vec2d): Boolean {
        return super.isPointInBounds(point) || draggingSide != null || getMouseEdge(point) != null
    }

    override fun layoutChildren() {
        val headerHeight = if(useShortHeader) shortHeaderHeight else tallHeaderHeight
        headerBackground.sprite = style.header
        bodyBackground.sprite = style.body

        headerBackground.size = vec(this.width, headerHeight)
        bodyBackground.pos = vec(0, headerHeight)
        bodyBackground.size = vec(this.width, this.height-headerHeight)
        titleText.pos = vec(this.width/2, (if(style == Style.DEFAULT) 3 else 2) + 0)
        headerClickRegion.frame = headerBackground.frame

        val bevelWidth = if(style == Style.DEFAULT) 3 else 2
        val controlWidth: Double

        if(Minecraft.IS_RUNNING_ON_MAC) {
            closeButton.pos = vec(bevelWidth, bevelWidth)

            if(useShortHeader) {
                closeButton.size = vec(5, 5)
                closeButton.sprite = PastryTexture.windowIconsCloseMacosSmall
                minimizeButton.sprite = PastryTexture.windowIconsMinimizeMacosSmall
                maximizeButton.sprite = PastryTexture.windowIconsMaximizeMacosSmall
            } else {
                closeButton.pos += vec(1, 1)
                closeButton.size = vec(7, 7)
                closeButton.sprite = PastryTexture.windowIconsCloseMacos
                minimizeButton.sprite = PastryTexture.windowIconsMinimizeMacos
                maximizeButton.sprite = PastryTexture.windowIconsMaximizeMacos
            }

            minimizeButton.frame = closeButton.frame.offset(vec(closeButton.width + 2, 0))
            maximizeButton.frame = minimizeButton.frame.offset(vec(minimizeButton.width + 2, 0))

            controlWidth = maximizeButton.frame.max.x
        } else {
            closeButton.size = vec(5, 5)
            closeButton.pos = vec(this.width-closeButton.width-bevelWidth, bevelWidth)
            minimizeButton.size = vec(6, 5)
            maximizeButton.size = vec(6, 5)

            closeButton.sprite = PastryTexture.windowIconsClose
            minimizeButton.sprite = PastryTexture.windowIconsMinimize
            maximizeButton.sprite = PastryTexture.windowIconsMaximize

            if(style == Style.DEFAULT) {
                closeButton.pos += vec(-1, 1)
            }

            minimizeButton.frame = closeButton.frame.offset(-vec(minimizeButton.width + 2, 0))
            maximizeButton.frame = minimizeButton.frame.offset(-vec(maximizeButton.width + 2, 0))

            controlWidth = this.width - maximizeButton.pos.x
        }

        header.pos = vec(controlWidth + 3, bevelWidth)
        header.size = vec(this.width - header.pos.x * 2, headerHeight - header.pos.y - 2)

        content.pos = vec(bevelWidth, headerHeight + 2)
        content.size = vec(this.width - bevelWidth * 2, this.height - content.pos.y - bevelWidth)

        super.layoutChildren()
    }

    /**
     * Pass a component to this method to allow the window to be dragged with it.
     */
    fun addDragHooks(component: GuiComponent) {
        var draggingButton: EnumMouseButton? = null

        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            if (draggingButton == null && draggingSide == null && component.mouseOver) {
                draggingButton = event.button
                draggingSide = Align2d.CENTER
                this.beginFrameDragOperation(Align2d.CENTER)
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (draggingButton == event.button) {
                draggingButton = null
                draggingSide = null
                this.endFrameDragOperation()
                event.cancel()
            }
        }
    }

    /**
     * Returns the edge or corner the mouse is over, if any
     */
    fun getMouseEdge(pos: Vec2d): Align2d? {
        val dLeft = pos.x
        val dRight = size.x - pos.x
        val dTop = pos.y
        val dBottom = size.y - pos.y

        if(dLeft > 0 && dTop > 0 && dLeft + dTop <= cornerSize) return Align2d.LEFT_TOP
        if(dRight > 0 && dTop > 0 && dRight + dTop <= cornerSize) return Align2d.RIGHT_TOP
        if(dLeft > 0 && dBottom > 0 && dLeft + dBottom <= cornerSize) return Align2d.LEFT_BOTTOM
        if(dRight > 0 && dBottom > 0 && dRight + dBottom <= cornerSize) return Align2d.RIGHT_BOTTOM

        if(dTop > 0 && dBottom > 0 && dLeft in edgeRange) return Align2d.LEFT_CENTER
        if(dTop > 0 && dBottom > 0 && dRight in edgeRange) return Align2d.RIGHT_CENTER
        if(dLeft > 0 && dRight > 0 && dTop in edgeRange) return Align2d.CENTER_TOP
        if(dLeft > 0 && dRight > 0 && dBottom in edgeRange) return Align2d.CENTER_BOTTOM

        return null
    }

    enum class Style(val header: Sprite, val body: Sprite) {
        DEFAULT(PastryTexture.windowBackgroundTitlebar, PastryTexture.windowBackgroundBody),
        PANEL(PastryTexture.windowSlightBackgroundTitlebar, PastryTexture.windowSlightBackgroundBody),
        DIALOG(PastryTexture.windowDialogBackgroundTitlebar, PastryTexture.windowDialogBackgroundBody),
    }
}