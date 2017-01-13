package com.teamwizardry.librarianlib.common.util

import sun.misc.Unsafe
import java.lang.reflect.Field

/**
 * Created by Elad on 12/20/2016.
 */
internal val unsafe by lazy {
    val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
    theUnsafe.isAccessible = true
    theUnsafe.get(null) as Unsafe }
internal fun <T> Class<T>.newInstanceUnsafe() = unsafe.allocateInstance(this) as T
internal fun Throwable.throwUnsafely() = unsafe.throwException(this)
internal fun Field.getOffset() = unsafe.fieldOffset(this)