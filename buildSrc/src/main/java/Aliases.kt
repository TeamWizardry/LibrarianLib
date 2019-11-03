import com.teamwizardry.librarianlib.gradle.settings.LibrarianLibSettings
import com.teamwizardry.librarianlib.gradle.settings.LibrarianLibSettingsPlugin
import org.gradle.api.initialization.Settings

typealias LibrarianLibModulePlugin = com.teamwizardry.librarianlib.gradle.module.LibrarianLibModulePlugin
typealias LibrarianLibModule = com.teamwizardry.librarianlib.gradle.module.LibrarianLibModule
typealias LibrarianLibDevPlugin = com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPlugin
typealias LibrarianLibDev = com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDev


inline fun Settings.librarianlib(config: LibrarianLibSettings.() -> Unit)
    = LibrarianLibSettingsPlugin.getInstance(this.settings).config()