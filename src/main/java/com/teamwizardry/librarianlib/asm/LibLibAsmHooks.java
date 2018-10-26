package com.teamwizardry.librarianlib.asm;

import com.teamwizardry.librarianlib.core.client.GlowingHandler;
import com.teamwizardry.librarianlib.core.client.RenderHookHandler;
import com.teamwizardry.librarianlib.features.forgeevents.EntityPostUpdateEvent;
import com.teamwizardry.librarianlib.features.forgeevents.EntityUpdateEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WireSegal
 * Created at 11:34 AM on 4/29/17.
 */
@SuppressWarnings("unused")
public class LibLibAsmHooks {
    public static final LibLibAsmHooks INSTANCE = new LibLibAsmHooks();
    private static float x, y;

    @SideOnly(Side.CLIENT)
    public static void renderHook(ItemStack stack, IBakedModel model) {
        RenderHookHandler.runItemHook(stack, model);
    }

    @SideOnly(Side.CLIENT)
    public static void renderHook(BlockModelRenderer blockModelRenderer, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder vertexBuffer) {
        RenderHookHandler.runBlockHook(blockModelRenderer, world, model, state, pos, vertexBuffer);
    }

    @SideOnly(Side.CLIENT)
    public static void renderHook(BlockFluidRenderer blockFluidRenderer, IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder vertexBuffer) {
        RenderHookHandler.runFluidHook(blockFluidRenderer, world, state, pos, vertexBuffer);
    }

    @SideOnly(Side.CLIENT)
    public void maximizeGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow()) {
            x = OpenGlHelper.lastBrightnessX;
            y = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }
    }

    @SideOnly(Side.CLIENT)
    public void returnGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow())
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, x, y);
    }

    @SideOnly(Side.CLIENT)
    public boolean usePotionGlow() {
        return GlowingHandler.getPotionGlow();
    }

    public static boolean preUpdate(Entity entity) {
        if (entity instanceof EntityPlayerMP)
            return false;

        return MinecraftForge.EVENT_BUS.post(new EntityUpdateEvent(entity));
    }

    public static void postUpdate(Entity entity) {
        if (entity instanceof EntityPlayerMP)
            return;

        MinecraftForge.EVENT_BUS.post(new EntityPostUpdateEvent(entity));
    }

    public static boolean preUpdateMP(EntityPlayerMP entity) {
        return MinecraftForge.EVENT_BUS.post(new EntityUpdateEvent(entity));
    }

    public static void postUpdateMP(EntityPlayerMP entity) {
        MinecraftForge.EVENT_BUS.post(new EntityPostUpdateEvent(entity));
    }
}
