package com.teamwizardry.librarianlib.testcore.content.impl

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix3f
import org.joml.Matrix4f
import kotlin.math.sqrt

internal class TestEntityRenderer(dispatcher: EntityRendererFactory.Context) : EntityRenderer<TestEntityImpl>(dispatcher) {
    override fun render(entity: TestEntityImpl, entityYaw: Float, partialTicks: Float, matrixStack: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int) {
        matrixStack.push()
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.yaw + 90.0f))
        matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(entity.pitch))
        matrixStack.scale(1 / 5f, 1 / 5f, 1 / 5f)

        fun draw(type: RenderLayer) {
            val builder = vertexConsumers.getBuffer(type)
            val stackEntry: MatrixStack.Entry = matrixStack.peek()

            var len = sqrt(1 * 1f + 3 * 3f)
            var normalX = 1 / len
            var normalYZ = 3 / len

            // mmm x y z u v nx ny nz light
            builder.vertex(stackEntry, 0, -1, 1, 0.5, 0, light, normalX, -normalYZ, 0)
            builder.vertex(stackEntry, 0, -1, -1, 0, 0.5, light, normalX, -normalYZ, 0)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, -normalYZ, 0)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, -normalYZ, 0)

            builder.vertex(stackEntry, 0, 1, -1, 0.5, 0, light, normalX, normalYZ, 0)
            builder.vertex(stackEntry, 0, 1, 1, 0, 0.5, light, normalX, normalYZ, 0)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, normalYZ, 0)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, normalYZ, 0)

            builder.vertex(stackEntry, 0, -1, -1, 0.5, 0, light, normalX, 0, normalYZ)
            builder.vertex(stackEntry, 0, 1, -1, 0, 0.5, light, normalX, 0, normalYZ)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, 0, normalYZ)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, 0, normalYZ)

            builder.vertex(stackEntry, 0, 1, 1, 0.5, 0, light, normalX, 0, -normalYZ)
            builder.vertex(stackEntry, 0, -1, 1, 0, 0.5, light, normalX, 0, -normalYZ)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, 0, -normalYZ)
            builder.vertex(stackEntry, 3, 0, 0, 0, 0, light, normalX, 0, -normalYZ)

            len = sqrt(1 * 1f + 1 * 1f)
            normalX = 1 / len
            normalYZ = 1 / len

            builder.vertex(stackEntry, 0, -1, -1, 0.5, 1, light, -normalX, -normalYZ, 0)
            builder.vertex(stackEntry, 0, -1, 1, 1, 0.5, light, -normalX, -normalYZ, 0)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, -normalYZ, 0)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, -normalYZ, 0)

            builder.vertex(stackEntry, 0, 1, 1, 0.5, 1, light, -normalX, normalYZ, 0)
            builder.vertex(stackEntry, 0, 1, -1, 1, 0.5, light, -normalX, normalYZ, 0)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, normalYZ, 0)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, normalYZ, 0)

            builder.vertex(stackEntry, 0, 1, -1, 0.5, 1, light, -normalX, 0, normalYZ)
            builder.vertex(stackEntry, 0, -1, -1, 1, 0.5, light, -normalX, 0, normalYZ)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, 0, normalYZ)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, 0, normalYZ)

            builder.vertex(stackEntry, 0, -1, 1, 0.5, 1, light, -normalX, 0, -normalYZ)
            builder.vertex(stackEntry, 0, 1, 1, 1, 0.5, light, -normalX, 0, -normalYZ)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, 0, -normalYZ)
            builder.vertex(stackEntry, -1, 0, 0, 1, 1, light, -normalX, 0, -normalYZ)
        }

        draw(RenderLayer.getEntityCutout(getTexture(entity)))
        if (entity.isGlowing) {
            draw(RenderLayer.getOutline(getTexture(entity)))
        }

        matrixStack.pop()
        super.render(entity, entityYaw, partialTicks, matrixStack, vertexConsumers, light)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun VertexConsumer.vertex(
        stackEntry: MatrixStack.Entry,
        x: Number, y: Number, z: Number,
        u: Number, v: Number,
        lightmap: Int,
        nx: Number, ny: Number, nz: Number
    ) {
        this
            .vertex(stackEntry, x.toFloat(), y.toFloat(), z.toFloat())
            .color(255, 255, 255, 255)
            .texture(u.toFloat(), v.toFloat())
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(lightmap)
            .normal(stackEntry, nx.toFloat(), ny.toFloat(), nz.toFloat())
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    override fun getTexture(entity: TestEntityImpl): Identifier {
        return Identifier.of("liblib_testcore:textures/entity/testentity.png")
    }
}