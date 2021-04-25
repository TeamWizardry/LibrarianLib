package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.core.util.GlResourceGc;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract boolean isPaused();

    @Shadow private float pausedTickDelta;

    @Inject(method = "render", at = @At("HEAD"))
    public void runGlResourceGc(boolean renderWorldIn, CallbackInfo ci) {
        GlResourceGc.INSTANCE.releaseCollectedResources$core();

        if(isPaused()) {
            // when the game is unpaused the delta will be updated by the RenderTickCounter
            Client.getWorldTime().updateTickDelta(pausedTickDelta);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void updateGameTicks(CallbackInfo ci) {
        Client.getTime().trackTick();
        if(!isPaused()) {
            Client.getWorldTime().trackTick();
        }
    }
}
