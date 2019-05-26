package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.forgeevents.PreGameLoopEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor

object MainThreadExecutors {
    init { MinecraftForge.EVENT_BUS.register(this) }

    @SubscribeEvent
    fun preClientGameLoop(e: PreGameLoopEvent.Client) {
        Client.runTasks()
    }

    @SubscribeEvent
    fun preServerGameLoop(e: PreGameLoopEvent.Server) {
        Server.runTasks()
    }

    @SubscribeEvent
    fun preClientTick(e: TickEvent.ClientTickEvent) {
        if(e.phase == TickEvent.Phase.START) {
            ClientTick.runTasks()
        }
    }

    @SubscribeEvent
    fun preServerTick(e: TickEvent.ServerTickEvent) {
        if(e.phase == TickEvent.Phase.START) {
            ServerTick.runTasks()
        }
    }

    object Client: MainLoopExecutor()
    object Server: MainLoopExecutor()
    object ClientTick: MainLoopExecutor()
    object ServerTick: MainLoopExecutor()

}

open class MainLoopExecutor: Executor {
    init { MainThreadExecutors }
    private val queue = ConcurrentLinkedQueue<Runnable>()

    override fun execute(command: Runnable) {
        queue.add(command)
    }

    fun runTasks() {
        whileNonNull({ queue.poll() }) {
            it.run()
        }
    }
}

