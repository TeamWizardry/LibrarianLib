package com.teamwizardry.librarianlib.facade.pastry.components.dropdown

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.identityMapOf
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.math.ScreenSpace
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec
import java.awt.Color
import kotlin.math.max

class DropdownMenu<T>(val button: PastryDropdown<T>, val mouseActivated: Boolean): GuiLayer(0, 0) {
    private val creationTime = Client.time.time

    val background = SpriteLayer(PastryTexture.dropdownBackground)
    val stack = StackLayout.build(0, 0).spacing(1).build()
    val items = identityMapOf<PastryDropdownItem<T>, DropdownStackItem>()
    val contents = GuiLayer(-2, 0)
    val contentsClip = GuiLayer(0, 0)
    val dragSelectTime = 10
    val dragSelectDist = 10
    var mouseClickStarted = false
    lateinit var initialMousePos: Vec2d
    var disableLayout = false

    init {
        zIndex = GuiLayer.OVERLAY_Z
        this.add(background, contents)
        contents.add(contentsClip)
        contentsClip.add(stack)
        contentsClip.clipToBounds = true

        button.items.forEach { item ->
            val stackItem = DropdownStackItem(item)
            items[item] = stackItem
            stack.add(stackItem)
        }

        stack.fitToLength()
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
        stack.pos = vec(0, -clipY)
    }

    fun scrollTo(item: PastryDropdownItem<T>) {
        runLayout()
        val stackItem = items[item] ?: return
        val itemPos = stackItem.itemLayer.convertPointTo(vec(0, 0), this).y
        contents.yi -= itemPos.toInt() - 2
    }

    // TODO - use ticks to prevent framerate-dependent motion
    @Hook
    fun preFrame(e: GuiLayerEvents.Update) {
        val parent = this.parent ?: return
        val min = button.convertPointTo(vec(0, 0), parent)
        this.pos = min
        if(stack.frame.minY < contentsClip.bounds.minY && this.mouseOver && !disableLayout) {
            val y = contentsClip.mousePos.y - contentsClip.bounds.minY
            when {
                y <= 4.0 -> contents.yi += 5
                y <= 8.0 -> contents.yi += 3
                y <= 16.0 -> contents.yi += 1
            }
        }
        if(stack.frame.maxY > contentsClip.bounds.maxY && this.mouseOver && !disableLayout) {
            val y = contentsClip.bounds.maxY - contentsClip.mousePos.y
            when {
                y <= 4.0 -> contents.yi -= 5
                y <= 8.0 -> contents.yi -= 3
                y <= 16.0 -> contents.yi -= 1
            }
        }
    }

    @Hook
    fun mouseDown(e: GuiLayerEvents.MouseDown) {
        if(!mouseOver) {
            close()
            return
        }
        mouseClickStarted = true
    }

    @Hook
    fun mouseUp(e: GuiLayerEvents.MouseUp) {
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
            val newMousePos = root.let { gui ->
                gui.convertPointTo(gui.mousePos, ScreenSpace)
            } ?: initialMousePos
            if(Client.time.time - creationTime > dragSelectTime ||
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
//        contentsClip.clipToBounds = false TODO

        layoutChildren()
        disableLayout = true

        contents.pos += vec(0, 2)
//        stack.pos -= vec(0, 2)
        contents.size -= vec(0, 4)

        val collapseTime = 11f
        val easing = Easing.easeOutQuint
        val fadeTime = 2f

        background.pos_rm.animate(vec(0, 0), collapseTime, easing)
        background.size_rm.animate(button.size, collapseTime, easing)
        val itemLayerPos = selected.itemLayer.convertPointTo(vec(0, 0), this)
        stack.pos_rm.animate(stack.pos + vec(2, 2) - itemLayerPos, collapseTime, easing)
        background.tint_im.animate(Color(1f, 1f, 1f, 0f), fadeTime, easing, collapseTime).onComplete {
            button.buttonContents?.isVisible = true
            close()
        }
        interactive = false
    }

    fun close() {
        this.parent?.remove(this)
        button.menuClosed()
    }

}

class DropdownStackItem(val item: PastryDropdownItem<*>): GuiLayer(0, 0) {
    val itemLayer = item.createLayer()
    val highlight = SpriteLayer(PastryTexture.dropdownHighlight)
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
            add(highlight)
        }

        itemLayer.pos = inset
        height = itemLayer.height + inset.y*2
        add(itemLayer)
    }

    override fun update() {
        super.update()
        if(!item.decoration)
            highlight.isVisible = mouseOver
    }

    override fun layoutChildren() {
        super.layoutChildren()
        if(item.listDynamicWidth) {
            itemLayer.width = this.width - inset.x*2
        }
        highlight.frame = this.bounds
    }

}
