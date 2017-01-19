package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import sun.misc.Unsafe
import java.lang.reflect.Field
import javax.management.relation.InvalidRelationServiceException

/**
 * Created by Elad on 12/20/2016.
 */
private val unsafe by lazy {
    val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
    theUnsafe.isAccessible = true
    theUnsafe.get(null) as Unsafe
}

@JvmName("getUnsafeSafely")
fun getUnsafe(): Unsafe {
    val clazz = Class.forName(Throwable().stackTrace[2].className)
    val modid = OwnershipHandler.getModId(clazz)
    println(clazz.name + " " + modid)
    if(modid in LibrarianLib.unsafeAllowedModIds || modid == LibrarianLib.MODID) return unsafe
    throw IllegalAccessError("Tried to access Unsafe from $modid")
}


internal fun <T> Class<T>.newInstanceUnsafe() = unsafe.allocateInstance(this) as? T ?: throw InvalidRelationServiceException("Invalid class $this?")
internal fun Throwable.throwUnsafely() = unsafe.throwException(this)
internal fun Field.getOffset() = unsafe.fieldOffset(this)
internal inline fun <reified T : Any> T?.orNewUnsafe(): T = this ?: T::class.java.newInstanceUnsafe();
internal inline fun <reified T : Any> T?.orNew(): T = this ?: T::class.constructors.elementAt(0).call()