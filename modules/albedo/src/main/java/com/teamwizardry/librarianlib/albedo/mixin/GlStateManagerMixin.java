package com.teamwizardry.librarianlib.albedo.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A mixin that allows directly modifying the GlStateManager's internal state
 */
@Mixin(GlStateManager.class)
public class GlStateManagerMixin {
    /**
     * <p>
     * Say we bind "foo" to <code>TEXTURE_1D</code>. Now say someone else binds "foo" normally. Since "foo" was put in
     * the cache, GlStateManager won't call <code>glBindTexture</code>, despite the fact that "foo" isn't bound to
     * <b><code>TEXTURE_2D.</code></b>
     * </p>
     *
     * <p>
     * The simplest way to avoid this is to pass -1 to the method, overwriting the internal state with -1, but abort
     * right before calling OpenGL.
     * </p>
     */
    @Inject(method = "_bindTexture", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V"), cancellable = true)
    private static void abortClearTexture(int texture, CallbackInfo ci) {
        if(texture == -1) {
            ci.cancel();
        }
    }
}
