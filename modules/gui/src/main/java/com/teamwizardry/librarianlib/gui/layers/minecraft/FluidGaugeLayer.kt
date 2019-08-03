package com.teamwizardry.librarianlib.gui.layers.minecraft

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.components.ComponentProgressBar
import com.teamwizardry.librarianlib.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteProgressBar
import com.teamwizardry.librarianlib.gui.layers.LinearGaugeLayer
import com.teamwizardry.librarianlib.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.gui.value.IMValueDouble
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
    private val fluidLayer = SpriteLayer(pinnedFluidSprite)

    init {
        contents.add(fluidLayer)
        fluidSprite.fluid_im { fluid }
        fluidSprite.flow_im { flow }

        contents.onLayout {
            fluidLayer.frame = contents.bounds
        }
    }
}
