package com.teamwizardry.librarianlib.mirage.mixin;

import com.teamwizardry.librarianlib.mirage.Mirage;
import com.teamwizardry.librarianlib.mirage.VirtualResourceManager;
import com.teamwizardry.librarianlib.mirage.bridge.MirageMixinBridge;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FallbackResourceManager.class)
public class FallbackResourceManagerMixin {
    @Shadow @Final public List<IResourcePack> resourcePacks;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initHook(ResourcePackType type, String namespace, CallbackInfo ci) {
        if(type != null) {
            resourcePacks.add(MirageMixinBridge.INSTANCE.getResourcePack());
        }
    }
}
