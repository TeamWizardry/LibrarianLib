package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by TheCodeWarrior
 */
object AutoRegisterHandler {
    fun handle(e: FMLPreInitializationEvent) {
        val table = e.asmData
        getAnnotatedBy(TileRegister::class.java, TileEntity::class.java, table).forEach {
            AbstractSaveHandler.cacheFields(it.clazz)
            GameRegistry.registerTileEntity(it.clazz, it["id"])
        }
        if(Loader.isModLoaded("mcmultipart")) {
            getAnnotatedBy(PartRegister::class.java, IMultipart::class.java, table).forEach {
                AbstractSaveHandler.cacheFields(it.clazz)
                MultipartRegistry.registerPart(it.clazz, it["id"])
            }
        }

    }

    fun getAnnotatedBy(annotationClass: Class<out Annotation>, asmDataTable: ASMDataTable): List<AnnotatedClass<*>> {
        return getAnnotatedBy(annotationClass, Any::class.java, asmDataTable)
    }

    fun <T> getAnnotatedBy(annotationClass: Class<out Annotation>, instanceClass: Class<T>, asmDataTable: ASMDataTable): List<AnnotatedClass<out T>> {
        val annotationClassName = annotationClass.canonicalName
        val asmDatas = asmDataTable.getAll(annotationClassName)
        val classes = mutableListOf<AnnotatedClass<out T>>()
        for (asmData in asmDatas) {
            try {
                val asmClass = Class.forName(asmData.className)
                classes.add(AnnotatedClass(asmClass.asSubclass(instanceClass), asmData.annotationInfo))
            } catch (e: ClassNotFoundException) {
                LibrarianLog.error(e, "Failed to load: ${asmData.className}")
            } catch (e: ClassCastException) {
                LibrarianLog.error("${annotationClass.typeName} annotated class ${asmData.className} not a subclass of ${instanceClass.canonicalName}")
            }
        }
        return classes
    }

    data class AnnotatedClass<T>(val clazz: Class<T>, val properties: Map<String, Any>) {
        @Suppress("UNCHECKED_CAST")
        operator fun <T> get(property: String): T {
            return properties[property] as T
        }
    }
}
