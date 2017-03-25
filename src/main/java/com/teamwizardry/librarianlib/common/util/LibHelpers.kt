@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.NonNullList
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.Loader
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class LibHelpers // so we can jump to this file with "go to class"

/**
 * Created by TheCodeWarrior
 */
fun <T : Any> nonnullListOf(): NonNullList<T> = NonNullList.create<T>()

fun <T : Any> nonnullListOf(vararg items: T) = items.toNonnullList()
fun <T : Any> nonnullListOf(count: Int, default: T): Collection<T> = NonNullList.withSize(count, default)

fun vec(x: Number, y: Number) = Vec2d(x.toDouble(), y.toDouble())

fun vec(x: Number, y: Number, z: Number) = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

internal var modIdOverride: String? = null

val currentModId: String
    get() = modIdOverride ?:
            Loader.instance().activeModContainer()?.modId ?:
            OwnershipHandler.getModId(Class.forName(Throwable().stackTrace[2].className)) ?:
            ""

fun <T> threadLocal(initial: (() -> T)? = null) = ThreadLocalDelegate(initial)

class ThreadLocalDelegate<T>(initial: (() -> T)?) : ReadWriteProperty<Any, T> {
    private val local = if (initial == null) ThreadLocal<T>() else ThreadLocal.withInitial(initial)

    override fun getValue(thisRef: Any, property: KProperty<*>): T = local.get()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = local.set(value)
}
