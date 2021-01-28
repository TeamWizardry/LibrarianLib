package com.teamwizardry.librarianlib.mirage.mixin;

import com.teamwizardry.librarianlib.mirage.Mirage;
import com.teamwizardry.librarianlib.mirage.VirtualResourceManager;
import com.teamwizardry.librarianlib.mirage.bridge.MirageMixinBridge;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin {
    @Shadow
    @Final
    private ResourcePackType type;

    @Inject(method = "getResource", at = @At(value = "NEW", target = "java/io/FileNotFoundException"), cancellable = true)
    private void getResourceMixin(ResourceLocation resourceLocationIn, CallbackInfoReturnable<IResource> cir) throws IOException {
        if (type != null)
            cir.setReturnValue(MirageMixinBridge.INSTANCE.fallbackManager(type).getResource(resourceLocationIn));
    }

    @Inject(method = "hasResource", at = @At(value = "RETURN"), cancellable = true)
    private void hasResourceMixin(ResourceLocation path, CallbackInfoReturnable<Boolean> cir) {
        if (type != null && !cir.getReturnValueZ())
            cir.setReturnValue(MirageMixinBridge.INSTANCE.fallbackManager(type).hasResource(path));
    }

    @Inject(method = "getAllResources", at = @At(value = "NEW", target = "java/io/FileNotFoundException"), cancellable = true)
    private void getAllResourcesMixin(ResourceLocation resourceLocationIn, CallbackInfoReturnable<List<IResource>> cir) throws IOException {
        if (type != null)
            cir.setReturnValue(MirageMixinBridge.INSTANCE.fallbackManager(type).getAllResources(resourceLocationIn));

    }
}
