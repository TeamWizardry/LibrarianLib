package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.common.util.times
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by TheCodeWarrior
 */
object AutoRegisterHandler {
    private val prefixes = mutableListOf<Pair<String, String>>()

    /**
     * Registers a class prefix for a mod. Usually this will be the main package for your mod.
     * e.g. "com.teamwizardry.refraction." (If it doesn't end with a dot this method will add one)
     */
    @JvmStatic
    @JvmOverloads
    fun registerPrefix(prefix: String, modid: String = Loader.instance().activeModContainer().modId) {
        val qualified = if(prefix.endsWith(".")) prefix else prefix + "."
        prefixes.add(qualified to modid)
    }

    private fun getModid(clazz: Class<*>): String? {
        val name = clazz.canonicalName
        return prefixes.find { name.startsWith(it.first) }?.second
    }

    fun handle(e: FMLPreInitializationEvent) {
        val table = e.asmData
        val errors = mutableMapOf<String, MutableList<Class<*>>>()

        getAnnotatedBy(TileRegister::class.java, TileEntity::class.java, table).forEach {
            val value = it.get<String>("value") ?: ""
            val name = if(":" in value) ResourceLocation(value).resourcePath else value
            val modid = if(":" in value) ResourceLocation(value).resourceDomain else getModid(it.clazz)
            if (value == "" || modid == null && ":" !in name)
                errors.getOrPut("TileRegister", { mutableListOf() }).add(it.clazz)
            else {
                AbstractSaveHandler.cacheFields(it.clazz)
                GameRegistry.registerTileEntity(it.clazz, "$modid:$name")
            }

        }
        if (Loader.isModLoaded("mcmultipart")) {
            getAnnotatedBy(PartRegister::class.java, IMultipart::class.java, table).forEach {
                val value = it.get<String>("value") ?: ""
                val name = if(":" in value) ResourceLocation(value).resourcePath else value
                val modid = if(":" in value) ResourceLocation(value).resourceDomain else getModid(it.clazz)
                if (value == "" || modid == null && ":" !in name)
                    errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
                else {
                    AbstractSaveHandler.cacheFields(it.clazz)
                    MultipartRegistry.registerPart(it.clazz, "$modid:$name")
                }
            }
        }

        if(errors.isNotEmpty()) {
            LibrarianLog.warn("AutoRegister Errors: No modid specified!")
            LibrarianLog.warn("Defined prefixes:")

            val keyMax = prefixes.maxBy { it.second.length }?.second?.length ?: 0
            for ((prefix, modid) in prefixes) {
                val spaces = keyMax - modid.length
                LibrarianLog.warn("${" " * spaces}$modid | $prefix")
            }
            LibrarianLog.warn("Errored registers:")
            for ((name, list) in errors) {
                LibrarianLog.warn("> @$name")
                list.forEach {
                    LibrarianLog.warn("  | ${it.canonicalName}")
                }
            }
            throw IllegalArgumentException("AutoRegister modid errors!!! (see log above)")
        }
    }

    private fun getAnnotatedBy(annotationClass: Class<out Annotation>, asmDataTable: ASMDataTable): List<AnnotatedClass<*>> {
        return getAnnotatedBy(annotationClass, Any::class.java, asmDataTable)
    }

    private fun <T> getAnnotatedBy(annotationClass: Class<out Annotation>, instanceClass: Class<T>, asmDataTable: ASMDataTable): List<AnnotatedClass<out T>> {
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

    private data class AnnotatedClass<T>(val clazz: Class<T>, val properties: Map<String, Any>) {
        @Suppress("UNCHECKED_CAST")
        operator fun <T> get(property: String): T? {
            return properties[property] as T?
        }
    }
}
