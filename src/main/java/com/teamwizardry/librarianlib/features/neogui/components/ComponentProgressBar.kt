package com.teamwizardry.librarianlib.features.neogui.components

/*
 * Created by bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.neogui.value.IMValue
import com.teamwizardry.librarianlib.features.neogui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
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
                                                     bgWidth: Int = bgSprite?.width ?: 16, bgHeight: Int = bgSprite?.height ?: 16)
    : GuiComponent(x, y, bgWidth, bgHeight) {

    val direction_im: IMValue<ComponentSpriteProgressBar.ProgressDirection> = IMValue(ComponentSpriteProgressBar.ProgressDirection.X_POS)
    var direction: ComponentSpriteProgressBar.ProgressDirection by direction_im
    val progress_im: IMValueDouble = IMValueDouble(1.0)
    var progress: Double by progress_im

    var backgroundComponent = ComponentSprite(bgSprite, 0, 0, bgWidth, bgHeight)
    var progressComponent = ComponentSpriteProgressBar(fgSprite, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, fgWidth, fgHeight)

    init {
        progressComponent.direction_im { this.direction }
        progressComponent.progress_im { this.progress }
        this.add(backgroundComponent, progressComponent)
    }

    override fun draw(partialTicks: Float) {
        // NOP
    }
}
