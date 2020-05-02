package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.client.renderer.RenderType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * A wrapper around a block's [RenderType].
 */
class RenderLayerSpec private constructor(private val getter: () -> Any) {

    @OnlyIn(Dist.CLIENT)
    fun getRenderType(): RenderType {
        return getter() as RenderType
    }

    companion object {
        @JvmStatic
        val SOLID: RenderLayerSpec = RenderLayerSpec { RenderType.getSolid() }
        @JvmStatic
        val CUTOUT: RenderLayerSpec = RenderLayerSpec { RenderType.getCutout() }
        @JvmStatic
        val CUTOUT_MIPPED: RenderLayerSpec = RenderLayerSpec { RenderType.getCutoutMipped() }
        @JvmStatic
        val TRANSLUCENT: RenderLayerSpec = RenderLayerSpec { RenderType.getTranslucent() }
    }
}
