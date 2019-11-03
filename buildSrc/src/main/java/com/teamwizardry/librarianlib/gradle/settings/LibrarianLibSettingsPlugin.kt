package com.teamwizardry.librarianlib.gradle.settings

import org.gradle.api.GradleScriptException
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.InvalidPluginException
import org.gradle.api.plugins.UnknownPluginException
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.WeakHashMap

/**
 * The plugin used by root projects that should include liblib modules.
 *
 * This plugin can only be applied to the root project.
 */
class LibrarianLibSettingsPlugin : Plugin<Settings> {
    /**
     * The current instance, referenced here to prevent [weakInstance] being garbage collected prematurely.
     */
    private var _instance: LibrarianLibSettings? = null

    override fun apply(target: Settings) {
        if(_instance != null) {
            throw InvalidPluginException("LibrarianLibSettingsPlugin was already applied")
        }
        val instance = LibrarianLibSettings(target)
        _instance = instance
        weakInstance = WeakReference(instance)
    }

    companion object {
        private var weakInstance: WeakReference<LibrarianLibSettings>? = null
        fun getInstance(settings: Settings): LibrarianLibSettings {
            return weakInstance?.get()?.takeIf { it.settings === settings }
                ?: throw IllegalStateException("LibrarianLibSettingsPlugin has not been applied yet")
        }
    }
}
