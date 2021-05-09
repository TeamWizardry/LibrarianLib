package com.teamwizardry.librarianlib.testcore.mixin;

import com.teamwizardry.librarianlib.testcore.bridge.InjectedTranslations;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = {TranslationStorage.class})
public class TranslationInjectionMixin {
    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void translateKeyPrivateHook(String key, CallbackInfoReturnable<String> cir) {
        if (Objects.equals(key, cir.getReturnValue())) {
            String injectedName = InjectedTranslations.INSTANCE.getTranslations().get(key);
            if (injectedName != null)
                cir.setReturnValue(injectedName);
        }
    }

    @Inject(method = "hasTranslation", at = @At("RETURN"), cancellable = true)
    private void keyExistsHook(String key, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ())
            cir.setReturnValue(InjectedTranslations.INSTANCE.getTranslations().containsKey(key));
    }
}
