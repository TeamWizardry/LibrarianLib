package com.teamwizardry.librarianlib.testcore.content.impl

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import kotlin.math.sqrt

public class TestEntityRenderer(dispatcher: EntityRenderDispatcher) : EntityRenderer<TestEntityImpl>(dispatcher) {
    override fun render(entity: TestEntityImpl, entityYaw: Float, partialTicks: Float, matrixStack: MatrixStack, buffer: VertexConsumerProvider, packedLight: Int) {
        matrixStack.push()
        matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(MathHelper.lerp(partialTicks, entity.prevYaw, entity.yaw) + 90.0f))
        matrixStack.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(MathHelper.lerp(partialTicks, entity.prevPitch, entity.pitch)))
        matrixStack.scale(1 / 5f, 1 / 5f, 1 / 5f)

        fun draw(type: RenderLayer) {
            val builder = buffer.getBuffer(type)
            val stackEntry: MatrixStack.Entry = matrixStack.peek()
            val transform = stackEntry.model
            val normalTransform = stackEntry.normal

            var len = sqrt(1 * 1f + 3 * 3f)
            var normalX = 1 / len
            var normalYZ = 3 / len

            // mmm x y z u v nx ny nz light
            builder.vertex(transform, 0, -1, 1, 0.5, 0, packedLight, normalTransform, normalX, -normalYZ, 0)
            builder.vertex(transform, 0, -1, -1, 0, 0.5, packedLight, normalTransform, normalX, -normalYZ, 0)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, -normalYZ, 0)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, -normalYZ, 0)

            builder.vertex(transform, 0, 1, -1, 0.5, 0, packedLight, normalTransform, normalX, normalYZ, 0)
            builder.vertex(transform, 0, 1, 1, 0, 0.5, packedLight, normalTransform, normalX, normalYZ, 0)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, normalYZ, 0)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, normalYZ, 0)

            builder.vertex(transform, 0, -1, -1, 0.5, 0, packedLight, normalTransform, normalX, 0, normalYZ)
            builder.vertex(transform, 0, 1, -1, 0, 0.5, packedLight, normalTransform, normalX, 0, normalYZ)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, 0, normalYZ)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, 0, normalYZ)

            builder.vertex(transform, 0, 1, 1, 0.5, 0, packedLight, normalTransform, normalX, 0, -normalYZ)
            builder.vertex(transform, 0, -1, 1, 0, 0.5, packedLight, normalTransform, normalX, 0, -normalYZ)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, 0, -normalYZ)
            builder.vertex(transform, 3, 0, 0, 0, 0, packedLight, normalTransform, normalX, 0, -normalYZ)

            len = sqrt(1 * 1f + 1 * 1f)
            normalX = 1 / len
            normalYZ = 1 / len

            builder.vertex(transform, 0, -1, -1, 0.5, 1, packedLight, normalTransform, -normalX, -normalYZ, 0)
            builder.vertex(transform, 0, -1, 1, 1, 0.5, packedLight, normalTransform, -normalX, -normalYZ, 0)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, -normalYZ, 0)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, -normalYZ, 0)

            builder.vertex(transform, 0, 1, 1, 0.5, 1, packedLight, normalTransform, -normalX, normalYZ, 0)
            builder.vertex(transform, 0, 1, -1, 1, 0.5, packedLight, normalTransform, -normalX, normalYZ, 0)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, normalYZ, 0)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, normalYZ, 0)

            builder.vertex(transform, 0, 1, -1, 0.5, 1, packedLight, normalTransform, -normalX, 0, normalYZ)
            builder.vertex(transform, 0, -1, -1, 1, 0.5, packedLight, normalTransform, -normalX, 0, normalYZ)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, 0, normalYZ)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, 0, normalYZ)

            builder.vertex(transform, 0, -1, 1, 0.5, 1, packedLight, normalTransform, -normalX, 0, -normalYZ)
            builder.vertex(transform, 0, 1, 1, 1, 0.5, packedLight, normalTransform, -normalX, 0, -normalYZ)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, 0, -normalYZ)
            builder.vertex(transform, -1, 0, 0, 1, 1, packedLight, normalTransform, -normalX, 0, -normalYZ)
        }

        draw(RenderLayer.getEntityCutout(getTexture(entity)))
        if (entity.isGlowing) {
            draw(RenderLayer.getOutline(getTexture(entity)))
        }

        matrixStack.pop()
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight)
    }

    @Suppress("NOTHING_TO_INLINE")
    public inline fun VertexConsumer.vertex(
        transform: Matrix4f, x: Number, y: Number, z: Number,
        u: Number, v: Number,
        lightmap: Int,
        normalTransform: Matrix3f,
        nx: Number, ny: Number, nz: Number
    ) {
        this
            .vertex(transform, x.toFloat(), y.toFloat(), z.toFloat())
            .color(255, 255, 255, 255)
            .texture(u.toFloat(), v.toFloat())
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(lightmap)
            .normal(normalTransform, nx.toFloat(), ny.toFloat(), nz.toFloat())
            .next()
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    override fun getTexture(entity: TestEntityImpl): Identifier {
        return Identifier("liblib-testcore:textures/entity/testentity.png")
    }
}