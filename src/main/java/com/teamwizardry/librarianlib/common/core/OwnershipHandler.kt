package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.flatAssociateBy
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.nio.file.Paths

object OwnershipHandler {


    val prefixes: Map<String, String>

    val DEV_OWNED: List<String>

    val BASE_PATHS = arrayOf(
            "src/main/java",
            "src/main/kotlin",
            "src/main/scala",
            "src/test/java",
            "src/test/kotlin",
            "src/test/scala"
    )

    val ABS_BASE = Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent.toString()

    init {
        prefixes = Loader.instance().activeModList
                .flatAssociateBy { it.ownedPackages.map { pack -> pack to it.modId } }

        if (LibrarianLib.DEV_ENVIRONMENT) {
            val pad = " " * LibrarianLib.MODID.length
            LibrarianLog.info("${LibrarianLib.MODID} | Prefixes: ")
            for (mod in Loader.instance().activeModList) if (mod.ownedPackages.isNotEmpty()) {
                LibrarianLog.info("$pad | *** Owned by `${mod.modId}` ***")
                for (pack in mod.ownedPackages.toSet())
                    LibrarianLog.info("$pad | | $pack")
            }
        }

        val owned = mutableListOf<String>()

        for ((pack, mod) in prefixes) {
            if (mod in owned) continue
            for (base in BASE_PATHS) {
                val path = "$ABS_BASE/$base/" + pack.replace(".", "/")
                val file = File(path)
                if (file.exists() && file.isDirectory) {
                    owned.add(mod)
                    break
                }
            }
        }

        DEV_OWNED = owned
    }

    fun getModId(clazz: Class<*>): String? {
        val name = clazz.canonicalName
        prefixes.forEach {
            if (name.startsWith(it.key))
                return it.value
        }
        return null
    }
}
