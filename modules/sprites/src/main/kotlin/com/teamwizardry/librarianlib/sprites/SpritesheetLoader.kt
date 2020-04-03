package com.teamwizardry.librarianlib.sprites

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import com.teamwizardry.librarianlib.core.util.kotlin.tick
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.math.Vec2i
import com.teamwizardry.librarianlib.math.ceilInt
import com.teamwizardry.librarianlib.math.floorInt
import com.teamwizardry.librarianlib.math.ivec
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.resources.ReloadListener
import net.minecraft.client.resources.data.AnimationMetadataSection
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResource
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

internal object SpritesheetLoader : ReloadListener<Map<ResourceLocation, SpritesheetDefinition?>>() {
    private var definitions: MutableMap<ResourceLocation, SpritesheetDefinition?> = mutableMapOf()
    private var missingno = "librarianlib:sprites/textures/missingno.png".toRl()

    val missingnoSheet: SpritesheetDefinition get() = getDefinition(missingno)
    val missingnoSprite: SpriteDefinition get() = getDefinition(missingno).sprites[0]

    fun getDefinition(location: ResourceLocation): SpritesheetDefinition {
        val def = definitions.getOrPut(location) {
            load(Client.minecraft.resourceManager, location)
        }
        if(def == null && location == missingno)
            inconceivable("Could not find the missingno sprite sheet")
        return def ?: getDefinition(missingno)
    }

    override fun prepare(manager: IResourceManager, profiler: IProfiler): Map<ResourceLocation, SpritesheetDefinition?> {
        return profiler.tick { loadDefinitions(manager) }
    }

    override fun apply(result: Map<ResourceLocation, SpritesheetDefinition?>, manager: IResourceManager, profiler: IProfiler) {
        profiler.tick { updateDefinitions(result) }
    }

    internal fun updateDefinitions(result: Map<ResourceLocation, SpritesheetDefinition?>) {
        definitions = result.toMutableMap()
        synchronized(Texture.textures) {
            Texture.textures.removeIf {
                it.get()?.also {
                    it.loadDefinition()
                } == null
            }
        }
    }

    internal fun loadDefinitions(manager: IResourceManager): Map<ResourceLocation, SpritesheetDefinition?> {
        val locations = mutableSetOf<ResourceLocation>()
        synchronized(Texture.textures) {
            Texture.textures.removeIf {
                it.get()?.also { locations.add(it.location) } == null
            }
        }
        return locations.associateWith {
            load(manager, it)
        }
    }

    private fun load(manager: IResourceManager, location: ResourceLocation): SpritesheetDefinition? {
        val resource = try {
             manager.getResource(location)
        } catch (exception: IOException) {
            logger.error("Error loading sprite sheet '$location'", exception)
            return null
        }

        val (json, image) = resource.use {
            val image = ImageIO.read(resource.inputStream)
            return@use Pair(
                resource.getMetadata(SpritesheetJson.SERIALIZER) ?: throw IllegalStateException("Couldn't find sprite sheet metadata for $location"),
                image
            )
        }

        val sheet = SpritesheetDefinition(location)

        sheet.uvSize = ivec(json.width, json.height)
        sheet.image = image

        sheet.sprites = json.sprites.map { load(it, sheet, manager, location) }.unmodifiableView()
        sheet.colors = json.colors.map { load(it, sheet, manager, location) }.unmodifiableView()

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

    private fun load(json: SpriteJson, sheet: SpritesheetDefinition, manager: IResourceManager, location: ResourceLocation): SpriteDefinition {
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

    private fun load(json: ColorJson, sheet: SpritesheetDefinition, manager: IResourceManager, location: ResourceLocation): ColorDefinition {
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

    private fun loadRaw(manager: IResourceManager, location: ResourceLocation, resource: IResource, image: BufferedImage): SpritesheetDefinition {
        logger.warn("No spritesheet MCMETA found for $location. Loading it as a single sprite.")

        val sheet = SpritesheetDefinition(location)

        val animation = resource.getMetadata(AnimationMetadataSection.SERIALIZER)

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
                if(animation.isInterpolate) {
                    logger.warn("Ignoring interpolation for raw animation of $location")
                }
                sprite.size = ivec(sheet.uvSize.x, sheet.uvSize.x * animation.getFrameHeight(1) / animation.getFrameWidth(1))
                val offset = ivec(0, sprite.size.y)
                val frames = mutableListOf<Vec2i>()
                for(it in 0 until animation.frameCount) {
                    val index = animation.getFrameIndex(it)
                    val duration = animation.getFrameTimeSingle(it)
                    val uv = sprite.uv + offset * index

                    repeat(duration) { frames.add(uv) }
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

}