package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.math.Direction2d
import com.teamwizardry.librarianlib.core.util.rect
import kotlin.math.roundToInt

public open class LinearGaugeLayer: GuiLayer {
    public constructor(): super()
    public constructor(posX: Int, posY: Int): super(posX, posY)
    public constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    /**
     * @see fillFraction
     */
    public val fillFraction_im: IMValueDouble = imDouble(1.0)

    /**
     * How full the gauge is. Ranges from 0–1
     */
    public var fillFraction: Double by fillFraction_im

    /**
     * The direction to expand (e.g. [UP][Direction2d.UP] means the gauge will sit at the bottom and rise up)
     */
    public var direction: Direction2d = Direction2d.UP

    /**
     * The contents of the gauge. This is the layer that is resized based on [fillFraction].
     */
    public val contents: GuiLayer = GuiLayer()

    init {
        this.add(contents)
    }

    override fun prepareLayout() {
        val fillFraction = fillFraction
        val totalSize = direction.axis.get(this.size)
        val fullSize = (totalSize * fillFraction).roundToInt()
        val emptySize = (totalSize * (1 - fillFraction)).roundToInt()

        contents.frame = when (direction) {
            Direction2d.UP -> rect(0, emptySize, width, fullSize)
            Direction2d.DOWN -> rect(0, 0, width, fullSize)
            Direction2d.LEFT -> rect(emptySize, 0, fullSize, height)
            Direction2d.RIGHT -> rect(0, 0, fullSize, height)
        }
    }
}