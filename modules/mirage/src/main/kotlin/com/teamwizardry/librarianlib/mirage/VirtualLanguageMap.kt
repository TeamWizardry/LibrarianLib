package com.teamwizardry.librarianlib.mirage

import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

public class VirtualLanguageMap {
    public val mixinBridge: MixinBridge = MixinBridge()
    private val languageKeys = mutableMapOf<String, String>().synchronized()
    private val lock = ReentrantReadWriteLock()

    /**
     * Add a language key.
     */
    public fun add(name: String, value: String) {
        lock.write {
            languageKeys[name] = value
            logger.debug("Added language key $name")
        }
    }

    private inline fun <T> read(callback: (VirtualLanguageMap) -> T): T = lock.read { callback(this) }
    private inline fun <T> write(callback: (VirtualLanguageMap) -> T): T = lock.write { callback(this) }

    public inner class MixinBridge {
        public fun tryTranslateKey(key: String): String? {
            return read { languageKeys[key] }
        }

        public fun keyExists(key: String): Boolean {
            return read { key in languageKeys }
        }
    }

    private companion object {
        private val logger = LibrarianLibMirageModule.makeLogger<VirtualResourceManager>()
    }
}