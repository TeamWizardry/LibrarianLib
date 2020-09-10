package com.teamwizardry.librarianlib.courier.testmod

import com.teamwizardry.librarianlib.courier.LibrarianLibCourierModule
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-courier-test")
object LibrarianLibCourierTestMod: TestMod(LibrarianLibCourierModule) {
}

internal val logger = LibrarianLibCourierTestMod.makeLogger(null)
