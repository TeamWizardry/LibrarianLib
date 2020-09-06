package com.teamwizardry.librarianlib.testbase.objects

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.Matrix3f
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.Vector3f
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.math.sqrt

@OnlyIn(Dist.CLIENT)
public class TestEntityRenderer(renderManagerIn: EntityRendererManager): EntityRenderer<TestEntity>(renderManagerIn) {
    private val tex = loc("librarianlib-testbase:entity/testentity.png")

    override fun render(entity: TestEntity, entityYaw: Float, partialTicks: Float, matrixStack: MatrixStack, buffer: IRenderTypeBuffer, packedLight: Int) {
        matrixStack.push()
        matrixStack.rotate(Vector3f.YN.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw) + 90.0f))
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch)))
        matrixStack.scale(1 / 5f, 1 / 5f, 1 / 5f)

        fun draw(type: RenderType) {
            val builder = buffer.getBuffer(type)
            val stackEntry: MatrixStack.Entry = matrixStack.last
            val transform = stackEntry.matrix
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

        draw(RenderType.getEntityCutout(getEntityTexture(entity)))
        if (entity.isGlowing) {
            draw(RenderType.getOutline(getEntityTexture(entity)))
        }

        matrixStack.pop()
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight)
    }

    @Suppress("NOTHING_TO_INLINE")
    public inline fun IVertexBuilder.vertex(
        transform: Matrix4f, x: Number, y: Number, z: Number,
        u: Number, v: Number,
        lightmap: Int,
        normalTransform: Matrix3f,
        nx: Number, ny: Number, nz: Number
    ) {
        this
            .pos(transform, x.toFloat(), y.toFloat(), z.toFloat())
            .color(255, 255, 255, 255)
            .tex(u.toFloat(), v.toFloat())
            .overlay(OverlayTexture.NO_OVERLAY)
            .lightmap(lightmap)
            .normal(normalTransform, nx.toFloat(), ny.toFloat(), nz.toFloat())
            .endVertex()
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    override fun getEntityTexture(entity: TestEntity): ResourceLocation {
//        return ResourceLocation("textures/entity/projectiles/arrow.png")
        return loc("librarianlib-testbase:textures/entity/testentity.png")
    }
}
