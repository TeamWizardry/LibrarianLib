package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.math.rect
import kotlin.math.roundToInt

open class LinearGaugeLayer: GuiLayer {
    constructor(): super()
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    /**
     * @see fillFraction
     */
    val fillFraction_im: IMValueDouble = imDouble(1.0)
    /**
     * How full the gauge is. Ranges from 0–1
     */
    var fillFraction: Double by fillFraction_im

    /**
     * @see direction
     */
    val direction_im: IMValue<Cardinal2d> = imValue(Cardinal2d.UP)
    /**
     * The direction to expand (e.g. [UP][Cardinal2d.UP] means the gauge will sit at the bottom and rise up)
     */
    var direction: Cardinal2d by direction_im

    /**
     * The contents of the gauge. This is the layer that is resized based on [fillFraction].
     */
    val contents: GuiLayer = GuiLayer()

    init {
        this.add(contents)
    }

    override fun update() { // todo yoga
        val fillFraction = fillFraction
        val totalSize = direction.axis.get(this.size)
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