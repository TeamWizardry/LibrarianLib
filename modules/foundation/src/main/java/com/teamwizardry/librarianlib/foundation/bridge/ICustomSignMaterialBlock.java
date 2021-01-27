package com.teamwizardry.librarianlib.foundation.bridge;

import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public interface ICustomSignMaterialBlock {
    @OnlyIn(Dist.CLIENT)
    @NotNull RenderMaterial signMaterial();
}
