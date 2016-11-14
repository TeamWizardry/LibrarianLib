package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.LibrarianLib
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

    private fun generatePrefixes() {
        Loader.instance().activeModList
                .flatMap { it.ownedPackages.map { pack -> pack to it.modId } }
                .forEach { prefixes.put(it.first, it.second) }

        if (LibrarianLib.DEV_ENVIRONMENT) {
            val pad = " " * LibrarianLib.MODID.length
            LibrarianLog.info("${LibrarianLib.MODID} | Prefixes: ")
            for (mod in Loader.instance().activeModList) if (mod.ownedPackages.isNotEmpty()) {
                LibrarianLog.info("$pad | *** Owned by `${mod.modId}` ***")
                for (pack in mod.ownedPackages.toSet())
                    LibrarianLog.info("$pad | | $pack")
            }
        }
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
            var build = "AutoRegister Errors: No modid specified!"
            build += "\nDefined prefixes:"

            val keyMax = prefixes.maxBy { it.second.length }?.second?.length ?: 0
            for ((prefix, modid) in prefixes) {
                val spaces = keyMax - modid.length
                build += "\n${" " * spaces}$modid | $prefix"
            }
            build += "\nErrored registers:"
            for ((name, list) in errors) {
                build += "\n> @$name"
                list.forEach {
                    build += "\n  | ${it.canonicalName}"
                }
            }
            throw RuntimeException("FATAL: AutoRegister failed - \n$build")
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
