@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.testing.BlockTest
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler
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
        EasyConfigHandler.init(LibrarianLib.MODID, e.suggestedConfigurationFile, e.asmData)
        if(LibrarianLib.DEV_ENVIRONMENT && LibLibConfig.generateTestBlock) initBlock()
    }

    private fun initBlock() {
        TileMod.registerTile(BlockTest.TETest::class.java, "tetest")
        BlockTest()
    }

    open fun init(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    open val bookInstance: Book?
        get() = null
}

