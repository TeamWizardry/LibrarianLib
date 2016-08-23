package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton
import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.common.util.math.Matrix4
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

class Component3DView(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<Component3DView>(posX, posY, width, height) {

    init {
        BUS.hook(PreDrawEvent::class.java) { event -> preDraw(event.mousePos) }
        BUS.hook(PostDrawEvent::class.java) { postDraw() }

        BUS.hook(MouseWheelEvent::class.java) { event ->
            if (mouseOver) {
                if (event.direction == MouseWheelDirection.UP && zoom < 100) {
                    zoom *= 1.5
                }
                if (event.direction == MouseWheelDirection.DOWN && zoom > 1) {
                    zoom /= 1.5
                }
            }
        }

        BUS.hook(MouseUpEvent::class.java) { event ->
            calcDrag(event.mousePos, event.button)

            dragStart = Vec2d.ZERO
            dragButton = null
        }
    }

    internal var dragStart = Vec2d.ZERO
    var offset = Vec3d.ZERO
    var zoom: Double = 0.toDouble()
    var rotX: Double = 0.toDouble()
    var rotY: Double = 0.toDouble()
    var rotZ: Double = 0.toDouble()
    internal var dragButton: EnumMouseButton? = null

    override fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        super.mouseDown(mousePos, button)

        if (!mouseOver)
            return
        if (dragButton != null)
            return

        dragStart = mousePos
        dragButton = button
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
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
        GlStateManager.translate(this.offset.xCoord, this.offset.yCoord, this.offset.zCoord)

        this.offset = offset
        this.rotZ = rotZ
        this.rotY = rotY
        this.rotX = rotX

        run { // RenderHelper.enableStandardItemLighting but brighter because of different light and ambiant values.
            val LIGHT0_POS = Vec3d(0.0, 1.0, 0.1).normalize()//(new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
            val LIGHT1_POS = Vec3d(0.0, 1.0, -0.1).normalize()//(new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

            GlStateManager.enableLighting()
            GlStateManager.enableLight(0)
            //            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial()
            GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE)

            val light = 0.3f
            val ambiant = 0.7f
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer(LIGHT0_POS.xCoord.toFloat(), LIGHT0_POS.yCoord.toFloat(), LIGHT0_POS.zCoord.toFloat(), 0.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0f, 0.0f, 0.0f, 1.0f))

            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer(LIGHT1_POS.xCoord.toFloat(), LIGHT1_POS.yCoord.toFloat(), LIGHT1_POS.zCoord.toFloat(), 0.0f))
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
