package com.teamwizardry.librarianlib.features.gui.provided.pastry.components.dropdown

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.value.GuiAnimator
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.identityMapOf
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import java.awt.Color
import kotlin.math.max

class DropdownMenu<T>(val button: PastryDropdown<T>, val mouseActivated: Boolean): GuiComponent(0, 0) {
    private val creationTime = ClientTickHandler.ticks

    val background = SpriteLayer(PastryTexture.dropdownBackground, 0, 0)
    val stack = StackLayout.build(0, 2).space(1).layer()
    val items = identityMapOf<PastryDropdownItem<T>, DropdownStackItem>()
    val contents = GuiComponent(-2, 0)
    val dragSelectTime = 10
    val dragSelectDist = 10
    var mouseClickStarted = false
    lateinit var initialMousePos: Vec2d
    var disableLayout = false

    init {
        zIndex = GuiLayer.OVERLAY_Z
        this.add(background, contents)
        contents.add(stack.componentWrapper())
        contents.clipToBounds = true
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
        background.frame = contents.frame
    }

    fun scrollTo(item: PastryDropdownItem<T>) {
        runLayout()
        val stackItem = items[item] ?: return
        val itemPos = stackItem.itemLayer.convertPointTo(vec(0, 0), this).y
        contents.yi -= itemPos.toInt() - 2
    }

    @Hook
    fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
        val parent = this.parent ?: return
        val min = button.convertPointTo(vec(0, 0), parent)
        this.pos = min
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
        button.select(index)
        button.buttonContents?.isVisible = false

        items.values.forEach {
            it.isVisible = false
        }
        selected.isVisible = true
        selected.highlight.isVisible = false

        runLayout()
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
