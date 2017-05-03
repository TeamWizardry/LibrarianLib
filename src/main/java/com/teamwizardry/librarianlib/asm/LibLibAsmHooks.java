package com.teamwizardry.librarianlib.asm;

import com.teamwizardry.librarianlib.core.client.GlowingHandler;
import com.teamwizardry.librarianlib.core.client.RenderHookHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WireSegal
 *         Created at 11:34 AM on 4/29/17.
 */
@SuppressWarnings("unused")
public class LibLibAsmHooks {
    @SideOnly(Side.CLIENT)
    public static void renderHook(ItemStack stack, IBakedModel model) {
        RenderHookHandler.runItemHook(stack, model);
    }

    @SideOnly(Side.CLIENT)
    public static void renderHook(BlockModelRenderer blockModelRenderer, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, VertexBuffer vertexBuffer) {
        RenderHookHandler.runBlockHook(blockModelRenderer, world, model, state, pos, vertexBuffer);
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
