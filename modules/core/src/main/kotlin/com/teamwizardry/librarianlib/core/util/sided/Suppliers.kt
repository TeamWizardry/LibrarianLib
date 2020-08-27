package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * A supplier that will get different blocks of code on the client and server.
 */
interface SidedSupplier<T>: Supplier<T> {
    override fun get(): T {
        return when (FMLEnvironment.dist) {
            Dist.CLIENT -> getClient()
            Dist.DEDICATED_SERVER -> getServer()
            null -> inconceivable("No dist")
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun getClient(): T

    @OnlyIn(Dist.DEDICATED_SERVER)
    fun getServer(): T

    companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        fun <T> client(supplier: ClientSupplier<T>): T? {
            return supplier.get()
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        fun <T> server(supplier: ServerSupplier<T>): T? {
            return supplier.get()
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        fun <T> sided(clientSupplier: ClientSupplier<T>, serverSupplier: ServerSupplier<T>): T {
            return object: SidedSupplier<T> {
                override fun getClient(): T {
                    return clientSupplier.getClient()
                }

                override fun getServer(): T {
                    return serverSupplier.getServer()
                }
            }.get()
        }
    }
}

/**
 * A supplier that will get only on the client.
 */
fun interface ClientSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FMLEnvironment.dist.isClient) {
            getClient()
        } else {
            null
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun getClient(): T
}

/**
 * A supplier that will get only on the server.
 */
fun interface ServerSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FMLEnvironment.dist.isDedicatedServer) {
            getServer()
        } else {
            null
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    fun getServer(): T
}
