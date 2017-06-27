@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.core.common

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.autoregister.AnnotationMarkersHandler
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IShieldItem
import com.teamwizardry.librarianlib.features.config.EasyConfigHandler
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.saving.SavingFieldCache
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils.generatedFiles
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.features.utilities.unsafeAllowedModIds
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File
import java.io.InputStream
import java.util.*

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    lateinit var asmDataTable: ASMDataTable
        private set

    // Internal methods for initialization

    open fun pre(e: FMLPreInitializationEvent) {
        if (LibrarianLib.DEV_ENVIRONMENT && unsafeAllowedModIds.isNotEmpty()) {
            LibrarianLog.info(LibrarianLib.MODID + " | Unsafe-allowed mod IDs:")
            unsafeAllowedModIds.forEach { "${" " * LibrarianLib.MODID.length} | $it" }
        }
    }

    open fun latePre(e: FMLPreInitializationEvent) {
        AnnotationMarkersHandler.preInit(e)
        EasyConfigHandler.bootstrap(e.asmData, e.modConfigurationDirectory)
        ModCreativeTab.latePre()
        asmDataTable = e.asmData
    }

    open fun init(e: FMLInitializationEvent) {
        RecipeGeneratorHandler.fireRecipes()
        NetworkRegistry.INSTANCE.registerGuiHandler(LibrarianLib, GuiHandler)
    }

    open fun lateInit(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open fun latePost(e: FMLPostInitializationEvent) {
        SavingFieldCache.handleErrors()

        // Late-post because we want to intercept damage at the absolute lowest possible priority
        IShieldItem

        if (generatedFiles.isNotEmpty() && LibrarianLib.DEV_ENVIRONMENT) {
            val home = System.getProperty("user.home")
            generatedFiles = generatedFiles.map { if (it.startsWith(home) && home.isNotBlank()) (if (home.endsWith("/")) "~/" else "~") + it.substring(home.length) else it }.toMutableList()
            val starBegin = "**** THIS IS NOT AN ERROR ****"
            val starPad = (generatedFiles.fold(64) { max, it -> Math.max(max, it.length) } - starBegin.length + 3) * "*"

            print("\n$starBegin$starPad\n")
            print("* ${if (generatedFiles.size == 1) "One file was" else "${generatedFiles.size} files were"} generated automatically by LibLib.\n")
            print("* Restart the ${FMLCommonHandler.instance().side.toString().toLowerCase()} to lock in the changes.\n")
            print("* Generated files:\n")
            for (file in generatedFiles)
                print("** $file\n")
            print("$starBegin$starPad\n\n")
            FMLCommonHandler.instance().handleExit(0)
        }
    }

    // End internal methods

    /**
     * Translates a string. Works server-side or client-side.
     * [s] is the localization key, and [format] is any objects you want to fill into `%s`.
     */
    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    /**
     * Checks if a string has a translation. Works server or client-side.
     */
    open fun canTranslate(s: String): Boolean {
        return I18n.canTranslate(s)
    }

    /**
     * Gets a resource for a given modid.
     * [modId] must be the name of an existing mod server-side. Otherwise, it'll return null.
     * Client-side, it can be any domain provided by resource packs.
     * [path] should be of the format `path/to/file.extension`. Leading slashes will be ignored. Do not use backslashes.
     */
    open fun getResource(modId: String, path: String): InputStream? {
        val fixedModId = modId.toLowerCase(Locale.ROOT)
        val fixedPath = VariantHelper.pathToSnakeCase(path).removePrefix("/")
        val mods = Loader.instance().indexedModList
        val mod = mods[fixedModId] ?: return null
        return mod.mod.javaClass.getResourceAsStream("/assets/$fixedModId/$fixedPath")
    }

    /**
     * See [ClientRunnable].
     */
    open fun runIfClient(clientRunnable: ClientRunnable) {
        // NO-OP
    }

    open fun addReloadHandler(clientRunnable: ClientRunnable) {
        // NO-OP
    }

    /**
     * Used for clientside code rather than proxying.
     */
    open fun getClientPlayer(): EntityPlayer = throw UnsupportedOperationException("No client player on server side!")

    /**
     * Gets the working minecraft data folder. A reasonable guess is made that the CWD is the data folder on serverside.
     */
    open fun getDataFolder() = File("")
}

