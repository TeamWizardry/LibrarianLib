package com.teamwizardry.librarianlib.albedo.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightmapTextureManager.class)
public interface LightmapTextureManagerMixin {
    @Accessor NativeImageBackedTexture getTexture();
}
