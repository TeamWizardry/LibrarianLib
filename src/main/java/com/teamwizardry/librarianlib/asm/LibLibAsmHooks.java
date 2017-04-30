package com.teamwizardry.librarianlib.asm;

import com.teamwizardry.librarianlib.core.client.GlowingHandler;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WireSegal
 *         Created at 11:34 AM on 4/29/17.
 */
@SuppressWarnings("unused")
public class LibLibAsmHooks {
    @SideOnly(Side.CLIENT)
    public static void renderGlow(ItemStack stack, IBakedModel model) {
        GlowingHandler.glow(stack, model);
    }

    private static float x, y;

    @SideOnly(Side.CLIENT)
    public static void maximizeGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow()) {
            x = OpenGlHelper.lastBrightnessX;
            y = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void returnGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow())
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, x, y);
    }
}
