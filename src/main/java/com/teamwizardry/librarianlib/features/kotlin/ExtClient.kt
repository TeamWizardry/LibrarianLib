@file:SideOnly(Side.CLIENT)
@file:JvmName("ClientUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

// Easy client access ==================================================================================================

object Client {
    val minecraft: Minecraft get() = Minecraft.getMinecraft()
    val resolution: ScaledResolution get() = ScaledResolution(minecraft)
    val guiScaleFactor: Int get() = resolution.scaleFactor
    val resourceManager: IResourceManager get() = minecraft.resourceManager
    val renderEngine: TextureManager get() = minecraft.renderEngine
}

// Color ===============================================================================================================

fun Color.glColor() = GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
fun Color.copy(red: Int = this.red, blue: Int = this.blue, green: Int = this.green, alpha: Int = this.alpha) = Color(red, green, blue, alpha)
fun Color.copy(red: Float = this.red/255f, blue: Float = this.blue/255f, green: Float = this.green/255f, alpha: Float = this.alpha/255f) = Color(red, green, blue, alpha)

// VertexBuffer ========================================================================================================

fun BufferBuilder.pos(pos: Vec3d): BufferBuilder = this.pos(pos.x, pos.y, pos.z)

fun BufferBuilder.color(color: Color): BufferBuilder = this.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)

fun BufferBuilder.createCacheArrayAndReset(): IntArray {
    this.finishDrawing()

    val intBuf = this.byteBuffer.asIntBuffer()
    val bufferInts = IntArray(intBuf.limit())
    for (i in bufferInts.indices) {
        bufferInts[i] = intBuf.get(i)
    }

    this.reset()

    return bufferInts
}

fun BufferBuilder.addCacheArray(array: IntArray) {
    this.addVertexData(array)
}

val GuiComponent.width
    get() = size.xi
val GuiComponent.height
    get() = size.yi
val GuiComponent.x
    get() = pos.xi
val GuiComponent.y
    get() = pos.yi


val RenderManager.renderPosX by MethodHandleHelper.delegateForReadOnly<RenderManager, Double>(RenderManager::class.java, "field_78725_b", "renderPosX")
val RenderManager.renderPosY by MethodHandleHelper.delegateForReadOnly<RenderManager, Double>(RenderManager::class.java, "field_78726_c", "renderPosY")
val RenderManager.renderPosZ by MethodHandleHelper.delegateForReadOnly<RenderManager, Double>(RenderManager::class.java, "field_78723_d", "renderPosZ")
