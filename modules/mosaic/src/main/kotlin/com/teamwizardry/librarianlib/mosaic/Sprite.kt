package com.teamwizardry.librarianlib.mosaic

import net.minecraft.client.renderer.RenderType
import java.awt.image.BufferedImage

/**
 * This class represents a section of a [Mosaic]
 */
public class Sprite internal constructor(private val mosaic: Mosaic, public val name: String) : ISprite {

    private lateinit var definition: SpriteDefinition
    override val renderType: RenderType
        get() = mosaic.renderType

    override var width: Int = 0
        private set
    override var height: Int = 0
        private set

    override var uSize: Float = 0f
        private set
    override var vSize: Float = 0f
        private set

    override var pinLeft: Boolean = true
        private set
    override var pinTop: Boolean = true
        private set
    override var pinRight: Boolean = true
        private set
    override var pinBottom: Boolean = true
        private set

    override var minUCap: Float = 0f
        private set
    override var minVCap: Float = 0f
        private set
    override var maxUCap: Float = 0f
        private set
    override var maxVCap: Float = 0f
        private set

    override var frameCount: Int = 1
        private set

    public var images: List<BufferedImage> = emptyList()
        private set

    init {
        loadDefinition()
    }

    internal fun loadDefinition() {
        definition = mosaic.getSpriteDefinition(name)

        pinLeft = definition.minUPin
        pinTop = definition.minVPin
        pinRight = definition.maxUPin
        pinBottom = definition.maxVPin

        minUCap = definition.minUCap / definition.size.xf
        minVCap = definition.minVCap / definition.size.yf
        maxUCap = definition.maxUCap / definition.size.xf
        maxVCap = definition.maxVCap / definition.size.yf

        frameCount = definition.frameUVs.size

        width = mosaic.logicalU(definition.size.x)

        height = mosaic.logicalV(definition.size.y)

        uSize = definition.texU(definition.size.x)
        vSize = definition.texV(definition.size.y)

        images = definition.frameImages
    }

    /**
     * The minimum U coordinate (0-1)
     */
    override fun minU(animFrames: Int): Float {
        return definition.texU(definition.frameUVs[animFrames % frameCount].x)
    }

    /**
     * The minimum V coordinate (0-1)
     */
    override fun minV(animFrames: Int): Float {
        return definition.texV(definition.frameUVs[animFrames % frameCount].y)
    }

    /**
     * The maximum U coordinate (0-1)
     */
    override fun maxU(animFrames: Int): Float {
        return definition.texU(definition.frameUVs[animFrames % frameCount].x + definition.size.x)
    }

    /**
     * The maximum V coordinate (0-1)
     */
    override fun maxV(animFrames: Int): Float {
        return definition.texV(definition.frameUVs[animFrames % frameCount].y + definition.size.y)
    }

    override fun toString(): String {
        return "Sprite(texture=${mosaic.location}, name=$name)"
    }
}
