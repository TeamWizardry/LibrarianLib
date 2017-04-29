package com.teamwizardry.librarianlib.core.client

import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.client.GlUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 1:55 PM on 4/29/17.
 */
@SideOnly(Side.CLIENT)
object GlowingHandler {

    private val renderModel = MethodHandleHelper.wrapperForMethod(RenderItem::class.java, arrayOf("renderModel", "func_175045_a", "a"), IBakedModel::class.java, ItemStack::class.java)

    private val renderSpecialHandlers = mutableMapOf<Item, IGlowingItem>()

    @JvmStatic
    @JvmOverloads
    fun registerCustomGlowHandler(item: Item,
                        modelTransformer: (ItemStack, IBakedModel) -> IBakedModel?,
                        shouldDisableLighting: ((ItemStack, IBakedModel) -> Boolean) = { _, _ -> false }) {
        renderSpecialHandlers.put(item, object : IGlowingItem {
            override fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel? {
                return modelTransformer(itemStack, model)
            }

            override fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel): Boolean {
                return shouldDisableLighting(itemStack, model)
            }
        })
    }

    @JvmStatic
    fun glow(stack: ItemStack, model: IBakedModel) {
        val item = stack.item as? IGlowingItem ?: renderSpecialHandlers[stack.item]

        if (item != null) {
            val newModel = item.transformToGlow(stack, model)
            if (newModel != null) GlUtils.withLighting(!item.shouldDisableLightingForGlow(stack, model)) {
                GlUtils.useLightmap(240f, 240f) {
                    renderModel(Minecraft.getMinecraft().renderItem, arrayOf(newModel, stack))
                }
            }
        }
    }
}
