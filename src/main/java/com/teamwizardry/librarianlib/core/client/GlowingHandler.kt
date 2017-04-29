package com.teamwizardry.librarianlib.core.client

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 1:55 PM on 4/29/17.
 */
@SideOnly(Side.CLIENT)
object GlowingHandler {

    val renderModel = MethodHandleHelper.wrapperForMethod(RenderItem::class.java, arrayOf("renderModel", "func_175045_a", "a"), IBakedModel::class.java, ItemStack::class.java)

    @JvmStatic
    fun glow(stack: ItemStack, model: IBakedModel) {
        val item = stack.item as? IGlowingItem ?: return
        val newModel = item.transformToGlow(stack, model)
        if (newModel != null) {
            val prevX = OpenGlHelper.lastBrightnessX
            val prevY = OpenGlHelper.lastBrightnessY
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
            if (item.shouldDisableLightingForGlow(stack, model)) GlStateManager.disableLighting()
            renderModel(Minecraft.getMinecraft().renderItem, arrayOf(newModel, stack))
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY)
            if (item.shouldDisableLightingForGlow(stack, model)) GlStateManager.enableLighting()
        }
    }
}
