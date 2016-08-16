package com.teamwizardry.librarianlib.gui.mixin.gl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.gui.Option
import com.teamwizardry.librarianlib.util.Color

object GlMixin {

    val TAG_ATTRIB = "mixin_attrib"
    val TAG_MATRIX = "mixin_matrix"

    fun <T: GuiComponent<T>> pushPopAttrib(component: GuiComponent<T>) {
        if (!component.addTag(TAG_ATTRIB))
            return

        component.preDraw.add({ c, pos, partialTicks -> GlStateManager.pushAttrib() })
        component.postDraw.addFirst({ c, pos, partialTicks -> GlStateManager.popAttrib() })
    }

    fun <T: GuiComponent<T>> pushPopMatrix(component: GuiComponent<T>) {
        if (!component.addTag(TAG_MATRIX))
            return

        component.preDraw.add({ c, pos, partialTicks -> GlStateManager.pushMatrix() })
        component.postDraw.addFirst({ c, pos, partialTicks -> GlStateManager.popMatrix() })
    }

    fun <T : GuiComponent<T>> color(component: T): Option<T, Color> {
        pushPopAttrib(component)

        val opt = Option<T, Color>(Color.WHITE)
        component.preDraw.add({ c, pos, partialTicks -> opt.getValue(component).glColor() })

        return opt
    }

    fun <T : GuiComponent<T>> transform(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = Option<T, Vec3d>(Vec3d.ZERO)
        component.preDraw.add({ c, pos, partialTicks ->
            val v = opt.getValue(component)
            GlStateManager.translate(v.xCoord, v.yCoord, v.zCoord)
        })

        return opt
    }

    fun <T : GuiComponent<T>> scale(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = Option<T, Vec3d>(Vec3d.ZERO)
        component.preDraw.add({ c, pos, partialTicks ->
            val v = opt.getValue(component)
            GlStateManager.scale(v.xCoord, v.yCoord, v.zCoord)
        })

        return opt
    }

    fun <T : GuiComponent<T>> rotate(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = Option<T, Vec3d>(Vec3d.ZERO)
        component.preDraw.add({ c, pos, partialTicks ->
            val v = opt.getValue(component)
            GlStateManager.rotate(v.xCoord.toFloat(), 1f, 0f, 0f)
            GlStateManager.rotate(v.yCoord.toFloat(), 0f, 1f, 0f)
            GlStateManager.rotate(v.zCoord.toFloat(), 0f, 0f, 1f)
        })

        return opt
    }
}
