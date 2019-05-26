package com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.neogui.EnumMouseButton
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.getValue
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.setValue
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.Minecraft
import kotlin.math.abs

open class PastryWindow(width: Int, height: Int, style: Style = Style.DEFAULT, useShortHeader: Boolean = false)
    : PastryWindowBase(0, 0) {
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

    var style: Style = style
        set(value) {
            field = value
            setNeedsLayout()
        }
    var useShortHeader: Boolean = useShortHeader
        set(value) {
            field = value
            setNeedsLayout()
        }
    var enableHeaderControls: Boolean = true
        set(value) {
            field = value
            setNeedsLayout()
        }
    private val shortHeaderHeight = 7
    private val tallHeaderHeight = 11

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
        get() = (draggingSide ?: getMouseEdge(mousePos))?.let { frameCursor(it) } ?: super.cursor
        set(value) { super.cursor = value }

    val headerHeight: Int
        get() = (if(useShortHeader) shortHeaderHeight else tallHeaderHeight) + style.bevelWidth

    var contentSize: Vec2d
        get() = size - vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        set(value) {
            size = value + vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        }

    var maxContentSize: Vec2d
        get() = maxSize - vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        set(value) {
            maxSize = value + vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        }

    var minContentSize: Vec2d
        get() = minSize - vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        set(value) {
            minSize = value + vec(style.bevelWidth*2, headerHeight + 2 + style.bevelWidth)
        }

    init {
        this.contentSize = vec(width, height)
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

        titleText.align = Align2d.TOP_CENTER
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
        headerBackground.sprite = style.header
        bodyBackground.sprite = style.body

        headerBackground.size = vec(this.width, headerHeight)
        bodyBackground.pos = vec(0, headerHeight)
        bodyBackground.size = vec(this.width, this.height-headerHeight)
        titleText.pos = vec(this.width/2, (if(style == Style.DEFAULT) 3 else 2) + 0)
        headerClickRegion.frame = headerBackground.frame

        val controlWidth: Double

        closeButton.isVisible = enableHeaderControls
        minimizeButton.isVisible = enableHeaderControls
        maximizeButton.isVisible = enableHeaderControls
        if(Minecraft.IS_RUNNING_ON_MAC) {
            closeButton.pos = vec(style.bevelWidth, style.bevelWidth)

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
            closeButton.pos = vec(this.width-closeButton.width-style.bevelWidth, style.bevelWidth)
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

        header.pos = vec(controlWidth + 3, style.bevelWidth)
        header.size = vec(this.width - header.pos.x * 2, headerHeight - header.pos.y - 2)

        content.pos = vec(style.bevelWidth, headerHeight + 2)
        content.size = vec(this.width - style.bevelWidth * 2, this.height - content.pos.y - style.bevelWidth)

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

        if(dLeft > 0 && dTop > 0 && abs(dLeft) + abs(dTop) <= cornerSize) return Align2d.TOP_LEFT
        if(dRight > 0 && dTop > 0 && abs(dRight) + abs(dTop) <= cornerSize) return Align2d.TOP_RIGHT
        if(dLeft > 0 && dBottom > 0 && abs(dLeft) + abs(dBottom) <= cornerSize) return Align2d.BOTTOM_LEFT
        if(dRight > 0 && dBottom > 0 && abs(dRight) + abs(dBottom) <= cornerSize) return Align2d.BOTTOM_RIGHT

        if(dTop > 0 && dBottom > 0 && dLeft in edgeRange) return Align2d.CENTER_LEFT
        if(dTop > 0 && dBottom > 0 && dRight in edgeRange) return Align2d.CENTER_RIGHT
        if(dLeft > 0 && dRight > 0 && dTop in edgeRange) return Align2d.TOP_CENTER
        if(dLeft > 0 && dRight > 0 && dBottom in edgeRange) return Align2d.BOTTOM_CENTER

        return null
    }

    enum class Style(val header: Sprite, val body: Sprite, val bevelWidth: Int) {
        DEFAULT(PastryTexture.windowBackgroundTitlebar, PastryTexture.windowBackgroundBody, 3),
        PANEL(PastryTexture.windowSlightBackgroundTitlebar, PastryTexture.windowSlightBackgroundBody, 2),
        DIALOG(PastryTexture.windowDialogBackgroundTitlebar, PastryTexture.windowDialogBackgroundBody, 2),
    }
}