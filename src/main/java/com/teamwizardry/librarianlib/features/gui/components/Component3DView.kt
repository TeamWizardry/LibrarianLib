package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

class Component3DView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    init {
        BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) { event -> preDraw(this.mousePos) }
        BUS.hook(GuiLayerEvents.PostDrawEvent::class.java) { postDraw() }

        BUS.hook(GuiComponentEvents.MouseWheelEvent::class.java) { event ->
            if (mouseOver) {
                if (event.direction == GuiComponentEvents.MouseWheelDirection.UP && zoom < 100) {
                    zoom *= 1.5
                }
                if (event.direction == GuiComponentEvents.MouseWheelDirection.DOWN && zoom > 1) {
                    zoom /= 1.5
                }
            }
        }

        BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            calcDrag(event.mousePos, event.button)

            dragStart = Vec2d.ZERO
            dragButton = null
        }
    }

    internal var dragStart = Vec2d.ZERO
    val offset_rm: RMValue<Vec3d> = RMValue(Vec3d.ZERO)
    val zoom_rm: RMValueDouble = RMValueDouble(0.0)
    val rotX_rm: RMValueDouble = RMValueDouble(0.0)
    val rotY_rm: RMValueDouble = RMValueDouble(0.0)
    val rotZ_rm: RMValueDouble = RMValueDouble(0.0)
    var offset: Vec3d by offset_rm
    var zoom: Double by zoom_rm
    var rotX: Double by rotX_rm
    var rotY: Double by rotY_rm
    var rotZ: Double by rotZ_rm
    internal var dragButton: EnumMouseButton? = null

    @Hook
    fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        if (!mouseOver)
            return
        if (dragButton != null)
            return

        dragStart = e.mousePos
        dragButton = e.button
    }

    override fun draw(partialTicks: Float) {
        //NO-OP
    }

    fun calcDrag(mousePos: Vec2d, button: EnumMouseButton?) {
        if (button != dragButton)
            return

        if (dragButton == EnumMouseButton.LEFT) {
            rotY += mousePos.x - dragStart.x
            rotX += mousePos.y - dragStart.y
        }
        if (dragButton == EnumMouseButton.RIGHT) {
            var offset = Vec3d(mousePos.x - dragStart.x, mousePos.y - dragStart.y, 0.0)
            val matrix = Matrix4()
            matrix.rotate(-Math.toRadians(rotZ), Vec3d(0.0, 0.0, 1.0))
            matrix.rotate(-Math.toRadians(rotY), Vec3d(0.0, 1.0, 0.0))
            matrix.rotate(-Math.toRadians(rotX), Vec3d(1.0, 0.0, 0.0))
            matrix.scale(Vec3d(1.0 / zoom, -1.0 / zoom, 1.0 / zoom))
            offset = matrix.apply(offset)

            this.offset = this.offset.add(offset)
        }
    }

    fun preDraw(mousePos: Vec2d) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(this.size.x / 2, this.size.y / 2, 500.0)

        val rotX = this.rotX
        val rotY = this.rotY
        val rotZ = this.rotZ
        val offset = this.offset

        calcDrag(mousePos, dragButton)

        GlStateManager.enableRescaleNormal()
        GlStateManager.scale(zoom, -zoom, zoom)
        GlStateManager.rotate(this.rotX.toFloat(), 1f, 0f, 0f)
        GlStateManager.rotate(this.rotY.toFloat(), 0f, 1f, 0f)
        GlStateManager.rotate(this.rotZ.toFloat(), 0f, 0f, 1f)
        GlStateManager.translate(this.offset.x, this.offset.y, this.offset.z)

        this.offset = offset
        this.rotZ = rotZ
        this.rotY = rotY
        this.rotX = rotX

        run {
            // RenderHelper.enableStandardItemLighting but brighter because of different light and ambiant values.
            val LIGHT0_POS = Vec3d(0.0, 1.0, 0.1).normalize()//(new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
            val LIGHT1_POS = Vec3d(0.0, 1.0, -0.1).normalize()//(new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

            GlStateManager.enableLighting()
            GlStateManager.enableLight(0)
            //            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial()
            GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE)

            val light = 0.3f
            val ambiant = 0.7f
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer(LIGHT0_POS.x.toFloat(), LIGHT0_POS.y.toFloat(), LIGHT0_POS.z.toFloat(), 0.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))

            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer(LIGHT1_POS.x.toFloat(), LIGHT1_POS.y.toFloat(), LIGHT1_POS.z.toFloat(), 0.0f))
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))

            GlStateManager.shadeModel(GL11.GL_FLAT)
            GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambiant, ambiant, ambiant, 1.0f))
        }

    }

    fun postDraw() {
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
    }

}
