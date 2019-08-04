package com.teamwizardry.librarianlib.sprite

import com.teamwizardry.librarianlib.core.LibrarianLog
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

@SideOnly(Side.CLIENT)
class Java2DSprite(width: Int, height: Int) : ISprite {
    private var deleted = false
    override var width = width
        private set
    override var height = height
        private set

    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private val texID = TextureUtil.glGenTextures()

    init {
        TextureUtil.allocateTexture(texID, width, height)
    }

    @JvmOverloads
    fun begin(clear: Boolean = true, antialiasing: Boolean = false): Graphics2D {
        val g2d = image.graphics as Graphics2D
        if(antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        }
        g2d.background = Color(0, 0, 0, 0)
        if(clear) g2d.clearRect(0, 0, width, height)
        g2d.color = Color.WHITE
        return g2d
    }

    fun end() {
        TextureUtil.uploadTextureImageSub(texID, image, 0, 0, false, false)
    }

    override fun bind() {
        if(deleted) throw IllegalStateException("Texture has been deleted")
        GlStateManager.bindTexture(texID)
    }

    fun delete() {
        deleted = true
        TextureUtil.deleteTexture(texID)
    }

    override fun minU(animFrames: Int) = 0f
    override fun minV(animFrames: Int) = 0f
    override fun maxU(animFrames: Int) = 1f
    override fun maxV(animFrames: Int) = 1f
    override val uSize: Float = 1f
    override val vSize: Float = 1f
    override val frameCount = 1

    fun finalize() {
        val id = texID
        Minecraft.getMinecraft().addScheduledTask {
            LibrarianLog.debug("Deleting Java2DSprite $id")
            TextureUtil.deleteTexture(id)
        }
    }
}
