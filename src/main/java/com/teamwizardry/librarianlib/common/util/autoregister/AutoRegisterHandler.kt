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
    private val prefixes = mutableMapOf<String, String>()

    private fun generatePrefixes() {
        Loader.instance().activeModList
                .flatMap { it.ownedPackages.map { pack -> pack to it.modId } }
                .forEach { prefixes.put(it.first, it.second) }
    }

    private fun getModid(clazz: Class<*>): String? {
        val name = clazz.canonicalName
        prefixes.forEach {
            if (name.startsWith(it.key))
                return it.value
        }
        return null
    }

    fun handle(e: FMLPreInitializationEvent) {
        generatePrefixes()

        val table = e.asmData
        val errors = mutableMapOf<String, MutableList<Class<*>>>()

        getAnnotatedBy(TileRegister::class.java, TileEntity::class.java, table).forEach {
            val name = it.get<String>("value")
            val modId = getModid(it.clazz)
            if (name == null) {
                errors.getOrPut("TileRegister", { mutableListOf() }).add(it.clazz)
            } else {
                var loc = ResourceLocation(name)
                if (loc.resourceDomain == "minecraft" && modId == null)
                    errors.getOrPut("TileRegister", { mutableListOf() }).add(it.clazz)
                else {
                    if (loc.resourceDomain == "minecraft")
                        loc = ResourceLocation(modId, loc.resourcePath)
                    AbstractSaveHandler.cacheFields(it.clazz)
                    GameRegistry.registerTileEntity(it.clazz, loc.toString())
                }
            }

        }
        if (Loader.isModLoaded("mcmultipart")) {
            getAnnotatedBy(PartRegister::class.java, IMultipart::class.java, table).forEach {
                val name = it.get<String>("value")
                val modId = getModid(it.clazz)
                if (name == null) {
                    errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
                } else {
                    var loc = ResourceLocation(name)
                    if (loc.resourceDomain == "minecraft" && modId == null)
                        errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
                    else {
                        if (loc.resourceDomain == "minecraft")
                            loc = ResourceLocation(modId, loc.resourcePath)
                        AbstractSaveHandler.cacheFields(it.clazz)
                        MultipartRegistry.registerPart(it.clazz, loc.toString())
                    }
                }
            }
        }

        if(errors.isNotEmpty()) {
            var build = "AutoRegister Errors: No modId specified!"
            build += "\nDefined prefixes:"

            val keyMax = prefixes.maxBy { it.value.length }?.value?.length ?: 0
            for ((prefix, modId) in prefixes) {
                val spaces = keyMax - modId.length
                build += "\n${" " * spaces}$modId | $prefix"
            }
            build += "\nErrored registers:"
            for ((name, list) in errors) {
                build += "\n> @$name"
                list.forEach {
                    build += "\n  | ${it.canonicalName}"
                }
            }
            throw RuntimeException("FATAL: AutoRegister failed (This should be impossible!) - \n$build")
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
            return properties[property] as? T?
        }
    }
}
