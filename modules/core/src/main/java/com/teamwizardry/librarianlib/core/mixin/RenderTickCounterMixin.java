package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.util.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
class RenderTickCounterMixin {
    @Shadow public float tickDelta;

    @Inject(method = "beginRenderTick", at = @At("RETURN"))
    public void updateTickDeltas(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        Client.getTime().updateTickDelta(tickDelta);
        if(!MinecraftClient.getInstance().isPaused()) {
            // when the game is paused the delta will be updated at the start of the MinecraftClient.render method
            Client.getWorldTime().updateTickDelta(tickDelta);
        }
    }
}
