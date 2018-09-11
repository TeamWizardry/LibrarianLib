package com.teamwizardry.librarianlib.features.gui.components

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.gui.IMValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.sprite.LTextureAtlasSprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Easy way to render a fluid gauge.
 * The background [Sprite] will be rendered behind the fluid, and the
 * foreground [Sprite] will be rendered over it (overlay).
 */
@SideOnly(Side.CLIENT)
class ComponentFluidStack @JvmOverloads constructor(fgSprite: ISprite?, bgSprite: ISprite?,
                                                    x: Int, y: Int,
                                                    fgWidth: Int = fgSprite?.width ?: 16, fgHeight: Int = fgSprite?.height ?: 16,
                                                    bgWidth: Int = bgSprite?.width ?: 16, bgHeight: Int = bgSprite?.height ?: 16,
                                                    var fluidWidth: Int = bgWidth, var fluidHeight: Int = bgHeight,
                                                    val tankProps: IFluidTankProperties)
    : GuiComponent(x, y, bgWidth, bgHeight) {

    val direction_im: IMValue<ComponentSpriteProgressBar.ProgressDirection> = IMValue(ComponentSpriteProgressBar.ProgressDirection.X_POS)
    var direction: ComponentSpriteProgressBar.ProgressDirection by direction_im

    private var lastFluid: Fluid? = null

    val progress = ComponentProgressBar(null, bgSprite, 0, 0, fgWidth = fluidWidth, fgHeight = fluidHeight, bgWidth = bgWidth, bgHeight = bgHeight)

    init {
        progress.direction_im { this.direction }
        progress.progress_im {
            (tankProps.contents?.amount ?: 0) / Math.max(1, tankProps.capacity).toDouble()
        }
    }
    override fun draw(partialTicks: Float) {
        val fs = tankProps.contents
        if (fs != null && lastFluid != fs.fluid) {
            lastFluid = fs.fluid
            progress.progressComponent.sprite = LTextureAtlasSprite(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(fs.fluid.still.toString()), fluidWidth, fluidHeight)
        }
    }

    init {
        this.add(progress)
        if (fgSprite != null) this.add(ComponentSprite(fgSprite, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, fgWidth, fgHeight))
        val fs = tankProps.contents
        if (fs != null) {
            lastFluid = fs.fluid
            progress.progressComponent.sprite = LTextureAtlasSprite(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(fs.fluid.still.toString()), fluidWidth, fluidHeight)
        }
    }
}
