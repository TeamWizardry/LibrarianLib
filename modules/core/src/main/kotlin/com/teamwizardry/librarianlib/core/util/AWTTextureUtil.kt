package com.teamwizardry.librarianlib.core.util

import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.renderer.texture.NativeImage
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.lang.IllegalArgumentException

object AWTTextureUtil {
    @JvmStatic
    fun createNativeImage(image: BufferedImage): NativeImage {
        val native = NativeImage(image.width, image.height, false)
        fillNativeImage(image, native)
        return native
    }

    @JvmStatic
    fun fillNativeImage(image: BufferedImage, nativeImage: NativeImage) {
        val imagePointer: Long = imagePointer.get(nativeImage)

        if(nativeImage.format != NativeImage.PixelFormat.RGBA)
            throw IllegalArgumentException("The ${nativeImage.format} format isn't supported. Only the RGBA image format is supported.")
        for(y in 0 until image.height) {
            for(x in 0 until image.width) {
                val index = (x + y * image.width) * 4
                val rgb = image.getRGB(x, y)
                MemoryUtil.memPutInt(imagePointer + index, (rgb shl 8) or (rgb ushr 24))
            }
        }
    }

    private val imagePointer = Mirror.reflectClass<NativeImage>().getDeclaredField(mapSrgName("field_195722_d")) // imagePointer
}