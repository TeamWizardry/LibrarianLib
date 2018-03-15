package com.teamwizardry.librarianlib.features.gui.provided.book

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.util.*

/**
 * @author WireSegal
 * Created at 9:08 AM on 2/27/18.
 */
@SideOnly(Side.CLIENT)
interface IBookGui {

    val mainBookComponent: GuiComponent

    val paperComponent: GuiComponent

    val bindingComponent: GuiComponent

    var focus: GuiComponent?

    val history: Stack<IBookElement>

    var currentElement: IBookElement?

    val book: Book

    val cachedSearchContent: Map<Entry, String>

    fun makeNavigationButton(offsetIndex: Int, entry: Entry, extra: ((ComponentVoid) -> Unit)?): GuiComponent

    val bindingSprite: Sprite

    val paperSprite: Sprite

    val pageSprite: Sprite

    val nextSpritePressed: Sprite

    val nextSprite: Sprite

    val backSpritePressed: Sprite

    val backSprite: Sprite

    val homeSpritePressed: Sprite

    val homeSprite: Sprite

    val bannerSprite: Sprite

    val bookmarkSprite: Sprite

    val searchIconSprite: Sprite

    val titleBarSprite: Sprite

    val processArrow: Sprite

    fun updateTextureData(sheet: String, outerColor: Color, bindingColor: Color)

    fun actualElement(): IBookElement? {
        val current = currentElement ?: return null
        return current.heldElement
    }

    fun placeInFocus(element: IBookElement) {
        if (element === actualElement())
            return

        val currentElement = currentElement
        if (currentElement != null)
            history.push(currentElement)
        forceInFocus(element)
    }

    fun forceInFocus(element: IBookElement) {
        if (element === actualElement())
            return

        focus?.invalidate()
        focus = element.createComponent(this)
        focus?.let { mainBookComponent.add(it) }
        currentElement = element
    }

    companion object {

        @JvmOverloads
        fun getRendererFor(icon: JsonElement?, size: Vec2d, mask: Boolean = false): () -> Unit {
            if (icon == null) return { }

            if (icon.isJsonPrimitive) {
                val iconLocation = ResourceLocation(icon.asString)
                val sprite = Sprite(ResourceLocation(iconLocation.resourceDomain,
                        "textures/" + iconLocation.resourcePath + ".png"))
                return { renderSprite(sprite, size, mask) }
            } else if (icon.isJsonObject) {
                val stack = CraftingHelper.getItemStack(icon.asJsonObject, JsonContext("minecraft"))
                if (!stack.isEmpty)
                    return { renderStack(stack, size) }
            }
            return { }
        }

        fun renderSprite(sprite: Sprite, size: Vec2d, mask: Boolean) {
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            if (!mask)
                GlStateManager.color(1f, 1f, 1f, 1f)

            sprite.tex.bind()
            sprite.draw(ClientTickHandler.partialTicks.toInt(), 0f, 0f, size.xi.toFloat(), size.yi.toFloat())

            GlStateManager.popMatrix()
        }

        fun renderStack(stack: ItemStack, size: Vec2d) {
            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.enableRescaleNormal()

            if (stack.isNotEmpty) {
                GlStateManager.pushMatrix()

                val itemRender = Minecraft.getMinecraft().renderItem
                itemRender.zLevel = 200.0f

                GlStateManager.scale(size.x / 16.0, size.y / 16.0, 1.0)

                val fr = (stack.item.getFontRenderer(stack) ?: Minecraft.getMinecraft().fontRenderer)
                itemRender.renderItemAndEffectIntoGUI(stack, 0, 0)
                itemRender.renderItemOverlayIntoGUI(fr, stack, 0, 0, null)

                itemRender.zLevel = 0.0f

                GlStateManager.popMatrix()
            }

            GlStateManager.disableRescaleNormal()
            RenderHelper.enableStandardItemLighting()
        }
    }
}
