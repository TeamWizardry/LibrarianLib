package com.teamwizardry.librarianlib.core.common

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.kotlin.times
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.nio.file.Paths

object OwnershipHandler {

    val prefixes: List<Pair<String, String>> = Loader.instance().activeModList
            .filter { it.ownedPackages.isNotEmpty() }
            .flatMap { it.ownedPackages.map { pack -> it.modId to pack } }

    val DEV_OWNED: List<String>

    val BASE_PATHS = arrayOf(
            "src/main/java",
            "src/main/kotlin",
            "src/main/scala",
            "test/java",
            "test/kotlin",
            "test/scala"
    )

    val ABS_BASE = Paths.get(LibrarianLib.PROXY.getDataFolder().absolutePath)?.parent?.parent?.toString() ?: ""

    init {

        val owned = mutableListOf<String>()

        if (LibrarianLib.DEV_ENVIRONMENT) {
            val pad = " " * LibrarianLib.MODID.length
            LibrarianLog.info("${LibrarianLib.MODID} | Prefixes: ")
            for (mod in Loader.instance().activeModList) if (mod.ownedPackages.isNotEmpty()) {
                LibrarianLog.info("$pad | *** Owned by `${mod.modId}` ***")
                for (pack in mod.ownedPackages.toSet())
                    LibrarianLog.info("$pad | | $pack")
            }

            for ((mod, pack) in prefixes) {
                if (mod in owned) continue
                for (base in BASE_PATHS) {
                    val path = "${ABS_BASE}/$base/" + pack.replace(".", "/")
                    val file = File(path)
                    if (file.exists() && file.isDirectory) {
                        owned.add(mod)
                        break
                    }
                }
            }
        }




        DEV_OWNED = owned
    }

    fun getModId(clazz: Class<*>): String? {
        val name = clazz.canonicalName
        prefixes.forEach {
            if (name.startsWith(it.second))
                return it.first
        }
        return null
    }
}
