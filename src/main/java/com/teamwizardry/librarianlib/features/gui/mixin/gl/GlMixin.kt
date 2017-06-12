package com.teamwizardry.librarianlib.features.gui.mixin.gl

import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.kotlin.x
import com.teamwizardry.librarianlib.features.kotlin.y
import com.teamwizardry.librarianlib.features.kotlin.z
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import java.awt.Color

object GlMixin {

    val TAG_ATTRIB = "mixin_attrib"
    val TAG_MATRIX = "mixin_matrix"

    fun <T : GuiComponent<T>> pushPopAttrib(component: GuiComponent<T>) {
        if (!component.addTag(TAG_ATTRIB))
            return

        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
            GlStateManager.pushAttrib()
        }
        component.BUS.hook(GuiComponent.PostDrawEvent::class.java) {
            GlStateManager.popAttrib()
        }
    }

    fun <T : GuiComponent<T>> pushPopMatrix(component: GuiComponent<T>) {
        if (!component.addTag(TAG_MATRIX))
            return

        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
            GlStateManager.pushMatrix()
        }
        component.BUS.hook(GuiComponent.PostDrawEvent::class.java) {
            GlStateManager.popMatrix()
        }
    }

    private fun getData(component: GuiComponent<*>): GlMixinData {
        var dat = component.getData(GlMixinData::class.java, "mixin")

        if (dat == null) {
            dat = GlMixinData()
            component.setData(GlMixinData::class.java, "mixin", dat)
        }
        return dat
    }

    fun <T : GuiComponent<T>> color(component: T): Option<T, Color> {
        pushPopAttrib(component)

        val opt = getData(component).getData("color", {
            val o = Option<T, Color>(Color.WHITE)
            component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
                o.getValue(component).glColor()
            }
            o
        })

        return opt
    }

    fun <T : GuiComponent<T>> transform(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("transform", {
            val o = Option<T, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.translate(v.x, v.y, v.z)
            }
            o
        })

        return opt
    }

    fun <T : GuiComponent<T>> scale(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("scale", {
            val o = Option<T, Vec3d>(Vec3d(1.0, 1.0, 1.0))
            component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.scale(v.x, v.y, v.z)
            }
            o
        })

        return opt
    }

    fun <T : GuiComponent<T>> rotate(component: T): Option<T, Vec3d> {
        pushPopMatrix(component)

        val opt = getData(component).getData("rotate", {
            val o = Option<T, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponent.PreDrawEvent::class.java) {
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
        private val data: MutableMap<String, Option<GuiComponent<*>, Any>> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <COMP : GuiComponent<COMP>, OPT : Any> getData(key: String, init: () -> Option<COMP, OPT>): Option<COMP, OPT> {
            if (!data.containsKey(key))
                data.put(key, init() as Option<GuiComponent<*>, Any>)
            return data[key] as Option<COMP, OPT>
        }
    }
}
