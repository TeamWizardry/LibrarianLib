package com.teamwizardry.librarianlib.glitter.mixin;

import com.teamwizardry.librarianlib.glitter.GlitterLightingCache;
import com.teamwizardry.librarianlib.glitter.GlitterWorldCollider;
import com.teamwizardry.librarianlib.glitter.ParticleSystemManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
class MinecraftClientHooks {
    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        GlitterWorldCollider.INSTANCE.clearCaches();
        GlitterLightingCache.INSTANCE.clearCache();
        ParticleSystemManager.INSTANCE.clearParticles$glitter();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void disconnect(Screen screen, CallbackInfo ci) {
        GlitterWorldCollider.INSTANCE.clearCaches();
        GlitterLightingCache.INSTANCE.clearCache();
        ParticleSystemManager.INSTANCE.clearParticles$glitter();
    }
}
