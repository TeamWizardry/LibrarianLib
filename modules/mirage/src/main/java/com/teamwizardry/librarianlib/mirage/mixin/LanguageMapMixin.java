package com.teamwizardry.librarianlib.mirage.mixin;

import com.teamwizardry.librarianlib.mirage.Mirage;
import net.minecraft.client.resources.ClientLanguageMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * {@code net.minecraft.util.text.LanguageMap$1} is the anonymous LanguageMap returned by
 * {@code LanguageMap.func_240595_c_()}
 */
@Mixin(value = {ClientLanguageMap.class}, targets = {"net.minecraft.util.text.LanguageMap$1"})
public class LanguageMapMixin {
    @Inject(method = "func_230503_a_(Ljava/lang/String;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void translateKeyPrivateHook(String key, CallbackInfoReturnable<String> cir) {
        if (Objects.equals(key, cir.getReturnValue())) {
            String mirageName = Mirage.languageMap.getMixinBridge().tryTranslateKey(key);
            if (mirageName != null)
                cir.setReturnValue(mirageName);
        }
    }

    @Inject(method = "func_230506_b_(Ljava/lang/String;)Z", at = @At("RETURN"), cancellable = true)
    private void keyExistsHook(String key, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ())
            cir.setReturnValue(Mirage.languageMap.getMixinBridge().keyExists(key));
    }
}
