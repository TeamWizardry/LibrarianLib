package com.teamwizardry.librarianlib.albedo.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.teamwizardry.librarianlib.albedo.bridge.GlStateManagerExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;

/**
 * A mixin that allows directly modifying the GlStateManager's internal state
 */
@Mixin(GlStateManager.class)
public class GlStateInjector {
    /**
     * Say we bind "foo" to `TEXTURE_1D`. Now say someone else binds "foo" normally. Since "foo" was put in the cache,
     * GlStateManager won't call `glBindTexture`, *despite the fact that "foo" isn't bound to `TEXTURE_2D`.*
     *
     * We have to set the cache to -1 so it doesn't do this.
     */
    @Redirect(method = "_bindTexture", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/platform/GlStateManager$Texture2DState;boundTexture:I"))
    private static int injectCacheAssignment(int value) {
        int customTextureTarget = GlStateManagerExtensions.customTextureTarget;
        return customTextureTarget >= 0 ? -1 : value;
    }

    /**
     * Minecraft hard-codes `TEXTURE_2D`, so we have to modify that
     */
    @ModifyConstant(method = "_bindTexture", constant = { @Constant(intValue = 3553) })
    private static int injectTextureTarget(int value) {
        int customTextureTarget = GlStateManagerExtensions.customTextureTarget;
        return customTextureTarget >= 0 ? customTextureTarget : value;
    }
}
