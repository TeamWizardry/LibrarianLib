@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.NonNullList
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.Loader

/**
 * Created by TheCodeWarrior
 */
fun <T : Any> nonnullListOf(): NonNullList<T> = NonNullList.create<T>()

fun <T : Any> nonnullListOf(vararg items: T) = items.toNonnullList()
fun <T : Any> nonnullListOf(count: Int, default: T) = NonNullList.withSize(count, default)

fun vec(x: Number, y: Number) = Vec2d(x.toDouble(), y.toDouble())

fun vec(x: Number, y: Number, z: Number) = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

internal var modIdOverride: String? = null

val currentModId: String
    get() = modIdOverride ?: Loader.instance().activeModContainer()?.modId ?: ""

@JvmName("getModId")
fun getCurrentModId() = if (currentModId == "") currentModId else OwnershipHandler.getModId(Class.forName(Throwable().stackTrace[2].className))

