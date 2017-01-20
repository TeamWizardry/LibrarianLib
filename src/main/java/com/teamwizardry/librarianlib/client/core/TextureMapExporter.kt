package com.teamwizardry.librarianlib.client.core

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


/**
 * Created by TheCodeWarrior
 */

val TextureMap.mh_mipMapLevel by MethodHandleHelper.delegateForReadOnly<TextureMap, Int>(TextureMap::class.java, "mipmapLevels", "field_147636_j")
val TextureMap.mh_basePath by MethodHandleHelper.delegateForReadOnly<TextureMap, String>(TextureMap::class.java, "basePath", "field_94254_c")

object TextureMapExporter {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun postTextureStitch(e: TextureStitchEvent.Post) {
        saveGlTexture(e.map.mh_basePath, e.map.glTextureId, e.map.mh_mipMapLevel)
    }

    fun saveGlTexture(name: String, textureId: Int, mipmapLevels: Int) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

        for (level in 0..mipmapLevels) {
            val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH)
            val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT)
            val size = width * height

            val bufferedimage = BufferedImage(width, height, 2)
            val output = File(name + "_" + level + ".png")

            val buffer = BufferUtils.createIntBuffer(size)
            val data = IntArray(size)

            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer)
            buffer.get(data)
            bufferedimage.setRGB(0, 0, width, height, data, 0, width)

            try {
                ImageIO.write(bufferedimage, "png", output)
                LibrarianLog.info("[TextureDump] Exported png to: ${output.absolutePath}")
            } catch (ioexception: IOException) {
                LibrarianLog.info("[TextureDump] Unable to write: ", ioexception)
            }

        }
    }
}
