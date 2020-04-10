package com.teamwizardry.librarianlib.gui.components.minecraft

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.sprites.ISprite
import com.teamwizardry.librarianlib.sprites.LTextureAtlasSprite
import com.teamwizardry.librarianlib.sprites.WrappedSprite
import net.minecraft.fluid.Fluid

/**
 * Easy way to render an optionally flowing fluid.
 */
class FluidSprite : WrappedSprite() {
    /**
     * @see fluid
     */
    val fluid_im: IMValue<Fluid?> = IMValue()
    /**
     * The fluid to be drawn
     */
    var fluid: Fluid? by fluid_im

    /**
     * @see flow
     */
    val flow_im: IMValue<Cardinal2d?> = IMValue()
    /*
     * The direction the fluid should appear to flow, if at all.
     */
    var flow: Cardinal2d? by flow_im

    private var lastFluidSprite: ISprite? = null
    private var lastFluid: Fluid? = null
    private var lastFlow: Cardinal2d? = null
    override val wrapped: ISprite?
        get() {
            update()
            return lastFluidSprite
        }
    override val rotation: Int
        get() {
            update()
            return (lastFlow ?: Cardinal2d.DOWN).rotation + 2
        }

    fun update() {
        val fluid = fluid
        val flow = flow
        if(fluid == lastFluid && flow == lastFlow) {
            return
        } else if(fluid == null) {
            lastFluidSprite = null
        } else {
            val spriteName = if(flow == null) fluid.attributes.stillTexture else fluid.attributes.flowingTexture
            val atlasSprite = Client.getBlockAtlasSprite(spriteName)
            lastFluidSprite = LTextureAtlasSprite(atlasSprite)
        }
        lastFluid = fluid
        lastFlow = flow
    }

}
