package com.teamwizardry.librarianlib.facade.layers.minecraft

import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.layers.LinearGaugeLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.mosaic.ISprite
import com.teamwizardry.librarianlib.mosaic.WrappedSprite
import net.minecraft.fluid.Fluid

/**
 * Easy way to render a fluid gauge.
 */
public class FluidGaugeLayer(x: Int, y: Int, width: Int, height: Int): LinearGaugeLayer(x, y, width, height) {
    public constructor(x: Int, y: Int): this(x, y, 0, 0)
    public constructor(): this(0, 0, 0, 0)

    /**
     * @see fluid
     */
    public val fluid_im: IMValue<Fluid?> = imValue()

    /**
     * The fluid to render. No fluid is rendered if this is null
     */
    public var fluid: Fluid? by fluid_im

    /**
     * @see flow
     */
    public val flow_im: IMValue<Cardinal2d?> = imValue()

    /**
     * The direction the fluid should appear to flow, if at all.
     */
    public var flow: Cardinal2d? by flow_im

    private val fluidSprite = addAnimationTimeListener(FluidSprite(this))
    private val pinnedFluidSprite = object: WrappedSprite() {
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
    private val fluidLayer = SpriteLayer(pinnedFluidSprite)

    init {
        contents.add(fluidLayer)
        fluidSprite.fluid_im.set { fluid }
        fluidSprite.flow_im.set { flow }
    }

    override fun update() { // todo yoga
        super.update()
        fluidLayer.frame = contents.bounds
    }
}
