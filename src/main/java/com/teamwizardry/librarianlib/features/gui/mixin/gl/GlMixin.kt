package com.teamwizardry.librarianlib.features.gui.mixin.gl

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.kotlin.glColor
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import java.awt.Color

object GlMixin {

    val TAG_ATTRIB = "mixin_attrib"
    val TAG_MATRIX = "mixin_matrix"

    fun pushPopAttrib(component: GuiComponent) {
        if (!component.addTag(TAG_ATTRIB))
            return

        component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
            GlStateManager.pushAttrib()
        }
        component.BUS.hook(GuiComponentEvents.PostDrawEvent::class.java) {
            GlStateManager.popAttrib()
        }
    }

    fun pushPopMatrix(component: GuiComponent) {
        if (!component.addTag(TAG_MATRIX))
            return

        component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
            GlStateManager.pushMatrix()
        }
        component.BUS.hook(GuiComponentEvents.PostDrawEvent::class.java) {
            GlStateManager.popMatrix()
        }
    }

    private fun getData(component: GuiComponent): GlMixinData {
        var dat = component.getData(GlMixinData::class.java, "mixin")

        if (dat == null) {
            dat = GlMixinData()
            component.setData(GlMixinData::class.java, "mixin", dat)
        }
        return dat
    }

    fun color(component: GuiComponent): Option<GuiComponent, Color> {
        pushPopAttrib(component)

        val opt = getData(component).getData("color", {
            val o = Option<GuiComponent, Color>(Color.WHITE)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                o.getValue(component).glColor()
            }
            o
        })

        return opt
    }

    fun transform(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("transform", {
            val o = Option<GuiComponent, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.translate(v.x, v.y, v.z)
            }
            o
        })

        return opt
    }

    fun scale(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("scale", {
            val o = Option<GuiComponent, Vec3d>(Vec3d(1.0, 1.0, 1.0))
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.scale(v.x, v.y, v.z)
            }
            o
        })

        return opt
    }

    fun rotate(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("rotate", {
            val o = Option<GuiComponent, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.rotate(v.x.toFloat(), 1f, 0f, 0f)
                GlStateManager.rotate(v.y.toFloat(), 0f, 1f, 0f)
                GlStateManager.rotate(v.z.toFloat(), 0f, 0f, 1f)
            }
            o
        })

        return opt
    }

    class GlMixinData {
        private val data: MutableMap<String, Option<GuiComponent, Any>> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> getData(key: String, init: () -> Option<GuiComponent, T>): Option<GuiComponent, T> {
            if (!data.containsKey(key))
                data.put(key, init() as Option<GuiComponent, Any>)
            return data[key] as Option<GuiComponent, T>
        }
    }
}
