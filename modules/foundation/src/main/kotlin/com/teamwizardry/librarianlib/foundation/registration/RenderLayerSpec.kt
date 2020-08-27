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
        @JvmField
        val SOLID: RenderLayerSpec = RenderLayerSpec { RenderType.getSolid() }
        @JvmField
        val CUTOUT: RenderLayerSpec = RenderLayerSpec { RenderType.getCutout() }
        @JvmField
        val CUTOUT_MIPPED: RenderLayerSpec = RenderLayerSpec { RenderType.getCutoutMipped() }
        @JvmField
        val TRANSLUCENT: RenderLayerSpec = RenderLayerSpec { RenderType.getTranslucent() }
    }
}
