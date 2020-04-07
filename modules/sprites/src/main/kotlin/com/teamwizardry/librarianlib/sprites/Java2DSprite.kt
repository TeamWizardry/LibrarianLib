package com.teamwizardry.librarianlib.sprites

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class Java2DSprite(width: Int, height: Int) : ISprite {
    private var deleted = false
    override var width = width
        private set
    override var height = height
        private set

    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private val native = NativeImage(NativeImage.PixelFormat.RGBA, width, height, true)
    private val texID = TextureUtil.generateTextureId()
    override val renderType: RenderType

    init {
        TextureUtil.prepareImage(texID, width, height)
        native.uploadTextureSub(0, 0, 0, false)

        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState("minecraft:missingno".toRl(), false, false))
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
//        if(deleted) throw IllegalStateException("Texture has been deleted")

        @Suppress("INACCESSIBLE_TYPE")
        renderType = RenderType.makeType("sprite_type",
            DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState.build(true)
        )
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
        //todo This does nothing
        native.uploadTextureSub(0, 0, 0, false)
    }

    fun delete() {
        deleted = true
        TextureUtil.releaseTextureId(texID)
    }

    override fun minU(animFrames: Int) = 0f
    override fun minV(animFrames: Int) = 0f
    override fun maxU(animFrames: Int) = 1f
    override fun maxV(animFrames: Int) = 1f
    override val uSize: Float = 1f
    override val vSize: Float = 1f
    override val frameCount = 1

    fun finalize() {
        if(deleted) return
        val id = texID
        Client.minecraft.deferTask {
            logger.debug("Deleting Java2DSprite $id")
            TextureUtil.releaseTextureId(id)
        }
    }
}
