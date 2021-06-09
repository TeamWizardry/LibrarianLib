package com.teamwizardry.librarianlib.albedo.uniform

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL20

public class FloatUniform : Uniform(GL20.GL_FLOAT) {
    private var value: Float = 0f

    public fun get(): Float = value
    public fun set(value: Float) {
        this.value = value
    }

    override fun push() {
        GL20.glUniform1f(location, value)
    }

}

public class FloatArrayUniform(length: Int) : ArrayUniform(GL20.GL_FLOAT, length) {
    private val values: FloatArray = FloatArray(length)

    public operator fun get(index: Int): Float = values[index]
    public operator fun set(index: Int, value: Float) {
        values[index] = value
    }

    override fun push() {
        GL20.glUniform1fv(location, values)
    }
}

public class FloatVec2Uniform : Uniform(GL20.GL_FLOAT_VEC2) {
    public var x: Float = 0f
    public var y: Float = 0f

    public fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    public fun get(): Vec2d = vec(x, y)
    public fun set(value: Vec2d) {
        set(value.xf, value.yf)
    }

    override fun push() {
        GL20.glUniform2f(location, x, y)
    }
}

public class FloatVec2ArrayUniform(length: Int) : ArrayUniform(GL20.GL_FLOAT_VEC2, length) {
    private val values: FloatArray = FloatArray(length * 2)

    public fun getX(index: Int): Float = values[index * 2]
    public fun getY(index: Int): Float = values[index * 2 + 1]
    public fun setX(index: Int, x: Float) {
        values[index * 2] = x
    }

    public fun setY(index: Int, y: Float) {
        values[index * 2 + 1] = y
    }

    public fun set(index: Int, x: Float, y: Float) {
        setX(index, x)
        setY(index, y)
    }

    public operator fun get(index: Int): Vec2d = vec(values[index * 2], values[index * 2 + 1])
    public operator fun set(index: Int, value: Vec2d) {
        set(index, value.xf, value.yf)
    }

    override fun push() {
        GL20.glUniform2fv(location, values)
    }
}

public class FloatVec3Uniform : Uniform(GL20.GL_FLOAT_VEC3) {
    public var x: Float = 0f
    public var y: Float = 0f
    public var z: Float = 0f

    public fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    public fun get(): Vec3d = vec(x, y, z)
    public fun set(value: Vec3d) {
        set(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
    }

    override fun push() {
        GL20.glUniform3f(location, x, y, z)
    }
}

public class FloatVec3ArrayUniform(length: Int) : ArrayUniform(GL20.GL_FLOAT_VEC3, length) {
    private val values: FloatArray = FloatArray(length * 3)

    public fun getX(index: Int): Float = values[index * 3]
    public fun getY(index: Int): Float = values[index * 3 + 1]
    public fun getZ(index: Int): Float = values[index * 3 + 2]
    public fun setX(index: Int, x: Float) {
        values[index * 3] = x
    }

    public fun setY(index: Int, y: Float) {
        values[index * 3 + 1] = y
    }

    public fun setZ(index: Int, z: Float) {
        values[index * 3 + 2] = z
    }

    public fun set(index: Int, x: Float, y: Float, z: Float) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
    }

    public operator fun get(index: Int): Vec3d = vec(values[index * 3], values[index * 3 + 1], values[index * 3 + 2])
    public operator fun set(index: Int, value: Vec3d) {
        set(index, value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
    }

    override fun push() {
        GL20.glUniform3fv(location, values)
    }
}

public class FloatVec4Uniform : Uniform(GL20.GL_FLOAT_VEC4) {
    public var x: Float = 0f
    public var y: Float = 0f
    public var z: Float = 0f
    public var w: Float = 0f

    public fun set(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    override fun push() {
        GL20.glUniform4f(location, x, y, z, w)
    }
}

public class FloatVec4ArrayUniform(length: Int) : ArrayUniform(GL20.GL_FLOAT_VEC4, length) {
    private val values: FloatArray = FloatArray(length * 4)

    public fun getX(index: Int): Float = values[index * 4]
    public fun getY(index: Int): Float = values[index * 4 + 1]
    public fun getZ(index: Int): Float = values[index * 4 + 2]
    public fun getW(index: Int): Float = values[index * 4 + 3]
    public fun setX(index: Int, x: Float) {
        values[index * 4] = x
    }

    public fun setY(index: Int, y: Float) {
        values[index * 4 + 1] = y
    }

    public fun setZ(index: Int, z: Float) {
        values[index * 4 + 2] = z
    }

    public fun setW(index: Int, w: Float) {
        values[index * 4 + 3] = w
    }

    public fun set(index: Int, x: Float, y: Float, z: Float, w: Float) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
        setW(index, w)
    }

    override fun push() {
        GL20.glUniform4fv(location, values)
    }
}
