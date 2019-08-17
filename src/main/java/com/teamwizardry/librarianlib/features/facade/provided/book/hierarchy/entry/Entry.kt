package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.facade.provided.book.context.PaginationContext
import com.teamwizardry.librarianlib.features.facade.provided.book.helper.TranslationHolder
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.IBookElement
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.book.Book
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.ICriterion
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game.EntryUnlockedEvent
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.page.Page
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
class Entry(override val bookParent: Book, parentSheet: String, parentOuter: Color, parentBinding: Color, rl: String, json: JsonObject) : IBookElement {

    val pages: List<Page>
    val title: TranslationHolder?
    val desc: TranslationHolder?
    val icon: JsonElement

    val criterion: ICriterion?

    val sheet: String
    val outerColor: Color
    val bindingColor: Color

    var isValid = false

    init {
        ENTRIES[ResourceLocation(rl)] = this

        val pages = mutableListOf<Page>()
        val baseKey = bookParent.location.namespace + "." +
                bookParent.location.path + "." +
                rl.replace("^.*/(?=\\w+)".toRegex(), "")
        var title: TranslationHolder? = TranslationHolder("$baseKey.title")
        var desc: TranslationHolder? = TranslationHolder("$baseKey.description")
        var icon: JsonElement = JsonObject()
        var criterion: ICriterion? = null
        var sheet: String = parentSheet
        var outerColor: Color = parentOuter
        var bindingColor: Color = parentBinding
        try {
            if (json.has("title"))
                title = TranslationHolder.fromJson(json.get("title"))
            if (json.has("description"))
                desc = TranslationHolder.fromJson(json.get("description"))
            icon = json.get("icon")
            if (json.has("criteria"))
                criterion = ICriterion.fromJson(json.get("criteria"))

            if (json.has("style")) {
                val obj = json.getAsJsonObject("style")
                if (obj.has("sheet"))
                    sheet = obj.getAsJsonPrimitive("sheet").asString
                if (obj.has("color"))
                    outerColor = Book.colorFromJson(obj.get("color"))
                if (obj.has("binding"))
                    bindingColor = Book.colorFromJson(obj.get("binding"))
            }

            isValid = true
        } catch (exception: Exception) {
            LibrarianLog.error(exception, "Failed trying to parse an entry component")
        }

        this.title = title
        this.desc = desc
        this.icon = icon
        this.criterion = criterion
        this.sheet = sheet
        this.outerColor = outerColor
        this.bindingColor = bindingColor

        if (isValid) {
            try {
                isValid = false
                val allPages = json.getAsJsonArray("content")
                allPages.mapNotNullTo(pages) { Page.fromJson(this, it) }
                isValid = pages.isNotEmpty()
            } catch (exception: Exception) {
                LibrarianLog.error(exception, "Failed trying to parse an entry component")
            }
        }

        this.pages = pages
    }

    fun isUnlocked(player: EntityPlayer): Boolean {
        if (criterion == null) return true

        val event = EntryUnlockedEvent(player, this)
        MinecraftForge.EVENT_BUS.post(event)

        return criterion.isUnlocked(player, event.result)
    }

    @SideOnly(Side.CLIENT)
    override fun createComponents(book: IBookGui): List<PaginationContext> {
        book.updateTextureData(sheet, outerColor, bindingColor)

        val title = title.toString()

        val xSize = book.mainBookComponent.size.xi - 32
        val ySize = book.mainBookComponent.size.yi - 32
        val size = vec(xSize, ySize)

        val pageComponents = mutableListOf<PaginationContext>()

        for (page in pages) {
            for (component in page.createBookComponents(book, size)) {
                pageComponents.add(PaginationContext({
                    val holderComponent = component()

                    val titleBar = ComponentSprite(book.titleBarSprite,
                            16 + xSize / 2 - book.titleBarSprite.width / 2, -31)
                    titleBar.color = book.book.bookColor
                    holderComponent.add(titleBar)

                    val titleText = ComponentText(titleBar.size.xi / 2 - 12, titleBar.size.yi / 2 + 1, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE)
                    titleText.text = title
                    titleText.color = book.book.entryTitleTextColor
                    titleBar.add(titleText)

                    holderComponent
                }, page.extraBookmarks))
            }
        }

        return pageComponents
    }

    companion object {
        val ENTRIES: MutableMap<ResourceLocation, Entry> = mutableMapOf()
    }
}
