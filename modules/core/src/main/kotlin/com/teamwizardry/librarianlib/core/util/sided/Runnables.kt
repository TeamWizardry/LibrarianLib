package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.Consumer

/**
 * A runnable that will run different blocks of code on the client and server.
 */
interface SidedRunnable: Runnable {
    @JvmDefault
    override fun run() {
        when (FMLEnvironment.dist) {
            Dist.CLIENT -> runClient()
            Dist.DEDICATED_SERVER -> runServer()
            null -> inconceivable("No dist")
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun runClient()

    @OnlyIn(Dist.DEDICATED_SERVER)
    fun runServer()

    companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        fun client(runnable: ClientRunnable) {
            runnable.run()
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        fun server(runnable: ServerRunnable) {
            runnable.run()
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        fun sided(clientRunnable: ClientRunnable, serverRunnable: ServerRunnable) {
            object: SidedRunnable {
                override fun runClient() {
                    clientRunnable.runClient()
                }

                override fun runServer() {
                    serverRunnable.runServer()
                }
            }.run()
        }
    }
}

/**
 * A runnable that will run only on the client.
 */
fun interface ClientRunnable: Runnable {
    @JvmDefault
    override fun run() {
        if (FMLEnvironment.dist.isClient) {
            runClient()
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun runClient()
}

/**
 * A runnable that will run only on the server.
 */
fun interface ServerRunnable: Runnable {
    @JvmDefault
    override fun run() {
        if (FMLEnvironment.dist.isDedicatedServer) {
            runServer()
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    fun runServer()
}
