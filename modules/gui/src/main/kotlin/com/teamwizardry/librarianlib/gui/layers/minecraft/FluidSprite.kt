package com.teamwizardry.librarianlib.gui.layers.minecraft

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.sprite.LTextureAtlasSprite
import com.teamwizardry.librarianlib.features.sprite.WrappedSprite
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Easy way to render an optionally flowing fluid.
 */
@SideOnly(Side.CLIENT)
class FluidSprite() : WrappedSprite() {
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
    /**
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
            val spriteName = if(flow == null) fluid.still else fluid.flowing
            val atlasSprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(spriteName.toString())
            lastFluidSprite = LTextureAtlasSprite(atlasSprite, atlasSprite.iconWidth, atlasSprite.iconHeight)
        }
        lastFluid = fluid
        lastFlow = flow
    }

}
