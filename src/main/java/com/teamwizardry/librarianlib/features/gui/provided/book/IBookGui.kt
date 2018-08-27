package com.teamwizardry.librarianlib.features.gui.provided.book

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.context.BookContext
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentNavBar
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
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

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

    var context: BookContext

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

    val materialIcon: Sprite

    val navBar: ComponentNavBar

    fun updateTextureData(sheet: String, outerColor: Color, bindingColor: Color)

    fun placeInFocus(element: IBookElement) {
        if (element == context.bookElement)
            return

        focusOn(BookContext(this, element))
    }

    fun forceInFocus(element: IBookElement) {
        if (element == context.bookElement)
            return

        focusOn(BookContext(this, element, context.parent))
    }

    fun focusOn(newContext: BookContext): BookContext {
        focus?.invalidate()
        context = newContext

        val page = context.pages[context.position]
        val focusComponent = page.component()
        focus = focusComponent
        mainBookComponent.add(focusComponent)

        var bookmarkIndex = 0
        for (bookmark in context.bookmarks)
            focusComponent.add(bookmark.createBookmarkComponent(this, bookmarkIndex++))
        for (bookmark in page.pageBookmarks)
            focusComponent.add(bookmark.createBookmarkComponent(this, bookmarkIndex++))

        return newContext
    }

    fun changePage(to: Int) {
        context.position = MathHelper.clamp(to, 0, context.pages.size)
        focusOn(context)
    }

    fun up() {
        val prev = context
        val parent = prev.parent
        if (parent != null)
            focusOn(parent)
    }

    companion object {

        @JvmOverloads
        fun getRendererFor(icon: JsonElement?, size: Vec2d, mask: Boolean = false): () -> Unit {
            if (icon == null) return { }

            if (icon.isJsonPrimitive) {
                val iconLocation = ResourceLocation(icon.asString)
                val sprite = Sprite(ResourceLocation(iconLocation.namespace,
                        "textures/" + iconLocation.path + ".png"))
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
        }
    }
}
