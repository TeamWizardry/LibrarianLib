package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.Consumer

/**
 * A consumer that will run different blocks of code on the client and server.
 */
public interface SidedConsumer<T>: Consumer<T> {
    override fun accept(t: T) {
        when (FMLEnvironment.dist) {
            Dist.CLIENT -> acceptClient(t)
            Dist.DEDICATED_SERVER -> acceptServer(t)
            null -> inconceivable("No dist")
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun acceptClient(t: T)

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun acceptServer(t: T)

    public companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        public fun <T> client(argument: T, consumer: ClientConsumer<T>) {
            consumer.accept(argument)
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        public fun <T> server(argument: T, consumer: ServerConsumer<T>) {
            consumer.accept(argument)
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        public fun <T> sided(argument: T, clientConsumer: ClientConsumer<T>, serverConsumer: ServerConsumer<T>) {
            object: SidedConsumer<T> {
                override fun acceptClient(t: T) {
                    clientConsumer.acceptClient(t)
                }

                override fun acceptServer(t: T) {
                    serverConsumer.acceptServer(t)
                }
            }.accept(argument)
        }
    }
}

/**
 * A consumer that will run only on the client.
 */
public fun interface ClientConsumer<T>: Consumer<T> {
    override fun accept(t: T) {
        if (FMLEnvironment.dist.isClient) {
            acceptClient(t)
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun acceptClient(t: T)
}

/**
 * A consumer that will run only on the server.
 */
public fun interface ServerConsumer<T>: Consumer<T> {
    override fun accept(t: T) {
        if (FMLEnvironment.dist.isDedicatedServer) {
            acceptServer(t)
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun acceptServer(t: T)
}
