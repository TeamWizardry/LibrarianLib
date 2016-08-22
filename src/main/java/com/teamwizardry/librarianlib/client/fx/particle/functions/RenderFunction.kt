package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBase
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
abstract class RenderFunction() {

    abstract fun render(i: Float, particle: ParticleBase, color: Color,
                        worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, posX: Double, posY: Double, posZ: Double, skyLight: Int, blockLight: Int)
}