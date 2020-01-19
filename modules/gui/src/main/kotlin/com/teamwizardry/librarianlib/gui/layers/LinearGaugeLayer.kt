package com.teamwizardry.librarianlib.gui.layers

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import kotlin.math.roundToInt

open class LinearGaugeLayer: GuiLayer {
    constructor(): super()
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    /**
     * @see fillFraction
     */
    val fillFraction_im: IMValueDouble = IMValueDouble(1.0)
    /**
     * How full the gauge is. Ranges from 0–1
     */
    var fillFraction: Double by fillFraction_im

    /**
     * @see direction
     */
    val direction_im: IMValue<Cardinal2d> = IMValue(Cardinal2d.UP)
    /**
     * The direction to expand (e.g. [UP][Cardinal2d.UP] means the gauge will sit at the bottom and rise up)
     */
    var direction: Cardinal2d by direction_im

    /**
     * The contents of the gauge. This is the layer that is resized based on [fillFraction].
     */
    val contents: GuiLayer = GuiLayer()

    override fun update() {
        setNeedsLayout()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        val fillFraction = fillFraction
        val totalSize = this.size[direction.axis]
        val fullSize = (totalSize * fillFraction).roundToInt()
        val emptySize = (totalSize * (1-fillFraction)).roundToInt()

        contents.frame = when(direction) {
            Cardinal2d.UP -> rect(0, emptySize, width, fullSize)
            Cardinal2d.DOWN -> rect(0, 0, width, fullSize)
            Cardinal2d.LEFT -> rect(emptySize, 0, fullSize, height)
            Cardinal2d.RIGHT -> rect(0, 0, fullSize, height)
        }
    }
}