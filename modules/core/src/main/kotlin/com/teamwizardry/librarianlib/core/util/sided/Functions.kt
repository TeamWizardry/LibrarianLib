package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.Function

/**
 * A function that will run different blocks of code on the client and server.
 */
public interface SidedFunction<T, R>: Function<T, R> {
    override fun apply(t: T): R {
        return when (FMLEnvironment.dist) {
            Dist.CLIENT -> applyClient(t)
            Dist.DEDICATED_SERVER -> applyServer(t)
            null -> inconceivable("No dist")
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun applyClient(t: T): R

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun applyServer(t: T): R

    public companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        public fun <T, R> client(argument: T, function: ClientFunction<T, R>): R? {
            return function.apply(argument)
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        public fun <T, R> server(argument: T, function: ServerFunction<T, R>): R? {
            return function.apply(argument)
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        public fun <T, R> sided(argument: T, clientFunction: ClientFunction<T, R>, serverFunction: ServerFunction<T, R>) {
            object: SidedFunction<T, R> {
                override fun applyClient(t: T): R = clientFunction.applyClient(t)
                override fun applyServer(t: T): R = serverFunction.applyServer(t)
            }.apply(argument)
        }
    }
}

/**
 * A function that will run only on the client.
 */
public fun interface ClientFunction<T, R>: Function<T, R?> {
    override fun apply(t: T): R? {
        return if (FMLEnvironment.dist.isClient) {
            applyClient(t)
        } else {
            null
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun applyClient(t: T): R
}

/**
 * A function that will run only on the server.
 */
public fun interface ServerFunction<T, R>: Function<T, R?> {
    override fun apply(t: T): R? {
        return if (FMLEnvironment.dist.isDedicatedServer) {
            applyServer(t)
        } else {
            null
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun applyServer(t: T): R
}
