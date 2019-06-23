package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.HandlerList
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.util.function.Consumer

/**
 * ## Facade equivalent: [PastrySlider][com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastrySlider] (unimplemented)
 */
@Deprecated("As of version 4.20 this has been superseded by Facade")
class ComponentSlider(posX: Int, posY: Int, width: Int, height: Int, percentage: Double, var increments: Int) : GuiComponent(posX, posY, width, height) {

    var percentageChange = HandlerList<Consumer<Double>>()

    val handle: ComponentVoid
    var percentage: Double = 0.toDouble()
        set(percentage) {
            var newPercent = percentage
            newPercent = Math.min(1.0, Math.max(newPercent, 0.0))
            if (increments > 0)
                newPercent = Math.round(newPercent * increments).toDouble() / increments
            handlePos = size.mul(newPercent)
            field = newPercent
            percentageChange.fireAll { h -> h.accept(this.percentage) }
        }
    private var handlePos: Vec2d = vec(0, 0)

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

        handle.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
            if (handlePos != handle.pos)
                handle.pos = handlePos
        }

        add(handle)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        //NO-OP
    }
}
