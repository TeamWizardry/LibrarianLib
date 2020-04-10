package com.teamwizardry.librarianlib.gui.components.minecraft

import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.components.LinearGaugeLayer
import com.teamwizardry.librarianlib.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.sprites.ISprite
import com.teamwizardry.librarianlib.sprites.WrappedSprite
import net.minecraft.fluid.Fluid

/**
 * Easy way to render a fluid gauge.
 * The background [Sprite] will be rendered behind the fluid, and the
 * foreground [Sprite] will be rendered over it (overlay).
 */
class FluidGaugeLayer(x: Int, y: Int, width: Int, height: Int) : LinearGaugeLayer(x, y, width, height) {
    constructor(x: Int, y: Int): this(x, y, 0, 0)
    constructor(): this(0, 0, 0, 0)

    /**
     * @see fluid
     */
    val fluid_im: IMValue<Fluid?> = IMValue()
    /**
     * The fluid to render. No fluid is rendered if this is null
     */
    var fluid: Fluid? by fluid_im

    /**
     * @see flow
     */
    val flow_im: IMValue<Cardinal2d?> = IMValue()
    /**
     * The direction the fluid should appear to flow, if at all.
     */
    var flow: Cardinal2d? by flow_im

    private val fluidSprite = FluidSprite()
    private val pinnedFluidSprite = object : WrappedSprite() {
        override val wrapped: ISprite? get() = fluidSprite
        override val pinTop: Boolean
            get() = direction == Cardinal2d.DOWN ||
                (direction != Cardinal2d.UP && flow == Cardinal2d.DOWN) ||
                (direction.axis == Axis2d.X && flow?.axis != Axis2d.Y)
        override val pinBottom: Boolean
            get() = direction == Cardinal2d.UP ||
                (direction != Cardinal2d.DOWN && flow == Cardinal2d.UP)
        override val pinLeft: Boolean
            get() = direction == Cardinal2d.RIGHT ||
                (direction != Cardinal2d.LEFT && flow == Cardinal2d.RIGHT) ||
                (direction.axis == Axis2d.Y && flow?.axis != Axis2d.X)
        override val pinRight: Boolean
            get() = direction == Cardinal2d.LEFT ||
                (direction != Cardinal2d.RIGHT && flow == Cardinal2d.LEFT)
    }
    private val fluidComponent = SpriteLayer(pinnedFluidSprite)

    init {
        contents.add(fluidComponent)
        fluidSprite.fluid_im { fluid }
        fluidSprite.flow_im { flow }
    }

    override fun update() { // todo yoga
        super.update()
        fluidComponent.frame = contents.bounds
    }
}
