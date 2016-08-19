package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.bloat.MathUtil
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.HandlerList
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import java.util.function.Consumer

class ComponentSlider(posX: Int, posY: Int, width: Int, height: Int, percentage: Double, var increments: Int) : GuiComponent<ComponentSlider>(posX, posY, width, height) {

    var percentageChange = HandlerList<Consumer<Double>>()

    val handle: ComponentVoid
    var percentage: Double = 0.toDouble()
        set(percentage) {
            var newPercent = percentage
            newPercent = MathUtil.clamp(newPercent, 0.0, 1.0)
            if (increments > 0)
                newPercent = MathUtil.round(newPercent, 1.0 / increments)
            handlePos = size.mul(newPercent)
            field = newPercent
            percentageChange.fireAll { h -> h.accept(this.percentage) }
        }
    private var handlePos: Vec2d = Vec2d(0.0, 0.0)

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

        handle.BUS.hook(PreDrawEvent::class.java) { event ->
            if (handlePos != handle.pos)
                handle.pos = handlePos
        }

        add(handle)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // noop
    }
}
