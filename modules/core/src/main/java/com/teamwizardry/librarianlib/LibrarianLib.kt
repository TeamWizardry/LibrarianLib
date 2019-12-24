package com.teamwizardry.librarianlib

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraftforge.fml.ModLoader
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib")
object LibrarianLib {
    val logger = LogManager.getLogger("LibrarianLib")

    internal var currentInfo: ModuleInfo? = null
    private val _modules = mutableMapOf<String, LibrarianLibModule?>()

    val modules: Map<String, LibrarianLibModule?> = _modules.unmodifiableView()

    init {
        val names = resource("/META-INF/modules/index.txt")?.lines()
            ?: throw RuntimeException("Unable to find LibrarianLib modules list")
        names.forEach {
            _modules[it] = null
        }

        logger.debug("Found ${names.size} modules")
        names.forEach {
            try {
                loadModule(it)
            } catch (e: Exception) {
                throw RuntimeException("Error loading LibrarianLib module $it", e)
            }
        }
        logger.debug("Finished loading modules")
    }

    private fun loadModule(name: String) {
        logger.info("Loading $name module")
        val info = resource("/META-INF/modules/$name.json")?.let { Gson().fromJson(it, ModuleInfo::class.java) }
        if(info == null) {
            logger.warn("Unable to find module info file for $name")
            _modules.remove(name)
            return
        }
        currentInfo = info
        val clazz = Class.forName(info.mainClass)
        val instance = (clazz.kotlin.objectInstance ?: clazz.newInstance()) as LibrarianLibModule
        _modules[name] = instance
        logger.info("Finished loading $name module")
    }

    private fun resource(path: String): String? {
        return javaClass.getResourceAsStream(path)?.readBytes()?.let { String(it) }
    }
}