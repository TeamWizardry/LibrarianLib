package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.VariantHelper
import com.teamwizardry.librarianlib.common.util.times
// todo once mcmultipart is 1.11
//import mcmultipart.multipart.IMultipart
//import mcmultipart.multipart.MultipartRegistry
import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.common.util.toRl
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by TheCodeWarrior
 */
object AutoRegisterHandler {

    fun handle(e: FMLPreInitializationEvent) {
        val table = e.asmData
        val errors = mutableMapOf<String, MutableList<Class<*>>>()

        getAnnotatedBy(TileRegister::class.java, TileEntity::class.java, table).forEach {
            val name = it.get<String>("value")
            val modId = OwnershipHandler.getModId(it.clazz)
            if (name == null) {
                errors.getOrPut("TileRegister") {
                    mutableListOf()
                }.add(it.clazz)
            } else {
                var loc: ResourceLocation
                if (name != "")
                    loc = ResourceLocation(name)
                else loc = it.clazz.canonicalName.toLowerCase().toRl()
                if (loc.resourceDomain == "minecraft" && modId == null)
                    errors.getOrPut("TileRegister") {
                        mutableListOf()
                    }.add(it.clazz)
                else {
                    if (loc.resourceDomain == "minecraft")
                        loc = ResourceLocation(modId, loc.resourcePath)
                    AbstractSaveHandler.cacheFields(it.clazz)
                    GameRegistry.registerTileEntity(it.clazz, loc.toString())
                }
            }

        }
// todo once mcmultipart is 1.11
//        if (Loader.isModLoaded("mcmultipart")) {
//            getAnnotatedBy(PartRegister::class.java, IMultipart::class.java, table).forEach {
//                var name = it.get<String>("value")
//                val modId = getModid(it.clazz)
//                if (name == null) {
//                    errors.getOrPut("PartRegister", {
//                        mutableListOf()
//                    }).add(it.clazz)
//                } else {
//                    name = VariantHelper.toSnakeCase(name)
//                    var loc = ResourceLocation(name)
//                    if (loc.resourceDomain == "minecraft" && modId == null)
//                        errors.getOrPut("PartRegister", { mutableListOf() }).add(it.clazz)
//                    else {
//                        if (loc.resourceDomain == "minecraft")
//                            loc = ResourceLocation(modId, loc.resourcePath)
//                        AbstractSaveHandler.cacheFields(it.clazz)
//                        MultipartRegistry.registerPart(it.clazz, loc.toString())
//                    }
//                }
//            }
//        }
        getAnnotatedBy(PacketRegister::class.java, PacketBase::class.java, table).forEach {
            val side = it.get<ModAnnotation.EnumHolder>("value")?.value?.run { Side.valueOf(this) }
            if (side == null)
                errors.getOrPut("PacketRegister") {
                    mutableListOf()
                }.add(it.clazz)
            else
                PacketHandler.register(it.clazz, side)
        }

        if (errors.isNotEmpty()) {
            val build = mutableListOf("AutoRegister Errors: No modId specified!")
            build.add("Defined prefixes:")

            for ((prefix) in OwnershipHandler.prefixes) {
                build.add(prefix)
            }
            build.add("Errored registers:")
            for ((name, list) in errors) {
                build.add("> @$name")
                list.forEach {
                    build.add(it.canonicalName)
                }
            }
            LibrarianLog.bigDie("FATAL: AutoRegister failed (This should be impossible!)", build)
            FMLCommonHandler.instance().handleExit(0)
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
