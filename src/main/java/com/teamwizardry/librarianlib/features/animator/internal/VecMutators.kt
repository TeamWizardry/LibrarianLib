package com.teamwizardry.librarianlib.features.animator.internal

import com.teamwizardry.librarianlib.features.animator.ImmutableFieldMutator
import com.teamwizardry.librarianlib.features.animator.ImmutableFieldMutatorHandler
import com.teamwizardry.librarianlib.features.animator.ImmutableFieldMutatorProvider
import com.teamwizardry.librarianlib.features.kotlin.withX
import com.teamwizardry.librarianlib.features.kotlin.withY
import com.teamwizardry.librarianlib.features.kotlin.withZ
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.math.Vec3d

/**
 * TODO: Document file VecMutators
 *
 * Created by TheCodeWarrior
 */
object VecMutators {
    fun doubleFrom(any: Any): Double {
        any as? Number ?: throw IllegalArgumentException("Cannot cast `${any.javaClass.canonicalName}` to a Number")
        return any.toDouble()
    }

    init {

        //region Vec2d
        ImmutableFieldMutatorHandler.registerProvider(Vec2d::class.java, object : ImmutableFieldMutatorProvider<Vec2d> {
            override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vec2d>? {
                when (name) {
                    "x" -> {
                        return object : ImmutableFieldMutator<Vec2d> {
                            override fun mutate(target: Vec2d, value: Any?): Vec2d {
                                value ?: throw IllegalArgumentException("Cannot set Vec2d.x to null")
                                return target.withX(doubleFrom(value))
                            }
                        }
                    }
                    "y" -> {
                        return object : ImmutableFieldMutator<Vec2d> {
                            override fun mutate(target: Vec2d, value: Any?): Vec2d {
                                value ?: throw IllegalArgumentException("Cannot set Vec2d.y to null")
                                return target.withY(doubleFrom(value))
                            }
                        }
                    }
                }
                return null
            }

        })
        //endregion

        //region Vec3d
        ImmutableFieldMutatorHandler.registerProvider(Vec3d::class.java, object : ImmutableFieldMutatorProvider<Vec3d> {
            override fun getMutatorForImmutableField(name: String): ImmutableFieldMutator<Vec3d>? {
                when (name) {
                    "x" -> {
                        return object : ImmutableFieldMutator<Vec3d> {
                            override fun mutate(target: Vec3d, value: Any?): Vec3d {
                                value ?: throw IllegalArgumentException("Cannot set Vec3d.x to null")
                                return target.withX(doubleFrom(value))
                            }
                        }
                    }
                    "y" -> {
                        return object : ImmutableFieldMutator<Vec3d> {
                            override fun mutate(target: Vec3d, value: Any?): Vec3d {
                                value ?: throw IllegalArgumentException("Cannot set Vec3d.y to null")
                                return target.withX(doubleFrom(value))
                            }
                        }
                    }
                    "z" -> {
                        return object : ImmutableFieldMutator<Vec3d> {
                            override fun mutate(target: Vec3d, value: Any?): Vec3d {
                                value ?: throw IllegalArgumentException("Cannot set Vec3d.z to null")
                                return target.withZ(doubleFrom(value))
                            }
                        }
                    }
                }
                return null
            }

        })
        //endregion
    }
}
