package com.teamwizardry.librarianlib

import com.google.gson.Gson
import com.teamwizardry.librarianlib.core.bridge.ASMEnvCheckTarget
import com.teamwizardry.librarianlib.core.bridge.MixinEnvCheckTarget
import net.minecraftforge.fml.common.Mod

@Mod("librarianlib")
internal object LibrarianLibBootstrap {
    private val failedLoading = mutableListOf<String>()

    init {
        checkEnvironment()

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

    /**
     * Perform a few fail-fast checks for the LibrarianLib environment.
     *
     * The liblib build environment is pretty complex, so it would be easy to have one of these break, so we want to
     * fail immediately instead of crashing when we try to use the result of one of them.
     */
    private fun checkEnvironment() {
        LibrarianLib.logger.debug("Checking environment")

        LibrarianLib.logger.debug("Checking if Mixins are being applied")
        val mixinPatched = MixinEnvCheckTarget().isPatched
        if(mixinPatched)
            LibrarianLib.logger.debug("Environment check passed: Mixins are being applied")
        else
            LibrarianLib.logger.error("Environment check failed: Mixins are not being applied")

        LibrarianLib.logger.debug("Checking if ASM transformers are being applied")
        val asmPatched = ASMEnvCheckTarget().isPatched
        if(asmPatched)
            LibrarianLib.logger.debug("Environment check passed: ASM transformers are being applied")
        else
            LibrarianLib.logger.error("Environment check failed: ASM transformers are not being applied")

        if(!mixinPatched || !asmPatched) {
            throw RuntimeException("LibrarianLib environment checks failed")
        }
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