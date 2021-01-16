package com.teamwizardry.librarianlib.facade.layers.minecraft

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.layer.AnimationTimeListener
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.math.Direction2d
import com.teamwizardry.librarianlib.mosaic.ISprite
import com.teamwizardry.librarianlib.mosaic.LTextureAtlasSprite
import com.teamwizardry.librarianlib.mosaic.WrappedSprite
import net.minecraft.fluid.Fluid
import java.util.function.Supplier

/**
 * Easy way to render an optionally flowing fluid.
 */
public class FluidSprite(private val fluid: Supplier<Fluid?>, private val flow: Supplier<Direction2d?>) :
    WrappedSprite() {
    private var lastFluidSprite: ISprite? = null
    private var lastFluid: Fluid? = null
    private var lastFlow: Direction2d? = null
    override val wrapped: ISprite?
        get() {
            update()
            return lastFluidSprite
        }
    override val rotation: Int
        get() {
            update()
            return (lastFlow ?: Direction2d.DOWN).rotation + 2
        }

    public fun update() {
        val fluid = fluid.get()
        val flow = flow.get()
        if (fluid == lastFluid && flow == lastFlow) {
            return
        } else if (fluid == null) {
            lastFluidSprite = null
        } else {
            val spriteName = if (flow == null) fluid.attributes.stillTexture else fluid.attributes.flowingTexture
            if (spriteName == null) {
                lastFluidSprite = null
            } else {
                val atlasSprite = Client.getBlockAtlasSprite(spriteName)
                lastFluidSprite = LTextureAtlasSprite(atlasSprite)
            }
        }
        lastFluid = fluid
        lastFlow = flow
    }
}
