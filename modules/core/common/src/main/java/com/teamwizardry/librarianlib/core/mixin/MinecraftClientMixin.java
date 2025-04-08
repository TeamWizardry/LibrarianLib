package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.core.util.GlResourceGc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
    @Shadow public abstract boolean isPaused();

    @Shadow public abstract RenderTickCounter getRenderTickCounter();

    @Inject(method = "render", at = @At("HEAD"))
    public void runGlResourceGc(boolean renderWorldIn, CallbackInfo ci) {
        GlResourceGc.INSTANCE.releaseCollectedResourcesInternal();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;beginRenderTick(JZ)I",
                    shift = At.Shift.AFTER
            )
    )
    public void updatePartialTicks(boolean renderWorldIn, CallbackInfo ci) {
        Client.getTime().updateTickDelta(getRenderTickCounter().getTickDelta(true));
        Client.getWorldTime().updateTickDelta(getRenderTickCounter().getTickDelta(false));
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void updateGameTicks(CallbackInfo ci) {
        Client.getTime().trackTick();
        if(!isPaused()) {
            Client.getWorldTime().trackTick();
        }
    }
}
