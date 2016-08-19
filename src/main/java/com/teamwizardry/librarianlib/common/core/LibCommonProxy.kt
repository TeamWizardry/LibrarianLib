package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.client.book.Book
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    open fun pre(e: FMLPreInitializationEvent) {
        val config = e.suggestedConfigurationFile
        ConfigHandler.initConfig(config)
    }

    open fun init(e: FMLInitializationEvent) {
        //NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    @Suppress("DEPRECATION")
    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    open val bookInstance: Book?
        get() = null

}
