package com.teamwizardry.librarianlib.albedo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.class)
public interface RenderSystemMixin {
    /**
     * This method isn't named `getShaderLightDirections` because for god knows what reason doing that prevents me from
     * hot swapping code across the entire project.
     */
    @Accessor
    public static Vec3f[] getShaderLightDirections() {
        throw new UnsupportedOperationException();
    }
}
