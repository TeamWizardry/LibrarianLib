package com.teamwizardry.librarianlib.gui.mixin.gl

/*
object GlMixin {

    val TAG_MATRIX = "mixin_matrix"

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

        return getData(component).getData("color") {
            val o = Option<GuiComponent, Color>(Color.WHITE)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                o.getValue(component).glColor()
            }
            o
        }
    }

    fun transform(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        return getData(component).getData("transform") {
            val o = Option<GuiComponent, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.translate(v.x, v.y, v.z)
            }
            o
        }
    }

    fun scale(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        return getData(component).getData("scale") {
            val o = Option<GuiComponent, Vec3d>(Vec3d(1.0, 1.0, 1.0))
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.scale(v.x, v.y, v.z)
            }
            o
        }
    }

    fun rotate(component: GuiComponent): Option<GuiComponent, Vec3d> {
        pushPopMatrix(component)

        return getData(component).getData("rotate") {
            val o = Option<GuiComponent, Vec3d>(Vec3d.ZERO)
            component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) {
                val v = o.getValue(component)
                GlStateManager.rotate(v.x.toFloat(), 1f, 0f, 0f)
                GlStateManager.rotate(v.y.toFloat(), 0f, 1f, 0f)
                GlStateManager.rotate(v.z.toFloat(), 0f, 0f, 1f)
            }
            o
        }
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
*/
