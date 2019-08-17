@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.kotlin.toNonnullList
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.Loader
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *
 *
 * - nonnullListOf
 * - vec(x,y) / vec(x,y,z)
 * - val currentModId
 * - threadLocal delegate
 *
 *
 */

private class LibHelpers // so we can jump to this file with "go to class"

fun <T : Any> nonnullListOf(): NonNullList<T> = NonNullList.create<T>()

fun <T : Any> nonnullListOf(vararg items: T) = items.toNonnullList()
fun <T : Any> nonnullListOf(count: Int, default: T): Collection<T> = NonNullList.withSize(count, default)

fun aabb(a: Vec3d, b: Vec3d) = AxisAlignedBB(a.x, a.y, a.z, b.x, b.y, b.z)

internal var modIdOverride: String? = null

val currentModId: String
    get() = modIdOverride ?:
            Loader.instance().activeModContainer()?.modId ?:
            OwnershipHandler.getModId(Class.forName(Throwable().stackTrace[2].className)) ?:
            ""

fun <T : Any?> threadLocal() = ThreadLocalDelegate<T>(null)
fun <T> threadLocal(initial: () -> T) = ThreadLocalDelegate(initial)

class ThreadLocalDelegate<T>(initial: (() -> T)?) : ReadWriteProperty<Any, T> {
    private val local = if (initial == null) ThreadLocal<T>() else ThreadLocal.withInitial(initial)

    override fun getValue(thisRef: Any, property: KProperty<*>): T = local.get()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = local.set(value)
}

private class Collision : Block(Material.AIR) {
    companion object {
        fun exposedCollision(pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, blockBox: AxisAlignedBB?) =
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, blockBox)
    }
}

fun addCollisionBoxToList(pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, blockBox: AxisAlignedBB?) =
        Collision.exposedCollision(pos, entityBox, collidingBoxes, blockBox)


// legacy Vec2d =============================================================
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Number, y: Number) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Int) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Float) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Double) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Int) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Float) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Double) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Int) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Float) = vec(x, y)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Double) = vec(x, y)

// legacy Vec3d =============================================================
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Number, y: Number, z: Number) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Int, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Int, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Int, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Float, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Float, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Float, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Double, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Double, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Int, y: Double, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Int, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Int, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Int, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Float, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Float, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Float, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Double, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Double, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Float, y: Double, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Int, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Int, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Int, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Float, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Float, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Float, z: Double) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Double, z: Int) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Double, z: Float) = vec(x, y, z)
@Deprecated("Superseded by inline function", level = DeprecationLevel.HIDDEN)
@JvmName("vec")
fun legacyVec(x: Double, y: Double, z: Double) = vec(x, y, z)
