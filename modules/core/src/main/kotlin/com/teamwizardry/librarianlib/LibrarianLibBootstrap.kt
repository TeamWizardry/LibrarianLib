package com.teamwizardry.librarianlib

import com.google.gson.Gson
import net.minecraftforge.fml.common.Mod

@Mod("librarianlib")
internal object LibrarianLibBootstrap {
    private val failedLoading = mutableListOf<String>()

    init {
        val names = resource("/META-INF/modules/index.txt")?.lines()
            ?: throw RuntimeException("Unable to find LibrarianLib modules list")
        names.forEach {
            LibrarianLib._modules[it] = null
        }

        LibrarianLib.logger.debug("Module index had ${names.size} modules")
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
        LibrarianLib.logger.debug("Finished loading modules")
    }

    private fun loadModule(name: String) {
        LibrarianLib.logger.info("Loading $name module")
        val info = ModuleInfo.loadModuleInfo(name)
        if(info == null) {
            failedLoading.add(name)
            LibrarianLib.logger.error("Unable to find module info file for $name, skipping")
            LibrarianLib._modules.remove(name)
            return
        }
        try {
            Class.forName(info.mainClass, false, this.javaClass.classLoader)
        } catch(e: ClassNotFoundException) {
            failedLoading.add(name)
            LibrarianLib.logger.error("Unable to find module class ${info.mainClass}, skipping.")
            return
        }
        val clazz = Class.forName(info.mainClass)
        val instance = (clazz.kotlin.objectInstance ?: clazz.newInstance()) as LibrarianLibModule
        LibrarianLib._modules[name] = instance
        LibrarianLib.logger.info("Finished loading $name module")
    }

    private fun resource(path: String): String? {
        return javaClass.getResourceAsStream(path)?.readBytes()?.let { String(it) }
    }
}