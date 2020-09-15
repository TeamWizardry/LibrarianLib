package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.client.renderer.RenderType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * A wrapper around a block's [RenderType].
 */
public class RenderLayerSpec private constructor(private val getter: () -> Any) {

    @OnlyIn(Dist.CLIENT)
    public fun getRenderType(): RenderType {
        return getter() as RenderType
    }

    public companion object {
        @JvmField
        public val SOLID: RenderLayerSpec = RenderLayerSpec { RenderType.getSolid() }
        @JvmField
        public val CUTOUT: RenderLayerSpec = RenderLayerSpec { RenderType.getCutout() }
        @JvmField
        public val CUTOUT_MIPPED: RenderLayerSpec = RenderLayerSpec { RenderType.getCutoutMipped() }
        @JvmField
        public val TRANSLUCENT: RenderLayerSpec = RenderLayerSpec { RenderType.getTranslucent() }
    }
}
