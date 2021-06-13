@file:JvmName("AlbedoDebugging")
package com.teamwizardry.librarianlib.albedo

import java.nio.*

public fun ByteBuffer.hex(): String {
    val value = ByteArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
}

public fun CharBuffer.getContents(): CharArray {
    val value = CharArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

public fun DoubleBuffer.getContents(): DoubleArray {
    val value = DoubleArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

public fun FloatBuffer.getContents(): FloatArray {
    val value = FloatArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

public fun IntBuffer.getContents(): IntArray {
    val value = IntArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

public fun LongBuffer.getContents(): LongArray {
    val value = LongArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

public fun ShortBuffer.getContents(): ShortArray {
    val value = ShortArray(this.remaining())
    val pos = this.position()
    this.get(value)
    this.position(pos)
    return value
}

