package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.HandlerList
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.util.function.Consumer

class ComponentSlider(posX: Int, posY: Int, width: Int, height: Int, percentage: Double, var increments: Int) : GuiComponent(posX, posY, width, height) {

    var percentageChange = HandlerList<Consumer<Double>>()

    val handle: ComponentVoid
    var percentage: Double = 0.0
        set(percentage) {
            var newPercent = percentage
            newPercent = Math.min(1.0, Math.max(newPercent, 0.0))
            if (increments > 0)
                newPercent = Math.round(newPercent * increments).toDouble() / increments
            handlePos = size.mul(newPercent)
            field = newPercent
            percentageChange.fireAll { h -> h.accept(this.percentage) }
        }
    private var handlePos: Vec2d by RMValue(vec(0, 0))

    init {

        handle = ComponentVoid(0, 0)
        this.percentage = percentage

        DragMixin(handle) { vecIn ->
            var vec = vecIn.projectOnTo(size)
            vec = Vec2d.max(vec, Vec2d.ZERO) // clamp to the begining
            vec = Vec2d.min(vec, size) // clamp to the end

            this.percentage = vec.length() / size.length()

            vec
        }

        handle.BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) {
            if (handlePos != handle.pos)
                handle.pos = handlePos
        }

        add(handle)
    }

    override fun draw(partialTicks: Float) {
        //NO-OP
    }
}
