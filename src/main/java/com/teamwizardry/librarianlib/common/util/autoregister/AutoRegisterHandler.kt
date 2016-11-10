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
     * e.g. "com.teamwizardry.refraction." (I like to end it with a dot for clarity)
     */
    @JvmStatic
    @JvmOverloads
    fun registerPrefix(prefix: String, modid: String = Loader.instance().activeModContainer().modId) {
        prefixes.add(prefix to modid)
    }

    private fun getModid(clazz: Class<*>): String? {
        val name = clazz.canonicalName
        return prefixes.find { name.startsWith(it.first) }?.second
    }

    fun handle(e: FMLPreInitializationEvent) {
        val table = e.asmData
        val errors = mutableMapOf<String, MutableList<Class<*>>>()

        getAnnotatedBy(TileRegister::class.java, TileEntity::class.java, table).forEach {
            val name = it.get<String>("value")
            val modid = getModid(it.clazz)
            if (name == null) {
                errors.getOrPut("TileRegister", { mutableListOf() }).add(it.clazz)
            } else {
                var loc = ResourceLocation(name)
                if (loc.resourceDomain == "minecraft" && modid == null)
                    errors.getOrPut("TileRegister", { mutableListOf() }).add(it.clazz)
                else {
                    if (loc.resourceDomain == "minecraft")
                        loc = ResourceLocation(modid, loc.resourcePath)
                    AbstractSaveHandler.cacheFields(it.clazz)
                    GameRegistry.registerTileEntity(it.clazz, loc.toString())
                }
            }

        }
        if (Loader.isModLoaded("mcmultipart")) {
            getAnnotatedBy(PartRegister::class.java, IMultipart::class.java, table).forEach {
                val name = it.get<String>("value")
                val modid = getModid(it.clazz)
                if (name == null) {
                    errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
                } else {
                    var loc = ResourceLocation(name)
                    if (loc.resourceDomain == "minecraft" && modid == null)
                        errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
                    else {
                        if (loc.resourceDomain == "minecraft")
                            loc = ResourceLocation(modid, loc.resourcePath)
                        AbstractSaveHandler.cacheFields(it.clazz)
                        MultipartRegistry.registerPart(it.clazz, loc.toString())
                    }
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
            return properties[property] as? T?
        }
    }
}
