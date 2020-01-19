package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedConsumer


class ClientAction<T> {
    private var client: SidedConsumer.Client<T>? = null
    val exists: Boolean
        get() = client != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    fun run(context: T) {
        client?.accept(context)
    }

    fun clear() {
        client = null
    }

    @PublishedApi
    internal fun addClientRaw(client: SidedConsumer.Client<T>) {
        this.client = client
    }

    /**
     * Sets the client callback. This is only called on the logical client. This inlines to a [SidedConsumer.Client]
     * SAM object, so client-only code is safe to call inside it.
     */
    inline operator fun invoke(crossinline client: T.() -> Unit) = addClientRaw(SidedConsumer.Client { it.client() })
}

class ServerAction<T> {
    private var server: (T.() -> Unit)? = null

    val exists: Boolean
        get() = server != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    fun run(context: T) {
        server?.also { context.it() }
    }

    fun clear() {
        server = null
    }

    /**
     * Sets the server callback. This is only called on the logical server.
     */
    operator fun invoke(server: T.() -> Unit) {
        this.server = server
    }
}

class SidedAction<T> {
    private var client: SidedConsumer.Client<T>? = null
    private var server: (T.() -> Unit)? = null
    private var common: (T.() -> Unit)? = null

    val exists: Boolean
        get() = client != null || server != null || common != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    fun run(isClient: Boolean, context: T) {
        common?.also { context.it() }
        if(isClient)
            client?.accept(context)
        else
            server?.also { context.it() }
    }

    fun clear() {
        client = null
        common = null
        server = null
    }

    @PublishedApi
    internal fun addClientRaw(client: SidedConsumer.Client<T>) {
        this.client = client
    }

    /**
     * Sets the client callback. This is only called on the logical client. This inlines to a [SidedConsumer.Client]
     * SAM object, so client-only code is safe to call inside it.
     */
    inline fun client(crossinline client: T.() -> Unit) = addClientRaw(SidedConsumer.Client { it.client() })

    /**
     * Sets the common callback, which is called before both the server and client callbacks.
     */
    fun common(common: T.() -> Unit) {
        this.common = common
    }

    /**
     * Sets the server callback. This is only called on the logical server.
     */
    fun server(server: T.() -> Unit) {
        this.server = server
    }
}

object ClientActions {
    inline operator fun <T> SidedAction<T>.invoke(crossinline block: T.() -> Unit) {
        this.client(block)
    }
}
object ServerActions {
    operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.server(block)
    }
}
object CommonActions {
    operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.common(block)
    }
}
