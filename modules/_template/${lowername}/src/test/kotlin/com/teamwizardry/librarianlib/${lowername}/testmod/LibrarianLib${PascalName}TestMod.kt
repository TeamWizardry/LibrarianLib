package com.teamwizardry.librarianlib.${lowername}.testmod

import com.teamwizardry.librarianlib.${lowername}.LibrarianLib${PascalName}Module
import com.teamwizardry.librarianlib.testcore.TestMod
import net.minecraftforge.fml.common.Mod

@Mod("ll-${lowername}-test")
object LibrarianLib${PascalName}TestMod: TestMod(LibrarianLib${PascalName}Module) {
}

internal val logger = LibrarianLib${PascalName}TestMod.makeLogger(null)
