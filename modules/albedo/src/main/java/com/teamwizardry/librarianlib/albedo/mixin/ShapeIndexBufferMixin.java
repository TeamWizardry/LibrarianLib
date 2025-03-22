package com.teamwizardry.librarianlib.albedo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.ShapeIndexBuffer.class)
public interface ShapeIndexBufferMixin {
    @Accessor
    public VertexFormat.IndexType getIndexType();
}
