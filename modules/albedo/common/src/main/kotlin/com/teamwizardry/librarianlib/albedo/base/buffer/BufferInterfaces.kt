package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerUniform
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3f
import org.joml.Matrix4f
import java.awt.Color

public interface PositionBuffer<T> {
    public fun pos(x: Double, y: Double, z: Double): T

    public fun pos(matrix: Matrix4d, x: Double, y: Double, z: Double): T {
        return this.pos(
            matrix.transformX(x, y, z),
            matrix.transformY(x, y, z),
            matrix.transformZ(x, y, z)
        )
    }

    public fun pos(matrix: org.joml.Matrix4d, x: Double, y: Double, z: Double): T {
        return this.pos(
            matrix.m00() * x + matrix.m10() * y + matrix.m20() * z + matrix.m30() * 1,
            matrix.m01() * x + matrix.m11() * y + matrix.m21() * z + matrix.m31() * 1,
            matrix.m02() * x + matrix.m12() * y + matrix.m22() * z + matrix.m32() * 1
        )
    }

    public fun pos(matrix: Matrix4f, x: Float, y: Float, z: Float): T {
        val xd = x.toDouble()
        val yd = y.toDouble()
        val zd = z.toDouble()
        return this.pos(
            matrix.m00() * xd + matrix.m10() * yd + matrix.m20() * zd + matrix.m30() * 1,
            matrix.m01() * xd + matrix.m11() * yd + matrix.m21() * zd + matrix.m31() * 1,
            matrix.m02() * xd + matrix.m12() * yd + matrix.m22() * zd + matrix.m32() * 1
        )
    }

    public fun pos(stack: MatrixStack, x: Float, y: Float, z: Float): T {
        return this.pos(stack.peek().positionMatrix, x, y, z)
    }

    public fun pos(pos: Vec3d): T = this.pos(pos.x, pos.y, pos.z)

    public fun pos(matrix: Matrix4d, pos: Vec3d): T = this.pos(matrix, pos.x, pos.y, pos.z)

    public fun pos(matrix: Matrix4f, pos: Vec3d): T =
        this.pos(matrix, pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())

    public fun pos(stack: MatrixStack, pos: Vec3d): T =
        this.pos(stack, pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
}

public interface ColorBuffer<T> {
    public fun color(r: Int, g: Int, b: Int, a: Int): T

    public fun color(r: Float, g: Float, b: Float, a: Float): T {
        return this.color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), (a * 255).toInt());
    }

    public fun color(color: Color): T {
        return this.color(color.red, color.green, color.blue, color.alpha)
    }
}

public interface TexBuffer<T> {
    public val texture: SamplerUniform

    public fun tex(u: Float, v: Float): T

    public fun tex(u: Double, v: Double): T = this.tex(u.toFloat(), v.toFloat())
}

public interface NormalBuffer<T> {
    public fun normal(x: Double, y: Double, z: Double): T

    public fun normal(matrix: Matrix3d, x: Double, y: Double, z: Double): T {
        return this.normal(
            matrix.transformX(x, y, z),
            matrix.transformY(x, y, z),
            matrix.transformZ(x, y, z)
        )
    }

    public fun normal(matrix: org.joml.Matrix3d, x: Double, y: Double, z: Double): T {
        return this.normal(
            matrix.m00() * x + matrix.m10() * y + matrix.m20() * z,
            matrix.m01() * x + matrix.m11() * y + matrix.m21() * z,
            matrix.m02() * x + matrix.m12() * y + matrix.m22() * z
        )
    }

    public fun normal(matrix: Matrix3f, x: Float, y: Float, z: Float): T {
        val xd = x.toDouble()
        val yd = y.toDouble()
        val zd = z.toDouble()
        return this.normal(
            matrix.m00() * xd + matrix.m10() * yd + matrix.m20() * zd,
            matrix.m01() * xd + matrix.m11() * yd + matrix.m21() * zd,
            matrix.m02() * xd + matrix.m12() * yd + matrix.m22() * zd
        )
    }

    public fun normal(stack: MatrixStack, x: Float, y: Float, z: Float): T {
        return this.normal(stack.peek().normalMatrix, x, y, z)
    }


    public fun normal(normal: Vec3d): T {
        return this.normal(normal.x, normal.y, normal.z)
    }

    public fun normal(matrix: Matrix3d, normal: Vec3d): T {
        return this.normal(matrix, normal.x, normal.y, normal.z)
    }

    public fun normal(matrix: Matrix3f, normal: Vec3d): T {
        return this.normal(matrix, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
    }

    public fun normal(stack: MatrixStack, normal: Vec3d): T {
        return this.normal(stack, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
    }

}

public interface LightmapBuffer<T> {
    public fun light(lightmap: Int): T

    public fun light(sky: Int, block: Int): T = this.light((sky shl 20) or (block shl 4))
}

