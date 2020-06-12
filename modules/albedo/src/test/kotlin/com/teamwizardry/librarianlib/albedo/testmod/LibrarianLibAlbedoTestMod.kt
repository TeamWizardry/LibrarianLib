package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-albedo-test")
object LibrarianLibAlbedoTestMod: TestMod("albedo", "Albedo", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: Albedo Test")
