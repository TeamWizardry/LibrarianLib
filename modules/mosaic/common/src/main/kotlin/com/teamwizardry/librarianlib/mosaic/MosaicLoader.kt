package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import com.teamwizardry.librarianlib.core.util.kotlin.tick
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.math.Vec2i
import com.teamwizardry.librarianlib.math.ceilInt
import com.teamwizardry.librarianlib.math.floorInt
import com.teamwizardry.librarianlib.core.util.ivec
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.platform.LibLibPlatformCommon
import net.minecraft.client.resource.metadata.AnimationResourceMetadata
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

internal object MosaicLoader : ResourceReloader {
    private var definitions: MutableMap<Identifier, MosaicDefinition?> = mutableMapOf()
    private var missingno = Identifier.of("liblib_mosaic:textures/missingno.png")

    val missingnoSheet: MosaicDefinition get() = getDefinition(missingno)
    val missingnoSprite: SpriteDefinition get() = getDefinition(missingno).sprites[0]
    val missingnoColor: ColorDefinition get() = getDefinition(missingno).colors[0]

    init {
        LibLibPlatformCommon.instance.registerResourceReloadListener(
            ResourceType.CLIENT_RESOURCES,
            this,
            Identifier.of("liblib_mosaic:loader")
        )
    }

    fun getDefinition(location: Identifier): MosaicDefinition {
        val def = definitions.getOrPut(location) {
            load(Client.minecraft.resourceManager, location)
        }
        if(def == null && location == missingno)
            inconceivable("Could not find the missingno sprite sheet")
        return def ?: getDefinition(missingno)
    }

    override fun reload(
        synchronizer: ResourceReloader.Synchronizer,
        manager: ResourceManager,
        prepareProfiler: Profiler,
        applyProfiler: Profiler,
        prepareExecutor: Executor,
        applyExecutor: Executor
    ): CompletableFuture<Void> {
        return load(manager, prepareProfiler, prepareExecutor)
            .thenCompose(synchronizer::whenPrepared)
            .thenCompose { apply(it, manager, applyProfiler, applyExecutor) }
    }

