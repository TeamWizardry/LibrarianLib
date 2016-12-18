package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.NonNullList
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.Loader

/**
 * Created by TheCodeWarrior
 */
fun <T> nonnullListOf(): NonNullList<T> = NonNullList.create<T>()
fun <T> nonnullListOf(vararg items: T) = items.toNonnullList()

fun vec(x: Number, y: Number) = Vec2d(x.toDouble(), y.toDouble())

fun vec(x: Number, y: Number, z: Number) = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

val currentModId: String
    get() = Loader.instance().activeModContainer()?.modId ?: ""
