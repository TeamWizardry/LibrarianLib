package com.teamwizardry.librarianlib.virtualresources.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "run()V", at = @At("HEAD"))
    public void runMixin(CallbackInfo ci) {
        System.out.println("WHEEEEEEE!!! LOOK AT ME I'M IN A MIXIN!");
    }
}
