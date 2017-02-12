@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.util.lambdainterfs.ClientRunnable
import com.teamwizardry.librarianlib.common.base.ItemBlockClassAnnotationsHandler
import com.teamwizardry.librarianlib.common.container.GuiHandler
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.network.PacketSpamlessMessage
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler
import com.teamwizardry.librarianlib.common.util.autoregister.AutoRegisterHandler
import com.teamwizardry.librarianlib.common.util.saving.SavingFieldCache
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.common.util.unsafeAllowedModIds
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File
import java.io.InputStream

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    lateinit var asmDataTable: ASMDataTable
        private set

    private var mcDataFolder: File = File("/")

    open fun pre(e: FMLPreInitializationEvent) {
        EasyConfigHandler.init()
        mcDataFolder = e.modConfigurationDirectory.parentFile

        if(LibrarianLib.DEV_ENVIRONMENT && unsafeAllowedModIds.isNotEmpty()) {
            LibrarianLog.info(LibrarianLib.MODID + " | Unsafe-allowed mod IDs:")
            unsafeAllowedModIds.forEach { "${" " * LibrarianLib.MODID.length} | $it" }
        }
    }

    open fun latePre(e: FMLPreInitializationEvent) {
        AutoRegisterHandler.handle(e)
        EasyConfigHandler.bootstrap(e.asmData, e.modConfigurationDirectory)
        asmDataTable = e.asmData
        ItemBlockClassAnnotationsHandler
    }

    open fun init(e: FMLInitializationEvent) {
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
    }

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
        val fixPath = path.removePrefix("/")
        val mods = Loader.instance().indexedModList
        val mod = mods[modId] ?: return null
        return mod.mod.javaClass.getResourceAsStream("/assets/$modId/$fixPath")
    }

    open fun runIfClient(clientRunnable: ClientRunnable) {
        // NO-OP
    }

    open fun getClientPlayer(): EntityPlayer = throw UnsupportedOperationException("No client player on server side!")

    open fun sendSpamlessMessage(player: EntityPlayer, msg: ITextComponent, uniqueId: Int) {
        if (player !is EntityPlayerMP) return
        PacketHandler.NETWORK.sendTo(PacketSpamlessMessage(msg, uniqueId), player)
    }

    open fun getDataFolder() = mcDataFolder

}

