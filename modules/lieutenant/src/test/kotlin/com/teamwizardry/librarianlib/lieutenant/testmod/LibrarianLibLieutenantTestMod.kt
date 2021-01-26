package com.teamwizardry.librarianlib.lieutenant.testmod

import com.teamwizardry.librarianlib.lieutenant.LibrarianLibLieutenantModule
import com.teamwizardry.librarianlib.lieutenant.RegisterClientCommandsEvent
import com.teamwizardry.librarianlib.lieutenant.dsl.command
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod("librarianlib-lieutenant-test")
object LibrarianLibLieutenantTestMod : TestMod(LibrarianLibLieutenantModule) {
    @SubscribeEvent
    public fun registerClientCommands(e: RegisterClientCommandsEvent) {
        e.dispatcher.register(
            command("lieutenant") {
                +"help" {
                    execute {
                        source.logFeedback(StringTextComponent("Test help"))
                    }
                }
                +"foo" {
                    execute {
                        source.logFeedback(StringTextComponent("Foo bar"))
                    }
                }
                +"choices" {
                    +"name".string { name ->
                        suggest(listOf("java", "bedrock"))
                        execute {
                            when (name()) {
                                "java" -> {
                                    source.logFeedback(StringTextComponent("Correct!"))
                                }
                                "bedrock" -> {
                                    source.logFeedback(StringTextComponent("GTFO."))
                                }
                                else -> {
                                    source.sendErrorMessage(StringTextComponent(
                                        "Unrecognized Minecraft edition '${name()}'"
                                    ))
                                }
                            }
                        }
                    }
                }
                +"echo" {
                    +"boolean" {
                        +"value".boolean { value ->
                            execute {
                                source.logFeedback(StringTextComponent("Value: ${value()}"))
                            }
                        }
                    }
                    +"float" {
                        +"value".float { value ->
                            execute {
                                source.logFeedback(StringTextComponent("Value: ${value()}"))
                            }
                        }
                    }
                }
            }
        )
    }

}

internal val logger = LibrarianLibLieutenantTestMod.makeLogger(null)
