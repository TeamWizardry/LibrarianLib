package com.teamwizardry.librarianlib.features.facade.layers.minecraft

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentProgressBar
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentSpriteProgressBar
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.sprite.LTextureAtlasSprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.WrappedSprite
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.roundToInt

/**
 * Easy way to render a fluid gauge.
 * The background [Sprite] will be rendered behind the fluid, and the
 * foreground [Sprite] will be rendered over it (overlay).
 */
@SideOnly(Side.CLIENT)
class FluidGaugeLayer(x: Int, y: Int, width: Int, height: Int) : GuiLayer(x, y, width, height) {
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
     * @see fillFraction
     */
    val fillFraction_im: IMValueDouble = IMValueDouble(1.0)
    /**
     * How full the tank is. Ranges from 0–1
     */
    var fillFraction: Double by fillFraction_im

    /**
     * @see direction
     */
    val direction_im: IMValue<Cardinal2d> = IMValue(Cardinal2d.UP)
    /**
     * The direction to expand (e.g. [UP][Cardinal2d.UP] means the fluid will sit at the bottom and rise up)
     */
    var direction: Cardinal2d by direction_im

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
    private val fluidLayer = SpriteLayer(pinnedFluidSprite)

    init {
        this.add(fluidLayer)
        fluidSprite.fluid_im { fluid }
        fluidSprite.flow_im { flow }
    }

    @Hook
    private fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
        setNeedsLayout()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        val fillFraction = fillFraction
        val totalSize = this.size[direction.axis]
        val fullSize = (totalSize * fillFraction).roundToInt()
        val emptySize = (totalSize * (1-fillFraction)).roundToInt()

        fluidLayer.frame = when(direction) {
            Cardinal2d.UP -> rect(0, emptySize, width, fullSize)
            Cardinal2d.DOWN -> rect(0, 0, width, fullSize)
            Cardinal2d.LEFT -> rect(emptySize, 0, fullSize, height)
            Cardinal2d.RIGHT -> rect(0, 0, fullSize, height)
        }
    }
}
