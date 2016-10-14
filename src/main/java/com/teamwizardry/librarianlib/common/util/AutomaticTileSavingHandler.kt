package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.lang.reflect.Field

/**
 * Created by Elad on 10/14/2016.
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

interface Serializator<T> {
    fun writeToNBT(t: T, nbt: NBTTagCompound, name: String)
    fun readFromNBT(nbt: NBTTagCompound, name: String): T
}


@Target(AnnotationTarget.FIELD) annotation class Save(val serializator: String = "")