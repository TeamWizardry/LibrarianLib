package com.teamwizardry.librarianlib.features.forgeevents

import net.minecraftforge.fml.common.eventhandler.Event

sealed class PreGameLoopEvent : Event() {
    class Client : PreGameLoopEvent()
    class Server : PreGameLoopEvent()
}
