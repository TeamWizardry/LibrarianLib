package com.teamwizardry.librarianlib.common.core

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.nio.file.Paths

object DevOwnershipTest {

    val OWNED: List<String>

    val BASE_PATHS = arrayOf(
            "src/main/java",
            "src/main/kotlin",
            "src/test/java",
            "src/test/kotlin"
    )

    val ABS_BASE = Paths.get(Minecraft.getMinecraft().mcDataDir.absolutePath).parent.parent.toString()

    init {
        val modids = mutableListOf<String>()
        val mods = Loader.instance().activeModList
        mainLoop@ for (mod in mods) {
            val packages = mod.ownedPackages
            for (pack in packages) for (base in BASE_PATHS) {
                val path = "$ABS_BASE/$base/" + pack.replace(".", "/")
                val file = File(path)
                if (file.exists() && file.isDirectory) {
                    modids.add(mod.modId)
                    continue@mainLoop
                }
            }
        }
        OWNED = modids
    }
}
