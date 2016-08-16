package com.teamwizardry.librarianlib.book.gui

import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.book.util.Page
import com.teamwizardry.librarianlib.data.DataNode
import com.teamwizardry.librarianlib.data.DataNodeParsers
import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.components.Component3DView
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteTiled
import com.teamwizardry.librarianlib.gui.components.ComponentStructure
import com.teamwizardry.librarianlib.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.gui.mixin.gl.GlMixin
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.structure.Structure
import com.teamwizardry.librarianlib.util.Color
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

class PageStructure(book: Book, rootData: DataNode, pageData: DataNode, page: Page) : GuiBook(book, rootData, pageData, page) {

    protected var originState: IBlockState

    init {

        originState = DataNodeParsers.parseBlockState(pageData.get("block"))

        val structure = Structure(ResourceLocation(pageData.get("structure").asStringOr("minecraft:missingno")))
        val structureTransparent = Structure(ResourceLocation(pageData.get("structure").asStringOr("minecraft:missingno")))

        val view = Component3DView(0, 0, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT)
        view.rotX = 22.0
        view.rotY = 45.0
        view.zoom = 10.0

        view.offset = view.offset.add(Vec3d(-0.5, 0.5, -0.5))

        val structureComp = ComponentStructure(0, 0, structure)
        val structureCompTransparent = ComponentStructure(0, 0, structureTransparent)
        structureCompTransparent.color.setValue(Color(1f, 1f, 1f, 0.5f))

        view.add(structureComp)
        view.add(structureCompTransparent)

        contents.add(view)

        val hudScale = 3

        val tiled = ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0)
        contents.add(tiled)

        tiled.BUS.hook(GuiComponent.PreDrawEvent::class.java) { GlStateManager.depthFunc(GL11.GL_ALWAYS) }
        tiled.BUS.hook(GuiComponent.PreChildrenDrawEvent::class.java) { GlStateManager.depthFunc(GL11.GL_LEQUAL) }

        tiled.size = Vec2d(
                ((structure.max.x - structure.min.x + 1) * hudScale + 12).toDouble(),
                ((structure.max.y - structure.min.y + 1) * hudScale + 12).toDouble())

        val structureHud = Structure(ResourceLocation(pageData.get("structure").asStringOr("minecraft:missingno")))

        val structureCompHud = ComponentStructure(0, 0, structureHud)

        GlMixin.scale(structureCompHud).setValue(Vec3d(hudScale.toDouble(), hudScale.toDouble(), hudScale.toDouble()))
        GlMixin.transform(structureCompHud).setValue(Vec3d(structureHud.origin.x + 1 + 2.toDouble(), structureHud.max.y - structureHud.origin.y + 1 + 2.toDouble(), 50.0))
        GlMixin.rotate(structureCompHud).setValue(Vec3d(0.0, 0.0, 180.0))
        tiled.add(structureCompHud)

        val structureTransparentHud = Structure(ResourceLocation(pageData.get("structure").asStringOr("minecraft:missingno")))

        val structureCompHudTransparent = ComponentStructure(0, 0, structureTransparentHud)
        structureCompHudTransparent.color.setValue(Color(1f, 1f, 1f, 0.5f))

        GlMixin.scale(structureCompHudTransparent).setValue(Vec3d(hudScale.toDouble(), hudScale.toDouble(), hudScale.toDouble()))
        GlMixin.transform(structureCompHudTransparent).setValue(Vec3d(structureTransparentHud.origin.x + 1 + 2.toDouble(), structureTransparentHud.max.y - structureTransparentHud.origin.y + 1 + 2.toDouble(), 50.0))
        GlMixin.rotate(structureCompHudTransparent).setValue(Vec3d(0.0, 0.0, 180.0))
        tiled.add(structureCompHudTransparent)

        val clickArea = ComponentVoid(6, 6)

        clickArea.size = tiled.size.sub(12.0, 12.0)

        clickArea.BUS.hook(GuiComponent.MouseClickEvent::class.java) { event ->
            if (event.component.mouseOver) {
                val y = (event.component.size.yi - event.mousePos.yi + 1) / 3

                setClipY(y, structureTransparent)
                setClipY(y, structureTransparentHud)

                setOnlyY(y, structure)
                setOnlyY(y, structureHud)

                structureComp.initStructure()
                structureCompHud.initStructure()

                structureCompTransparent.initStructure()
                structureCompHudTransparent.initStructure()
            }
        }

        tiled.add(clickArea)

        setClipY(Integer.MIN_VALUE, structureTransparent)
        setClipY(Integer.MIN_VALUE, structureTransparentHud)

        setOnlyY(Integer.MIN_VALUE, structure)
        setOnlyY(Integer.MIN_VALUE, structureHud)
    }

    fun setClipY(y: Int, structure: Structure) {
        structure.blockAccess.resetSetBlocks()

        structure.blockAccess.setBlockState(structure.origin, originState)
        if (y == Integer.MIN_VALUE) {
            return
        }

        for (info in structure.blockInfos()) {
            if (info.pos.y >= y) {
                structure.blockAccess.setBlockState(info.pos, Blocks.AIR.defaultState)
            } else {
                info.pos.y
            }
        }
    }

    fun setOnlyY(y: Int, structure: Structure) {
        structure.blockAccess.resetSetBlocks()

        structure.blockAccess.setBlockState(structure.origin, originState)
        if (y == Integer.MIN_VALUE) {
            return
        }

        for (info in structure.blockInfos()) {
            if (info.pos.y != y) {
                structure.blockAccess.setBlockState(info.pos, Blocks.AIR.defaultState)
            } else {
                info.pos.y
            }
        }
    }
}
