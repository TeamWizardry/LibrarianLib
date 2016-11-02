package com.teamwizardry.librarianlib.test.bitstorage.block

import net.minecraftforge.common.property.IUnlistedProperty

/**
 * Created by TheCodeWarrior
 */
abstract class UPropertyHelper<T>(val name_: String, val klass: Class<T>) : IUnlistedProperty<T> {
    override fun getType() = klass

    override fun valueToString(value: T) = value.toString()

    override fun getName() = name_

    override fun isValid(value: T) = true

}

class UPropertyInt(name: String) : UPropertyHelper<Int>(name, Int::class.javaObjectType)
class UPropertyFloat(name: String) : UPropertyHelper<Float>(name, Float::class.javaObjectType)
class UPropertyBoolean(name: String) : UPropertyHelper<Boolean>(name, Boolean::class.javaObjectType)
class UPropertyEnum<T : Enum<T>>(name: String, enum: Class<T>) : UPropertyHelper<T>(name, enum)
