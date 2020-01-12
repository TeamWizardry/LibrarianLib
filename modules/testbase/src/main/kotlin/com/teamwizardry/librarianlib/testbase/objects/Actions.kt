package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedConsumer

class Action<T> {
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
    inline operator fun <T> Action<T>.invoke(crossinline block: T.() -> Unit) {
        this.client(block)
    }
}
object ServerActions {
    operator fun <T> Action<T>.invoke(block: T.() -> Unit) {
        this.server(block)
    }
}
object CommonActions {
    operator fun <T> Action<T>.invoke(block: T.() -> Unit) {
        this.common(block)
    }
}
