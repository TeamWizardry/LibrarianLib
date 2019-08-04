package com.teamwizardry.librarianlib.core

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView

abstract class LibrarianLibModule(val modid: String) {
    init {
        register(modid, this)
    }

    companion object {
        private val _modules = mutableMapOf<String, LibrarianLibModule>()

        val modules: Map<String, LibrarianLibModule> = _modules.unmodifiableView()

        private fun register(modid: String, module: LibrarianLibModule) {
            _modules[modid] = module
        }
    }
}