package com.teamwizardry.librarianlib.features.particle.functions

import com.teamwizardry.librarianlib.features.particle.ParticleBase
import com.teamwizardry.librarianlib.features.particle.ParticleRenderLayer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
abstract class RenderFunction(protected var theLayer: ParticleRenderLayer) {

    open fun getLayer(): ParticleRenderLayer {
        return theLayer
    }

    abstract fun render(i: Float, particle: ParticleBase, color: Color, alpha: Float,
                        worldRendererIn: BufferBuilder, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, rotation: Float, pos: Vec3d, skyLight: Int, blockLight: Int)
}
