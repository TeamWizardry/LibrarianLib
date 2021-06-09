package com.teamwizardry.librarianlib.albedo.uniform

import org.lwjgl.opengl.GL20

public class IntUniform : Uniform(GL20.GL_INT) {
    private var value: Int = 0

    public fun get(): Int = value
    public fun set(value: Int) {
        this.value = value
    }

    override fun push() {
        GL20.glUniform1i(location, value)
    }
}

public class IntArrayUniform(length: Int) : ArrayUniform(GL20.GL_INT, length) {
    private val values: IntArray = IntArray(length)

    public operator fun get(index: Int): Int = values[index]
    public operator fun set(index: Int, value: Int) {
        values[index] = value
    }

    override fun push() {
        GL20.glUniform1iv(location, values)
    }
}

public class IntVec2Uniform : Uniform(GL20.GL_INT_VEC2) {
    public var x: Int = 0
    public var y: Int = 0

    public fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    override fun push() {
        GL20.glUniform2i(location, x, y)
    }
}

public class IntVec2ArrayUniform(length: Int) : ArrayUniform(GL20.GL_INT_VEC2, length) {
    private val values: IntArray = IntArray(length * 2)

    public fun getX(index: Int): Int = values[index * 2]
    public fun getY(index: Int): Int = values[index * 2 + 1]
    public fun setX(index: Int, x: Int) {
        values[index * 2] = x
    }

    public fun setY(index: Int, y: Int) {
        values[index * 2 + 1] = y
    }

    public fun set(index: Int, x: Int, y: Int) {
        setX(index, x)
        setY(index, y)
    }

    override fun push() {
        GL20.glUniform2iv(location, values)
    }
}

public class IntVec3Uniform : Uniform(GL20.GL_INT_VEC3) {
    public var x: Int = 0
    public var y: Int = 0
    public var z: Int = 0

    public fun set(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    override fun push() {
        GL20.glUniform3i(location, x, y, z)
    }
}

public class IntVec3ArrayUniform(length: Int) : ArrayUniform(GL20.GL_INT_VEC3, length) {
    private val values: IntArray = IntArray(length * 3)

    public fun getX(index: Int): Int = values[index * 3]
    public fun getY(index: Int): Int = values[index * 3 + 1]
    public fun getZ(index: Int): Int = values[index * 3 + 2]
    public fun setX(index: Int, x: Int) {
        values[index * 3] = x
    }

    public fun setY(index: Int, y: Int) {
        values[index * 3 + 1] = y
    }

    public fun setZ(index: Int, z: Int) {
        values[index * 3 + 2] = z
    }

    public fun set(index: Int, x: Int, y: Int, z: Int) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
    }

    override fun push() {
        GL20.glUniform3iv(location, values)
    }
}

public class IntVec4Uniform : Uniform(GL20.GL_INT_VEC4) {
    public var x: Int = 0
    public var y: Int = 0
    public var z: Int = 0
    public var w: Int = 0

    public fun set(x: Int, y: Int, z: Int, w: Int) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    override fun push() {
        GL20.glUniform4i(location, x, y, z, w)
    }
}

public class IntVec4ArrayUniform(length: Int) : ArrayUniform(GL20.GL_INT_VEC4, length) {
    private val values: IntArray = IntArray(length * 4)

    public fun getX(index: Int): Int = values[index * 4]
    public fun getY(index: Int): Int = values[index * 4 + 1]
    public fun getZ(index: Int): Int = values[index * 4 + 2]
    public fun getW(index: Int): Int = values[index * 4 + 3]
    public fun setX(index: Int, x: Int) {
        values[index * 4] = x
    }

    public fun setY(index: Int, y: Int) {
        values[index * 4 + 1] = y
    }

    public fun setZ(index: Int, z: Int) {
        values[index * 4 + 2] = z
    }

    public fun setW(index: Int, w: Int) {
        values[index * 4 + 3] = w
    }

    public fun set(index: Int, x: Int, y: Int, z: Int, w: Int) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
        setW(index, w)
    }

    override fun push() {
        GL20.glUniform4iv(location, values)
    }
}
