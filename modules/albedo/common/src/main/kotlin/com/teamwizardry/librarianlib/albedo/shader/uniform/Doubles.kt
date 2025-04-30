package com.teamwizardry.librarianlib.albedo.shader.uniform

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40

public class DoubleUniform(name: String) : Uniform(name, GL20.GL_DOUBLE) {
    private var value: Double = 0.0

    public fun get(): Double = value
    public fun set(value: Double) {
        this.value = value
    }

    override fun push() {
        GL40.glUniform1d(location, value)
    }

}

public class DoubleArrayUniform(name: String, length: Int) : ArrayUniform(name, GL20.GL_DOUBLE, length) {
    private val values: DoubleArray = DoubleArray(length)

    public operator fun get(index: Int): Double = values[index]
    public operator fun set(index: Int, value: Double) {
        values[index] = value
    }

    override fun push() {
        GL40.glUniform1dv(location, values)
    }
}

public class DoubleVec2Uniform(name: String) : Uniform(name, GL40.GL_DOUBLE_VEC2) {
    public var x: Double = 0.0
    public var y: Double = 0.0

    public fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    public fun get(): Vec2d = vec(x, y)
    public fun set(value: Vec2d) {
        set(value.x, value.y)
    }

    override fun push() {
        GL40.glUniform2d(location, x, y)
    }
}

public class DoubleVec2ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL40.GL_DOUBLE_VEC2, length) {
    private val values: DoubleArray = DoubleArray(length * 2)

    public fun getX(index: Int): Double = values[index * 2]
    public fun getY(index: Int): Double = values[index * 2 + 1]
    public fun setX(index: Int, x: Double) {
        values[index * 2] = x
    }

    public fun setY(index: Int, y: Double) {
        values[index * 2 + 1] = y
    }

    public fun set(index: Int, x: Double, y: Double) {
        setX(index, x)
        setY(index, y)
    }

    public operator fun get(index: Int): Vec2d = vec(values[index * 2], values[index * 2 + 1])
    public operator fun set(index: Int, value: Vec2d) {
        set(index, value.x, value.y)
    }

    override fun push() {
        GL40.glUniform2dv(location, values)
    }
}

public class DoubleVec3Uniform(name: String) : Uniform(name, GL40.GL_DOUBLE_VEC3) {
    public var x: Double = 0.0
    public var y: Double = 0.0
    public var z: Double = 0.0

    public fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    public fun get(): Vec3d = vec(x, y, z)
    public fun set(value: Vec3d) {
        set(value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
    }

    override fun push() {
        GL40.glUniform3d(location, x, y, z)
    }
}

public class DoubleVec3ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL40.GL_DOUBLE_VEC3, length) {
    private val values: DoubleArray = DoubleArray(length * 3)

    public fun getX(index: Int): Double = values[index * 3]
    public fun getY(index: Int): Double = values[index * 3 + 1]
    public fun getZ(index: Int): Double = values[index * 3 + 2]
    public fun setX(index: Int, x: Double) {
        values[index * 3] = x
    }

    public fun setY(index: Int, y: Double) {
        values[index * 3 + 1] = y
    }

    public fun setZ(index: Int, z: Double) {
        values[index * 3 + 2] = z
    }

    public fun set(index: Int, x: Double, y: Double, z: Double) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
    }

    public operator fun get(index: Int): Vec3d = vec(values[index * 3], values[index * 3 + 1], values[index * 3 + 2])
    public operator fun set(index: Int, value: Vec3d) {
        set(index, value.x.toDouble(), value.y.toDouble(), value.z.toDouble())
    }

    override fun push() {
        GL40.glUniform3dv(location, values)
    }
}

public class DoubleVec4Uniform(name: String) : Uniform(name, GL40.GL_DOUBLE_VEC4) {
    public var x: Double = 0.0
    public var y: Double = 0.0
    public var z: Double = 0.0
    public var w: Double = 0.0

    public fun set(x: Double, y: Double, z: Double, w: Double) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    override fun push() {
        GL40.glUniform4d(location, x, y, z, w)
    }
}

public class DoubleVec4ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL40.GL_DOUBLE_VEC4, length) {
    private val values: DoubleArray = DoubleArray(length * 4)

    public fun getX(index: Int): Double = values[index * 4]
    public fun getY(index: Int): Double = values[index * 4 + 1]
    public fun getZ(index: Int): Double = values[index * 4 + 2]
    public fun getW(index: Int): Double = values[index * 4 + 3]
    public fun setX(index: Int, x: Double) {
        values[index * 4] = x
    }

    public fun setY(index: Int, y: Double) {
        values[index * 4 + 1] = y
    }

    public fun setZ(index: Int, z: Double) {
        values[index * 4 + 2] = z
    }

    public fun setW(index: Int, w: Double) {
        values[index * 4 + 3] = w
    }

    public fun set(index: Int, x: Double, y: Double, z: Double, w: Double) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
        setW(index, w)
    }

    override fun push() {
        GL40.glUniform4dv(location, values)
    }
}
