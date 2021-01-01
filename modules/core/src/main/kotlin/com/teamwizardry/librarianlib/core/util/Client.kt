package com.teamwizardry.librarianlib.core.util

import com.google.common.collect.Lists
import com.teamwizardry.librarianlib.core.logger
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.IFutureReloadListener
import net.minecraft.resources.IReloadableResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Timer
import net.minecraft.util.math.Vec3d
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.resource.IResourceType
import net.minecraftforge.resource.ISelectiveResourceReloadListener
import net.minecraftforge.resource.SelectiveReloadStateHandler
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture

public object Client {
    @JvmStatic
    public val minecraft: Minecraft
        get() = Minecraft.getInstance()

    @JvmStatic
    public val player: ClientPlayerEntity?
        get() = minecraft.player

    @JvmStatic
    public val window: MainWindow
        get() = minecraft.mainWindow

    @JvmStatic
    public val guiScaleFactor: Double
        get() = window.guiScaleFactor

    @JvmStatic
    public val resourceManager: IResourceManager
        get() = minecraft.resourceManager

    @JvmStatic
    public val textureManager: TextureManager
        get() = minecraft.textureManager

    @JvmStatic
    public val fontRenderer: FontRenderer
        get() = minecraft.fontRenderer

    @JvmStatic
    public val tessellator: Tessellator
        get() = Tessellator.getInstance()

    /**
     * Datagen is like a quasi-client, so some things don't work. For example, there's no [Minecraft] instance.
     */
    @JvmStatic
    public val isDataGen: Boolean =
        Thread.getAllStackTraces().any { trace ->
            trace.value.any { element ->
                element.className == "net.minecraft.data.Main"
            }
        }

    /**
     * The game time, as measured from the game launch
     */
    @JvmStatic
    public val time: Time = object: Time() {
        override val ticks: Int
            get() = globalTicks
        override val partialTicks: Float
            get() = timer.renderPartialTicks
    }

    /**
     * The world time, as measured from the game launch
     */
    @JvmStatic
    public val worldTime: Time = object: Time() {
        override val ticks: Int
            get() = worldTicks
        override val partialTicks: Float
            get() = if (minecraft.isGamePaused)
                renderPartialTicksPaused.get(minecraft) as Float
            else
                timer.renderPartialTicks
    }

    @JvmStatic
    public val resourceReloadHandler: ResourceReload = ResourceReload()

    @JvmStatic
    public fun displayGuiScreen(screen: Screen?) {
        minecraft.displayGuiScreen(screen)
    }

    /**
     * Queue a task to be executed on the client thread. The task is executed immediately if this is called from the
     * client thread.
     */
    @JvmStatic
    public fun runAsync(task: Runnable): CompletableFuture<Void> {
        return minecraft.runAsync(task)
    }

    @JvmStatic
    public fun getBlockAtlasSprite(sprite: ResourceLocation): TextureAtlasSprite {
        @Suppress("DEPRECATION")
        return getAtlasSprite(AtlasTexture.LOCATION_BLOCKS_TEXTURE, sprite)
    }

    @JvmStatic
    public fun getAtlasSprite(atlas: ResourceLocation, texture: ResourceLocation): TextureAtlasSprite {
        return minecraft.getAtlasSpriteGetter(atlas).apply(texture)
    }

    /**
     * Gets the [InputStream] for a given resource, or throws an IOException if it isn't found
     *
     * @see getResourceInputStreamOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceInputStream(resourceManager: IResourceManager, location: ResourceLocation): InputStream {
        return resourceManager.getResource(location).inputStream
    }

    /**
     * Gets the [InputStream] for a given resource, or returns null if it isn't found
     *
     * @see getResourceInputStream
     */
    @JvmStatic
    public fun getResourceInputStreamOrNull(resourceManager: IResourceManager, location: ResourceLocation): InputStream? {
        return try {
            getResourceInputStream(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the contents of the given resource as a byte array, or throws an IOException if the resource isn't found
     *
     * @see getResourceBytesOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceBytes(resourceManager: IResourceManager, location: ResourceLocation): ByteArray {
        return resourceManager.getResource(location).inputStream.use { it.readBytes() }
    }

    /**
     * Gets the contents of the given resource as a byte array, or returns null if the resource isn't found
     *
     * @see getResourceBytes
     */
    @JvmStatic
    public fun getResourceBytesOrNull(resourceManager: IResourceManager, location: ResourceLocation): ByteArray? {
        return try {
            getResourceBytes(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the contents of the given resource as a string, or throws an IOException if the resource isn't found
     *
     * @see getResourceTextOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceText(resourceManager: IResourceManager, location: ResourceLocation): String {
        return resourceManager.getResource(location).inputStream.bufferedReader().use { it.readText() }
    }

    /**
     * Gets the contents of the given resource as a string, or returns null if the resource isn't found
     *
     * @see getResourceText
     */
    @JvmStatic
    public fun getResourceTextOrNull(resourceManager: IResourceManager, location: ResourceLocation): String? {
        return try {
            getResourceText(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the [InputStream] for a given resource, or throws an IOException if it isn't found
     *
     * @see getResourceInputStreamOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceInputStream(location: ResourceLocation): InputStream {
        return getResourceInputStream(resourceManager, location)
    }

    /**
     * Gets the [InputStream] for a given resource, or returns null if it isn't found
     *
     * @see getResourceInputStream
     */
    @JvmStatic
    public fun getResourceInputStreamOrNull(location: ResourceLocation): InputStream? {
        return getResourceInputStreamOrNull(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a byte array, or throws an IOException if the resource isn't found
     *
     * @see getResourceBytesOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceBytes(location: ResourceLocation): ByteArray {
        return getResourceBytes(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a byte array, or returns null if the resource isn't found
     *
     * @see getResourceBytes
     */
    @JvmStatic
    public fun getResourceBytesOrNull(location: ResourceLocation): ByteArray? {
        return getResourceBytesOrNull(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a string, or throws an IOException if the resource isn't found
     *
     * @see getResourceTextOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceText(location: ResourceLocation): String {
        return getResourceText(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a string, or returns null if the resource isn't found
     *
     * @see getResourceText
     */
    @JvmStatic
    public fun getResourceTextOrNull(location: ResourceLocation): String? {
        return getResourceTextOrNull(resourceManager, location)
    }

    public abstract class Time {
        public abstract val ticks: Int
        public abstract val partialTicks: Float
        public val time: Float get() = ticks + partialTicks
        public val seconds: Float get() = time / 20

        public fun interp(previous: Double, current: Double): Double {
            return previous + (current - previous) * partialTicks
        }

        @Suppress("NOTHING_TO_INLINE")
        public inline fun interp(previous: Number, current: Number): Double = interp(previous.toDouble(), current.toDouble())

        public fun interp(previous: Vec2d, current: Vec2d): Vec2d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y))
        }

        public fun interp(previous: Vec3d, current: Vec3d): Vec3d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y), interp(previous.z, current.z))
        }
    }

    private val timer: Timer = if(isDataGen)
        Timer(50f, 0)
    else
        Mirror.reflectClass<Minecraft>().getField(mapSrgName("field_71428_T")).get(minecraft)

    private val renderPartialTicksPaused = Mirror.reflectClass<Minecraft>().getField(mapSrgName("field_193996_ah"))

    private var worldTicks: Int = 0
    private var globalTicks: Int = 0

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @JvmSynthetic
    @SubscribeEvent
    internal fun clientTickEnd(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val mc = Minecraft.getInstance()
            if (!mc.isGamePaused)
                worldTicks += timer.elapsedTicks
            globalTicks += timer.elapsedTicks
        }
    }
}
