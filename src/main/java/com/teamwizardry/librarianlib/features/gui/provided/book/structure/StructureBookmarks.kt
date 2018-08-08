package com.teamwizardry.librarianlib.features.gui.provided.book.structure

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark
import com.teamwizardry.librarianlib.features.structure.dynamic.STRUCTURE_REGISTRY
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 12:18 PM on 8/8/18.
 */
class BookmarkRenderableStructure(val structure: () -> RenderableStructure?) : Bookmark {

    @SideOnly(Side.CLIENT)
    override fun createBookmarkComponent(book: IBookGui, bookmarkIndex: Int) =
            ComponentMaterialsBar(book, bookmarkIndex, StructureMaterials(structure()))
}

class BookmarkDynamicStructure(val structure: ResourceLocation) : Bookmark {

    private val builtin = STRUCTURE_REGISTRY.getObjectByName(structure)

    @SideOnly(Side.CLIENT)
    override fun createBookmarkComponent(book: IBookGui, bookmarkIndex: Int) =
            ComponentMaterialsBar(book, bookmarkIndex, StructureMaterials(builtin))
}
