package com.teamwizardry.librarianlib.gui.components

import java.util.function.Consumer

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.HandlerList
import com.teamwizardry.librarianlib.gui.mixin.DragMixin
import com.teamwizardry.librarianlib.math.MathUtil
import com.teamwizardry.librarianlib.math.Vec2d

class ComponentSlider(posX: Int, posY: Int, width: Int, height: Int, percentage: Double, var increments: Int) : GuiComponent<ComponentSlider>(posX, posY, width, height) {

    var percentageChange = HandlerList<Consumer<Double>>()

    val handle: ComponentVoid
    var percentage: Double = 0.toDouble()
        set(percentage) {
            var percentage = percentage
            percentage = MathUtil.clamp(percentage, 0.0, 1.0)
            if (increments > 0)
                percentage = MathUtil.round(percentage, 1.0 / increments)
            handlePos = size.mul(percentage)
            field = percentage
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

        handle.preDraw.add({ c, pos, partialTicks ->
            if (handlePos != handle.pos)
                handle.pos = handlePos
        })

        add(handle)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // noop
    }
}
