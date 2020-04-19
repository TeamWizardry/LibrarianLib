package com.teamwizardry.librarianlib

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import dev.thecodewarrior.mirror.Mirror
import net.minecraftforge.fml.ModLoader
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

object LibrarianLib {
    val logger = LogManager.getLogger("LibrarianLib")

    internal val _modules = mutableMapOf<String, LibrarianLibModule?>()
    val modules: Map<String, LibrarianLibModule?> = _modules.unmodifiableView()
}