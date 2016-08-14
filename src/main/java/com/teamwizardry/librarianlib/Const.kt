package com.teamwizardry.librarianlib

import net.minecraft.launchwrapper.Launch

object Const {

    val isDev = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

}
