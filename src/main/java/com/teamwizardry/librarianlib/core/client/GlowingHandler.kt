package com.teamwizardry.librarianlib.core.client

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.shader.Shader
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
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
        if (stack.item is IGlowingItem) {
            val newModel = (stack.item as IGlowingItem).transformToGlow(stack, model)
            if (newModel != null) {
                val prevX = OpenGlHelper.lastBrightnessX
                val prevY = OpenGlHelper.lastBrightnessY
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
                renderModel(Minecraft.getMinecraft().renderItem, arrayOf(newModel, stack))
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY)
            }
        }
    }
}
