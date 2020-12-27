package com.teamwizardry.librarianlib.foundation.mixin;

import com.teamwizardry.librarianlib.foundation.bridge.ICustomSignMaterialBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignTileEntityRenderer.class)
public class SignTileEntityRendererMixin {
    @Inject(method = "getMaterial(Lnet/minecraft/block/Block;)Lnet/minecraft/client/renderer/model/Material;", at = @At("HEAD"), cancellable = true)
    private static void getMaterial(Block block, CallbackInfoReturnable<Material> info) {
        if (block instanceof ICustomSignMaterialBlock) {
            info.setReturnValue(((ICustomSignMaterialBlock) block).signMaterial());
        }
    }
}
