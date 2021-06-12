package com.teamwizardry.librarianlib.albedo.uniform

import org.lwjgl.opengl.GL20

/**
 * Converts a bool to an int
 */
private fun bool(v: Boolean): Int = if (v) 1 else 0

/**
 * Converts an int to a bool
 */
private fun bool(v: Int): Boolean = v != 0

public class BoolUniform(name: String) : Uniform(name, GL20.GL_BOOL) {
    private var value: Boolean = false

    public fun get(): Boolean = value
    public fun set(value: Boolean) {
        this.value = value
    }

    override fun push() {
        GL20.glUniform1i(location, bool(value))
    }
}

public class BoolArrayUniform(name: String, length: Int) : ArrayUniform(name, GL20.GL_BOOL, length) {
    private val values: IntArray = IntArray(length)

    public operator fun get(index: Int): Boolean = bool(values[index])
    public operator fun set(index: Int, value: Boolean) {
        values[index] = bool(value)
    }

    override fun push() {
        GL20.glUniform1iv(location, values)
    }
}

public class BoolVec2Uniform(name: String) : Uniform(name, GL20.GL_BOOL_VEC2) {
    public var x: Boolean = false
    public var y: Boolean = false

    public fun set(x: Boolean, y: Boolean) {
        this.x = x
        this.y = y
    }

    override fun push() {
        GL20.glUniform2i(location, bool(x), bool(y))
    }
}

public class BoolVec2ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL20.GL_BOOL_VEC2, length) {
    private val values: IntArray = IntArray(length * 2)

    public fun getX(index: Int): Boolean = bool(values[index * 2])
    public fun getY(index: Int): Boolean = bool(values[index * 2 + 1])
    public fun setX(index: Int, x: Boolean) {
        values[index * 2] = bool(x)
    }

    public fun setY(index: Int, y: Boolean) {
        values[index * 2 + 1] = bool(y)
    }

    public fun set(index: Int, x: Boolean, y: Boolean) {
        setX(index, x)
        setY(index, y)
    }

    override fun push() {
        GL20.glUniform2iv(location, values)
    }
}

public class BoolVec3Uniform(name: String) : Uniform(name, GL20.GL_BOOL_VEC3) {
    public var x: Boolean = false
    public var y: Boolean = false
    public var z: Boolean = false

    public fun set(x: Boolean, y: Boolean, z: Boolean) {
        this.x = x
        this.y = y
        this.z = z
    }

    override fun push() {
        GL20.glUniform3i(location, bool(x), bool(y), bool(z))
    }
}

public class BoolVec3ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL20.GL_BOOL_VEC3, length) {
    private val values: IntArray = IntArray(length * 3)

    public fun getX(index: Int): Boolean = bool(values[index * 3])
    public fun getY(index: Int): Boolean = bool(values[index * 3 + 1])
    public fun getZ(index: Int): Boolean = bool(values[index * 3 + 2])
    public fun setX(index: Int, x: Boolean) {
        values[index * 3] = bool(x)
    }

    public fun setY(index: Int, y: Boolean) {
        values[index * 3 + 1] = bool(y)
    }

    public fun setZ(index: Int, z: Boolean) {
        values[index * 3 + 2] = bool(z)
    }

    public fun set(index: Int, x: Boolean, y: Boolean, z: Boolean) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
    }

    override fun push() {
        GL20.glUniform3iv(location, values)
    }
}

public class BoolVec4Uniform(name: String) : Uniform(name, GL20.GL_BOOL_VEC4) {
    public var x: Boolean = false
    public var y: Boolean = false
    public var z: Boolean = false
    public var w: Boolean = false

    public fun set(x: Boolean, y: Boolean, z: Boolean, w: Boolean) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    override fun push() {
        GL20.glUniform4i(location, bool(x), bool(y), bool(z), bool(w))
    }
}

public class BoolVec4ArrayUniform(name: String, length: Int) : ArrayUniform(name, GL20.GL_BOOL_VEC4, length) {
    private val values: IntArray = IntArray(length * 4)

    public fun getX(index: Int): Boolean = bool(values[index * 4])
    public fun getY(index: Int): Boolean = bool(values[index * 4 + 1])
    public fun getZ(index: Int): Boolean = bool(values[index * 4 + 2])
    public fun getW(index: Int): Boolean = bool(values[index * 4 + 3])
    public fun setX(index: Int, x: Boolean) {
        values[index * 4] = bool(x)
    }

    public fun setY(index: Int, y: Boolean) {
        values[index * 4 + 1] = bool(y)
    }

    public fun setZ(index: Int, z: Boolean) {
        values[index * 4 + 2] = bool(z)
    }

    public fun setW(index: Int, w: Boolean) {
        values[index * 4 + 3] = bool(w)
    }

    public fun set(index: Int, x: Boolean, y: Boolean, z: Boolean, w: Boolean) {
        setX(index, x)
        setY(index, y)
        setZ(index, z)
        setW(index, w)
    }

    override fun push() {
        GL20.glUniform4iv(location, values)
    }
}
