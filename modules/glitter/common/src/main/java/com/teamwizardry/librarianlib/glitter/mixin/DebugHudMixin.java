package com.teamwizardry.librarianlib.glitter.mixin;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.ParticleSystemManager;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At(value = "RETURN"))
    private void addSystemsText(CallbackInfoReturnable<List<String>> cir) {
        List<String> leftText = cir.getReturnValue();
        List<ParticleSystem> systems = ParticleSystemManager.INSTANCE.getSystems();
        if (systems.stream().noneMatch(particleSystem -> particleSystem.getParticleCount() > 0))
            return;

        leftText.add("LibrarianLib Glitter:");
        long total = 0;
        for (ParticleSystem system : systems) {
            if(system.getParticleCount() > 0)
                leftText.add(" - " + system.getName() + ": " + system.getParticleCount());
            total += system.getParticleCount();
        }
        leftText.add(" - total: " + total);
    }
}
