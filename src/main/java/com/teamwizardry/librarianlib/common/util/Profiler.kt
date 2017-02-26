package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraft.client.Minecraft

/**
 * Created by TheCodeWarrior
 */
object Profiler {
    inline fun section(name: String, code: () -> Unit) {
        LibrarianLib.PROXY.startProfilerSection(name)
        code()
        LibrarianLib.PROXY.endProfilerSection()
    }
}
