package com.teamwizardry.librarianlib.facade.layers.minecraft

import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.layers.LinearGaugeLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.value.IMValueInt
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Direction2d
import com.teamwizardry.librarianlib.mosaic.ISprite
import com.teamwizardry.librarianlib.mosaic.WrappedSprite
import net.minecraftforge.fluids.FluidStack
import java.awt.Color
import kotlin.math.roundToInt

/**
 * Easy way to render a fluid gauge.
 */
public class FluidGaugeLayer(x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    public constructor(x: Int, y: Int): this(x, y, 0, 0)
    public constructor(): this(0, 0, 0, 0)

    public val capacity_im: IMValueInt = imInt(1000)

    /**
     * The capacity of the gauge in millibuckets
     */
    public var capacity: Int by capacity_im

    public val content_im: IMValue<FluidStack?> = imValue()

    /**
     * The fluid to render. No fluid is rendered if this is null
     */
    public var content: FluidStack? by content_im

    /**
     * The direction the fluid should appear to flow, if at all.
     */
    public var flow: Direction2d? = null

    /**
     * Whether lighter-than-air fluids should "float" and appear reversed
     */
    public var floatLightFluids: Boolean = false

    public var direction: Direction2d = Direction2d.UP

    private val actualDirection: Direction2d
        get() = if(floatLightFluids && content?.fluid?.attributes?.isLighterThanAir == true) {
            direction.opposite
        } else {
            direction
        }

    private val fluidSprite = FluidSprite({ content?.fluid }, ::flow)
    private val pinnedFluidSprite = object: WrappedSprite() {
        override val wrapped: ISprite? get() = fluidSprite
        override val pinTop: Boolean
            get() = actualDirection == Direction2d.DOWN ||
                (actualDirection != Direction2d.UP && flow == Direction2d.DOWN) ||
                (actualDirection.axis == Axis2d.X && flow?.axis != Axis2d.Y)
        override val pinBottom: Boolean
            get() = actualDirection == Direction2d.UP ||
                (actualDirection != Direction2d.DOWN && flow == Direction2d.UP)
        override val pinLeft: Boolean
            get() = actualDirection == Direction2d.RIGHT ||
                (actualDirection != Direction2d.LEFT && flow == Direction2d.RIGHT) ||
                (actualDirection.axis == Axis2d.Y && flow?.axis != Axis2d.X)
        override val pinRight: Boolean
            get() = actualDirection == Direction2d.LEFT ||
                (actualDirection != Direction2d.RIGHT && flow == Direction2d.LEFT)
    }
    private val fluidLayer = SpriteLayer(pinnedFluidSprite)

    init {
        add(fluidLayer)
        fluidLayer.tint_im.set {
            content?.let { content ->
                Color(content.fluid.attributes.getColor(content))
            } ?: Color.WHITE
        }
    }

    override fun prepareLayout() {
        val content = content
        val fillFraction = if(capacity == 0 || content == null) 0.0 else content.amount.toDouble() / capacity
        val totalSize = actualDirection.axis.get(this.size)
        val fullSize = (totalSize * fillFraction).roundToInt()
        val emptySize = (totalSize * (1 - fillFraction)).roundToInt()

        fluidLayer.frame = when (actualDirection) {
            Direction2d.UP -> rect(0, emptySize, width, fullSize)
            Direction2d.DOWN -> rect(0, 0, width, fullSize)
            Direction2d.LEFT -> rect(emptySize, 0, fullSize, height)
            Direction2d.RIGHT -> rect(0, 0, fullSize, height)
        }
    }
}
