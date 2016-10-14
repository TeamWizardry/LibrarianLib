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
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        AnnotationHelper.findAnnotatedObjects(asm, Any::class.java, Save::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            val clazz =
                    if (info.getString("serializator", null) != null && Class.forName(info.getString("serializator", null)) != null)
                        Class.forName(info.getString("serializator", null)).asSubclass(Serializator::class.java)
                    else null
            fieldMap.put(field, clazz to info)

        })
        AnnotationHelper.findAnnotatedObjects(asm, Number::class.java, Save::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            val clazz =
                    if (info.getString("serializator", null) != null && Class.forName(info.getString("serializator", null)) != null)
                        Class.forName(info.getString("serializator", null)).asSubclass(Serializator::class.java)
                    else null
            fieldMap.put(field, clazz to info)

        })
        if (LibrarianLib.DEV_ENVIRONMENT) {
            fieldMap.keys.forEach { println("Found savable field ${it.declaringClass.name}.${it.name}") }
            if (fieldMap.keys.size == 0) println("No fields found!")
        }



    }


}

interface Serializator<T> {
    fun writeToNBT(t: T, nbt: NBTTagCompound, name: String)
    fun readFromNBT(nbt: NBTTagCompound, name: String): T
}


@Target(AnnotationTarget.FIELD) annotation class Save(val serializator: String = "")