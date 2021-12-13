package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.testcore.content.impl.TestEntityImpl
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

object AlbedoTestRenderManager : WorldRenderEvents.Last {
    val worldRenderers = mutableMapOf<Identifier, AlbedoTestRenderer>()

    fun registerEvents() {
        WorldRenderEvents.LAST.register(this)
    }

    override fun onLast(context: WorldRenderContext) {
        val matrices = MatrixStack()
        val viewPos = Client.minecraft.gameRenderer.camera.pos
        matrices.translate(-viewPos.x, -viewPos.y, -viewPos.z)
        matrices.peek().normal.load(context.matrixStack().peek().normal)

        val world = Client.minecraft.world ?: return
        world.entities.asSequence()
            .filterIsInstance<TestEntityImpl>()
            .forEach { entity ->
                matrices.push()
                matrices.translate(
                    Client.worldTime.interp(entity.prevX, entity.x),
                    Client.worldTime.interp(entity.prevY, entity.y),
                    Client.worldTime.interp(entity.prevZ, entity.z)
                )
                matrices.multiply(
                    Vec3f.NEGATIVE_Y.getDegreesQuaternion(
                        MathHelper.lerp(context.tickDelta(), entity.prevYaw, entity.yaw) + 90.0f
                    )
                )
                matrices.multiply(
                    Vec3f.NEGATIVE_Z.getDegreesQuaternion(
                        MathHelper.lerp(context.tickDelta(), entity.prevPitch, entity.pitch)
                    )
                )
                matrices.translate(3.0/5.0, 0.0, 0.0)

                val renderer = worldRenderers[entity.config.id]
                if(renderer != null && !renderer.crashed) {
                    try {
                        renderer.render(matrices)
                    } catch (e: Exception) {
                        logger.error("${entity.config.id}", e)
                        renderer.crashed = true
                    }
                }

                matrices.pop()
            }
    }

    val logger = LibLibAlbedoTest.logManager.makeLogger<AlbedoTestRenderManager>()
}