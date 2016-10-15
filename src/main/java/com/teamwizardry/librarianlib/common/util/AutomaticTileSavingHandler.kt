package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.lang.reflect.Field

/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for the automatic tile entity saving system. This object should not be accessed
 * outside of the library. It is for internal use.
 */
object AutomaticTileSavingHandler {
    val fieldMap: MutableMap<Field, Pair<Class<out Serializator<*>>?, AnnotationHelper.AnnotationInfo>> = mutableMapOf()
    fun init(asm: ASMDataTable?) {
        if (asm == null) return
        findByClass(Any::class.java, asm)
        findByClass(Boolean::class.javaPrimitiveType!!, asm)
        findByClass(Char::class.javaPrimitiveType!!, asm)
        findByClass(Byte::class.javaPrimitiveType!!, asm)
        findByClass(Short::class.javaPrimitiveType!!, asm)
        findByClass(Int::class.javaPrimitiveType!!, asm)
        findByClass(Float::class.javaPrimitiveType!!, asm)
        findByClass(Long::class.javaPrimitiveType!!, asm)
        if (LibrarianLib.DEV_ENVIRONMENT) {
            fieldMap.keys.forEach { println("Found savable field ${it.declaringClass.name}.${it.name}") }
            if (fieldMap.keys.size == 0) println("No fields found!")
        }



    }
    private fun findByClass(clazz: Class<*>, asm: ASMDataTable) {
        AnnotationHelper.findAnnotatedObjects(asm, clazz, Save::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            val clazz0 =
                    if (info.getString("serializator", null) != null && Class.forName(info.getString("serializator", null)) != null)
                        Class.forName(info.getString("serializator", null)).asSubclass(Serializator::class.java)
                    else null
            fieldMap.put(field, clazz0 to info)

        })
    }


}
/**
 * This interface should be implemented when you want to save a non-primitive/NBTTagCompound/ItemStack type
 * using the LibrarianLib automatic saving system.
 * Implement this interface on a public, non-static class with a public constructor and make its type annotation
 * the type you want to automatically save. Then put the full path to the class in the "serializator" field in [Save].
 * An example can be found in com.teamwizardry.librarianlib.common.test.MutableSerializable and its corresponding Tile
 * Entity, com.teamwizardry.librarianlib.common.test.TileTest.
 * See the comment on [Save] for more information.
 */
interface Serializator<T> {
    fun writeToNBT(t: T, nbt: NBTTagCompound, name: String)
    fun readFromNBT(nbt: NBTTagCompound, name: String): T
}


/**
 * This annotation should be applied on fields of classes that extend [TileMod] that you wish to have automatically saved.
 * What this means is that you will not have to use the [writeToNBT] and [readToNBT] methods to read and write them.
 * You will still need to markDirty as needed. Natively, this annotation supports saving the following types:
 * Boolean, Character, Byte, Short, Integer, Float, Long, NBTTagCompound, and ItemStack. In case you wish to save types
 * other than those listed above, you will need to implement a serializator. More information about serializators
 * can be found in the comment on [Serializator].
 */
@Target(AnnotationTarget.FIELD) annotation class Save(val serializator: String = "")