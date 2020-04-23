package com.teamwizardry.librarianlib

import com.google.gson.Gson

data class ModuleInfo(
    val mainClass: String
) {
    companion object {
        private val gson = Gson()
        fun loadModuleInfo(name: String): ModuleInfo? {
            return ModuleInfo::class.java.getResourceAsStream("/META-INF/ll/$name/module.json")?.readBytes()?.let {
                gson.fromJson(String(it), ModuleInfo::class.java)
            }
        }
    }
}
