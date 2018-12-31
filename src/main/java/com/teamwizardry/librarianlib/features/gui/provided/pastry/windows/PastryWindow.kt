package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d

open class PastryWindow(width: Int, height: Int): PastryWindowBase(width, height) {
    val header = GuiComponent(0, 0, width, 0)
    val content = GuiComponent(0, 15, width, height)

    init {
        header.heighti = 15
        content.clipToBounds = true
        add(header, content)
        addDragHooks(header)
    }


    private var draggingButton: EnumMouseButton? = null

    /**
     * Pass a component to this method to allow the window to be dragged with it.
     */
    fun addDragHooks(component: GuiComponent) {
        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            if (draggingButton == null && component.mouseOver) {
                draggingButton = event.button
                this.beginFrameDragOperation(Align2d.CENTER)
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (draggingButton == event.button) {
                draggingButton = null
                this.endFrameDragOperation()
                event.cancel()
            }
        }
    }

    override fun layoutChildren() {
        size = content.size + vec(0, header.height)
        header.pos = vec(0, 0)
        content.y = header.height
        header.width = width
        super.layoutChildren()
    }

}