package com.teamwizardry.librarianlib.testcore.mixin;

import com.teamwizardry.librarianlib.testcore.resources.RuntimeResources;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin({LifecycledResourceManagerImpl.class})
public class LifecycledResourceManagerImplMixin {
    @ModifyVariable(method = {"<init>"}, at = @At("HEAD"), argsOnly = true)
    private static List<ResourcePack> registerRRPs(List<ResourcePack> packs) {
        var copy = new ArrayList<>(packs);
        copy.add(RuntimeResources.INSTANCE);
        return copy;
    }
}
