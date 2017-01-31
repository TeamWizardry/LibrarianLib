package com.teamwizardry.librarianlib.test.command

import com.teamwizardry.librarianlib.common.util.CommandBuilder
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by Elad on 1/31/2017.
 */
object CommandEntryPoint : TestEntryPoint {
    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }

    override fun preInit(event: FMLPreInitializationEvent) {
        CommandBuilder("liblibtest").setExecuter { minecraftServer, iCommandSender, strings -> iCommandSender.sendMessage(TextComponentString("Hi")) }.setPermissionChecker { minecraftServer, iCommandSender -> true }.register()
    }
}