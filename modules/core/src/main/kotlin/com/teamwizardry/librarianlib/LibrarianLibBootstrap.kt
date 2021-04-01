package com.teamwizardry.librarianlib

import com.google.gson.Gson
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-core")
internal object LibrarianLibBootstrap {
    private val modEventBus: IEventBus = MOD_BUS
    private val logger = LogManager.getLogger("LibrarianLib Bootstrap")
    private val failedLoading = mutableListOf<String>()

    init {
        if("bootstrap" in System.getProperty("librarianlib.debug.modules", "").split(","))
            changeLogLevel(logger, Level.DEBUG)


        val names = resource("/META-INF/ll/core/modules.txt")?.lines()
            ?: throw RuntimeException("Unable to find LibrarianLib modules list")
        logger.info("Module index contained ${names.size} modules")
        logger.info("To enable LibrarianLib debug logging, add the `-Dlibrarianlib.debug.modules=<module>,<module>` VM option")
        names.forEach {
            try {
                loadModule(it)
            } catch (e: Exception) {
                throw RuntimeException("Error loading LibrarianLib module $it", e)
            }
        }
        if(failedLoading.isNotEmpty()) {
            throw RuntimeException("Failed to load LibrarianLib modules [${failedLoading.joinToString(", ")}]")
        }
        logger.info("Finished loading modules")
    }

    private fun loadModule(name: String) {
        logger.info("Loading module: $name")
        val info = ModuleInfo.loadModuleInfo(name)
        if(info == null) {
            logger.info("Unable to find module info file for $name, skipping")
            return
        }
        try {
            Class.forName(info.mainClass, false, this.javaClass.classLoader)
        } catch (e: ClassNotFoundException) {
            failedLoading.add(name)
            logger.error("Unable to find module class ${info.mainClass}.")
            return
        }
        val clazz = Class.forName(info.mainClass)
        val module = clazz.kotlin.objectInstance ?: clazz.newInstance()
        if(module is LibrarianLibModule && module.debugEnabled) {
            logger.info("Debug logging enabled for $name")
        }
        MinecraftForge.EVENT_BUS.register(module)
        modEventBus.register(module)

        logger.info("Finished loading $name")
    }

    private fun resource(path: String): String? {
        return javaClass.getResourceAsStream(path)?.readBytes()?.let { String(it) }
    }

    private data class ModuleInfo(
        val mainClass: String
    ) {
        companion object {
            private val gson = Gson()
            fun loadModuleInfo(name: String): ModuleInfo? {
                return ModuleInfo::class.java.getResourceAsStream("/META-INF/ll/$name/module.json")?.readBytes()?.let {
                    gson.fromJson(String(it), ModuleInfo::class.java)
                }
            }
        }
    }

    private fun changeLogLevel(logger: Logger, level: Level) {
        Configurator.setLevel(logger.name, level)
    }
}