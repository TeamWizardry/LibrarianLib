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
public interface SidedSupplier<T>: Supplier<T> {
    override fun get(): T {
        return when (FMLEnvironment.dist) {
            Dist.CLIENT -> getClient()
            Dist.DEDICATED_SERVER -> getServer()
            null -> inconceivable("No dist")
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun getClient(): T

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun getServer(): T
}

/**
 * A supplier that will get only on the client.
 */
public fun interface ClientSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FMLEnvironment.dist.isClient) {
            getClient()
        } else {
            null
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun getClient(): T
}

/**
 * A supplier that will get only on the server.
 */
public fun interface ServerSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FMLEnvironment.dist.isDedicatedServer) {
            getServer()
        } else {
            null
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun getServer(): T
}
