package com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.facade.value.GuiAnimator
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.identityMapOf
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import java.awt.Color
import kotlin.math.max

class DropdownMenu<T>(val button: PastryDropdown<T>, val mouseActivated: Boolean): GuiComponent(0, 0) {
    private val creationTime = ClientTickHandler.ticks

    val background = SpriteLayer(PastryTexture.dropdownBackground, 0, 0)
    val stack = StackLayout.build(0, 0).space(1).layer()
    val items = identityMapOf<PastryDropdownItem<T>, DropdownStackItem>()
    val contents = GuiComponent(-2, 0)
    val contentsClip = GuiComponent(0, 0)
    val dragSelectTime = 10
    val dragSelectDist = 10
    var mouseClickStarted = false
    lateinit var initialMousePos: Vec2d
    var disableLayout = false

    init {
        zIndex = GuiLayer.OVERLAY_Z
        this.add(background, contents)
        contents.add(contentsClip)
        contentsClip.add(stack.componentWrapper())
        contentsClip.clipToBounds = true

        button.items.forEach { item ->
            val stackItem = DropdownStackItem(item)
            items[item] = stackItem
            stack.componentWrapper().add(stackItem)
        }

        stack.fitLength()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        if(disableLayout) return
        this.size = button.size

        items.values.forEach { it.width = button.width }

        contents.size = vec(button.width, max(button.height, stack.height+4))

        val contentsFrame = contents.frame
        var top = contentsFrame.minY
        var bottom = contentsFrame.maxY

        val rootMinLimit = root.bounds.minY + 10
        val rootMaxLimit = root.bounds.maxY - 10
        if(convertPointTo(vec(0, top), root).y < rootMinLimit) {
            top = root.convertPointTo(vec(0, rootMinLimit), this).y
        }
        if(convertPointTo(vec(0, bottom), root).y > rootMaxLimit) {
            bottom = root.convertPointTo(vec(0, rootMaxLimit), this).y
        }

        background.frame = rect(contentsFrame.minX, top, contentsFrame.width, bottom-top)
        val clipY = top-contentsFrame.minY
        contentsClip.frame = rect(0, clipY+2, contentsFrame.width, bottom-top-4)
        contentsClip.contentsOffset = vec(0, -clipY)
    }

    fun scrollTo(item: PastryDropdownItem<T>) {
        runLayoutIfNeeded()
        val stackItem = items[item] ?: return
        val itemPos = stackItem.itemLayer.convertPointTo(vec(0, 0), this).y
        contents.yi -= itemPos.toInt() - 2
    }

    @Hook
    fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
        val parent = this.parent ?: return
        val min = button.convertPointTo(vec(0, 0), parent)
        this.pos = min
        if(stack.frame.minY < contentsClip.bounds.minY && contentsClip.mouseHit != null) {
            val y = contentsClip.mousePos.y - contentsClip.bounds.minY
            when {
                y <= 4.0 -> contents.yi += 3
                y <= 8.0 -> contents.yi += 2
                y <= 16.0 -> contents.yi += 1
            }
        }
        if(stack.frame.maxY > contentsClip.bounds.maxY && contentsClip.mouseHit != null) {
            val y = contentsClip.bounds.maxY - contentsClip.mousePos.y
            when {
                y <= 4.0 -> contents.yi -= 3
                y <= 8.0 -> contents.yi -= 2
                y <= 16.0 -> contents.yi -= 1
            }
        }
    }

    @Hook
    fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if(!mouseOver) {
            close()
            return
        }
        mouseClickStarted = true
    }

    @Hook
    fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        if(!mouseOver) {
            close()
            return
        }
        if(mouseClickStarted) {
            if(items.values.any { it.mouseOver })
                select()
            return
        }
        if(mouseActivated) {
            val newMousePos = gui?.let { gui ->
                gui.convertPointTo(gui.mousePos, ScreenSpace)
            } ?: initialMousePos
            if(ClientTickHandler.ticks - creationTime > dragSelectTime ||
                initialMousePos.squareDist(newMousePos) > dragSelectDist * dragSelectDist) {
                select()
            }
        }
    }

    fun select() {
        val selected = items.values.firstOrNull { it.mouseOver }
        val index = button.items.indexOfFirst { it === selected?.item }
        if(selected == null || selected.item.decoration || index < 0) {
            close()
            return
        }
        button.selectIndex(index)
        button.buttonContents?.isVisible = false

        items.values.forEach {
            it.isVisible = false
        }
        selected.isVisible = true
        selected.highlight.isVisible = false

        runLayoutIfNeeded()
        disableLayout = true

        contents.pos += vec(0, 2)
        stack.pos -= vec(0, 2)
        contents.size -= vec(0, 4)

        val collapseTime = 5f
        val fadeTime = 2f
        val anim = GuiAnimator.animate(collapseTime) {
            background.pos = vec(0, 0)
            background.size = button.size

            val itemLayerPos = selected.itemLayer.convertPointTo(vec(0, 0), this)
            stack.pos -= itemLayerPos - vec(2, 2)
        }
        val bgAnim = background.tint_im.animate(Color(1f, 1f, 1f, 0f), fadeTime, Easing.linear, collapseTime)
        bgAnim.completion = Runnable {
            button.buttonContents?.isVisible = true
            close()
        }
        this.add(anim)
    }

    fun close() {
        this.parent?.remove(this)
        button.menuClosed()
    }

}

class DropdownStackItem(val item: PastryDropdownItem<*>): GuiComponent(0, 0) {
    val itemLayer = item.createLayer()
    val highlight = SpriteLayer(PastryTexture.dropdownHighlight, 0, 0)
    val inset: Vec2d =
        if(item.decoration) {
            if(item.listDynamicWidth) {
                vec(0, 0)
            } else {
                vec(4, 0)
            }
        } else {
            vec(4, 1)
        }

    init {
        if(!item.decoration) {
            cursor = LibCursor.POINT
            highlight.isVisible_im { mouseOver }
            add(highlight)
        }

        itemLayer.pos = inset
        height = itemLayer.height + inset.y*2
        add(itemLayer)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        if(item.listDynamicWidth) {
            itemLayer.width = this.width - inset.x*2
        }
        highlight.frame = this.bounds
    }

}
