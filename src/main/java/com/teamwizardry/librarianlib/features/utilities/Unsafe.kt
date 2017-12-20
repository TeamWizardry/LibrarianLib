package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.LoaderState
import sun.misc.Unsafe
import java.lang.reflect.Field

/**
 * Created by Elad on 12/20/2016.
 */
private val unsafe by lazy {
    val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
    theUnsafe.isAccessible = true
    theUnsafe.get(null) as Unsafe
}

fun hookIntoUnsafe() {
    if (!Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION))
        unsafeAllowedModIds.add(currentModId)
}

internal var unsafeAllowedModIds = mutableListOf<String>()

/**
 * This method provides a safe and fast way of accessing Unsafe methods but requires
 * the mod ID accessing it to be registered to be able to access it without errors.
 * In order to register your mod ID, send an IMC message to LibrarianLib with
 * title "Unsafe" and put your mod ID as the content.
 * This registration system is put in place as a minor security measure against abuse
 * of Unsafe.
 */
@JvmName("getUnsafeSafely")
@JvmOverloads
fun getUnsafe(more: Int = 0): Unsafe {
    val clazz = Class.forName(Throwable().stackTrace[2 + more].className)
    val modid = OwnershipHandler.getModId(clazz)
    if (modid in unsafeAllowedModIds || modid == LibrarianLib.MODID) return unsafe
    throw IllegalAccessError("Tried to access Unsafe from $modid")
}


@Suppress("UNCHECKED_CAST")
fun <T> Class<T>.newInstanceUnsafe(more: Int = 0) = getUnsafe(1 + more).allocateInstance(this) as? T ?: throw ClassNotFoundException("Invalid class $this")

fun Throwable.throwUnsafely(more: Int = 0) = getUnsafe(1 + more).throwException(this)
@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
@Deprecated("Deprecated in java")
fun Field.getOffset(more: Int = 0) = getUnsafe(1 + more).fieldOffset(this)

inline fun <reified T : Any> T?.orNewUnsafe(): T = this ?: T::class.java.newInstanceUnsafe(1)
inline fun <reified T : Any> T?.orNew(): T = this ?: T::class.java.constructors.elementAt(0).newInstance() as T
