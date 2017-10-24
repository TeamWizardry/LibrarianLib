package com.teamwizardry.librarianlib.features.gui.components

/*
 * Created by bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Easy way to render a progress bar with a foreground [Sprite], used to show the progress, as well as a background [Sprite].
 */
@SideOnly(Side.CLIENT)
class ComponentProgressBar @JvmOverloads constructor(fgSprite: ISprite?, bgSprite: ISprite?,
                                                     x: Int, y: Int,
                                                     fgWidth: Int = fgSprite?.width ?: 16, fgHeight: Int = fgSprite?.height ?: 16,
                                                     bgWidth: Int = bgSprite?.width ?: 16, bgHeight: Int = bgSprite?.height ?: 16,
                                                     direction: Option<ComponentSpriteProgressBar, ComponentSpriteProgressBar.ProgressDirection> = Option(ComponentSpriteProgressBar.ProgressDirection.X_POS),
                                                     progress: Option<ComponentSpriteProgressBar, Float> = Option(1.0F))
    : GuiComponent<ComponentProgressBar>(x, y, bgWidth, bgHeight) {

    var backgroundComponent = ComponentSprite(bgSprite, 0, 0, bgWidth, bgHeight)
    var progressComponent = ComponentSpriteProgressBar(fgSprite, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, fgWidth, fgHeight)

    init {
        progressComponent.direction = direction
        progressComponent.progress = progress
        this.add(backgroundComponent, progressComponent)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        // NOP
    }

    var direction: Option<ComponentSpriteProgressBar, ComponentSpriteProgressBar.ProgressDirection>
        get() = progressComponent.direction
        set(value) {
            progressComponent.direction = value
        }

    var progress: Option<ComponentSpriteProgressBar, Float>
        get() = progressComponent.progress
        set(value) {
            progressComponent.progress = value
        }
}
