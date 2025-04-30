package com.teamwizardry.librarianlib.albedo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.class)
public interface RenderSystemMixin {
    @Accessor(remap = false)
    public static Vector3f[] getShaderLightDirections() {
        throw new UnsupportedOperationException();
    }
}