    fun load(
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<Map<Identifier, MosaicDefinition?>> {
        return CompletableFuture.supplyAsync({
            profiler.tick { loadDefinitions(manager) }
        }, executor)
    }

    fun apply(
        data: Map<Identifier, MosaicDefinition?>,
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            profiler.tick { updateDefinitions(data) }
        }, executor)
    }

    internal fun updateDefinitions(result: Map<Identifier, MosaicDefinition?>) {
        definitions = result.toMutableMap()
        synchronized(Mosaic.textures) {
            Mosaic.textures.forEach {
                it.loadDefinition()
            }
        }
    }

    internal fun loadDefinitions(manager: ResourceManager): Map<Identifier, MosaicDefinition?> {
        val locations = mutableSetOf<Identifier>()
        synchronized(Mosaic.textures) {
            Mosaic.textures.forEach {
                locations.add(it.location)
            }
        }
        return locations.associateWith {
            load(manager, it)
        }
    }

    private fun load(manager: ResourceManager, location: Identifier): MosaicDefinition? {
        val resource = try {
             manager.getResourceOrThrow(location)
        } catch (exception: IOException) {
            logger.error("Error loading sprite sheet '$location'", exception)
            return null
        }

        val image = resource.inputStream.use { ImageIO.read(it) }
        val json = resource.getMetadata().decode(MosaicMetadataReader).getOrNull()
            ?: return loadRaw(location, resource, image)

        val sheet = MosaicDefinition(location)

        sheet.blur = json.blur
        sheet.mipmap = json.mipmap
        sheet.uvSize = ivec(json.width, json.height)
        sheet.image = image

        sheet.sprites = json.sprites.map { load(it, sheet) }.unmodifiableView()
        sheet.colors = json.colors.map { load(it, sheet) }.unmodifiableView()

        sheet.missingSprite = SpriteDefinition("missingno").also {
            it.sheet = sheet
            it.uv = ivec(0, 0)
            it.size = sheet.uvSize
            it.frameUVs = listOf(it.uv).unmodifiableView()
            it.image = sheet.image
            it.frameImages = listOf(sheet.image).unmodifiableView()
        }

        return sheet
    }

    private fun load(json: SpriteJson, sheet: MosaicDefinition): SpriteDefinition {
        val sprite = SpriteDefinition(json.name)
        sprite.sheet = sheet

        sprite.uv = ivec(json.u, json.v)
        sprite.size = ivec(json.w, json.h)

        sprite.minUCap = json.minUCap
        sprite.minVCap = json.minVCap
        sprite.maxUCap = json.maxUCap
        sprite.maxVCap = json.maxVCap

        sprite.minUPin = json.pinLeft
        sprite.minVPin = json.pinTop
        sprite.maxUPin = json.pinRight
        sprite.maxVPin = json.pinBottom

        val offset = ivec(json.offsetU, json.offsetV)
        sprite.frameUVs = json.frames.map { sprite.uv + offset * it }.unmodifiableView()

        val uvFactor = vec(sheet.image.width / sheet.uvSize.xd, sheet.image.height / sheet.uvSize.yd)
        sprite.image = sheet.image.getSubimage(
            floorInt(sprite.uv.x * uvFactor.x),
            floorInt(sprite.uv.y * uvFactor.y),
            ceilInt(sprite.size.x * uvFactor.x),
            ceilInt(sprite.size.y * uvFactor.y)
        )

        sprite.frameImages = sprite.frameUVs.map {
            sheet.image.getSubimage(
                floorInt(it.x * uvFactor.x),
                floorInt(it.y * uvFactor.y),
                ceilInt(sprite.size.x * uvFactor.x),
                ceilInt(sprite.size.y * uvFactor.y)
            )
        }.unmodifiableView()

        return sprite
    }

    private fun load(json: ColorJson, sheet: MosaicDefinition): ColorDefinition {
        val color = ColorDefinition(json.name)
        color.sheet = sheet

        color.uv = ivec(json.u, json.v)

        val uvFactor = vec(sheet.image.width / sheet.uvSize.xd, sheet.image.height / sheet.uvSize.yd)
        color.color = Color(sheet.image.getRGB(
            floorInt(color.uv.x * uvFactor.x),
            floorInt(color.uv.y * uvFactor.y)
        ))

        return color
    }

    private fun loadRaw(location: Identifier, resource: Resource, image: BufferedImage): MosaicDefinition {
        val sheet = MosaicDefinition(location)
        sheet.singleSprite = true

        val animation = resource.getMetadata().decode(AnimationResourceMetadata.READER).getOrNull()

        sheet.uvSize = ivec(image.width, image.height)
        sheet.image = image

        val sprite = run {
            val sprite = SpriteDefinition("")
            sprite.sheet = sheet

            sprite.uv = ivec(0, 0)

            if(animation == null) {
                sprite.size = sheet.uvSize
                sprite.frameUVs = listOf(sprite.uv).unmodifiableView()
            } else {
                if(animation.shouldInterpolate()) {
                    logger.warn("Ignoring interpolation for raw animation of $location")
                }
                sprite.size = animation.getSize(image.width, image.height).let { ivec(it.width, it.height) }
                val offset = ivec(0, sprite.size.y)
                val frames = mutableListOf<Vec2i>()
                animation.forEachFrame { index, frameTime ->
                    val uv = sprite.uv + offset * index
                    repeat(frameTime) { frames.add(uv) }
                }
                sprite.frameUVs = frames.unmodifiableView()
            }

            sprite.image = sheet.image.getSubimage(
                sprite.uv.x,
                sprite.uv.y,
                sprite.size.x,
                sprite.size.y
            )

            sprite.frameImages = sprite.frameUVs.map {
                sheet.image.getSubimage(
                    it.x,
                    it.y,
                    it.x + sprite.size.x,
                    it.y + sprite.size.y
                )
            }.unmodifiableView()

            sprite
        }

        sheet.sprites = listOf(sprite)

        return sheet
    }

    private val logger = LibLibMosaic.makeLogger<MosaicLoader>()
}