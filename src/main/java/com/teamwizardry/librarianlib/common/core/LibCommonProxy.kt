@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler
import com.teamwizardry.librarianlib.common.util.autoregister.AutoRegisterHandler
import com.teamwizardry.librarianlib.common.util.bitsaving.BitwiseStorageManager
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    open internal fun pre(e: FMLPreInitializationEvent) {
        BitwiseStorageManager
        EasyConfigHandler.init(LibrarianLib.MODID, e.suggestedConfigurationFile, e.asmData)
    }

    open internal fun latePre(e: FMLPreInitializationEvent) {
        AutoRegisterHandler.handle(e)
    }

    open internal fun init(e: FMLInitializationEvent) {
        // NO-OP
    }

    open internal fun lateInit(e: FMLInitializationEvent) {
        // NO-OP
    }

    open internal fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open internal fun latePost(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    open val bookInstance: Book?
        get() = null

}

