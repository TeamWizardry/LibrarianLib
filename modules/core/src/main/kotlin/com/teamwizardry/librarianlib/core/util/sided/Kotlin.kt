package com.teamwizardry.librarianlib.core.util.sided

/**
 * Runs the passed block on the client side, stripping the block out on the dedicated server side
 */
@JvmSynthetic
public fun <T> clientOnly(block: ClientSupplier<T>): T? {
    return block.get()
}

/**
 * Runs the passed block on the dedicated server side, stripping the block out on the client side
 */
@JvmSynthetic
public fun <T> serverOnly(block: ServerSupplier<T>): T? {
    return block.get()
}

/**
 * Runs the passed blocks on the dedicated server side or the client side, stripping out the other side
 */
@JvmSynthetic
public fun <T> runSided(client: ClientSupplier<T>, server: ServerSupplier<T>): T {
    return object: SidedSupplier<T> {
        override fun getClient(): T {
            return client.getClient()
        }

        override fun getServer(): T {
            return server.getServer()
        }
    }.get()
}
