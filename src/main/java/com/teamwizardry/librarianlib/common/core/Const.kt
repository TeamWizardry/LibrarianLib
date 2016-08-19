package com.teamwizardry.librarianlib.common.core

import net.minecraft.launchwrapper.Launch

object Const {

    val isDev = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

}
