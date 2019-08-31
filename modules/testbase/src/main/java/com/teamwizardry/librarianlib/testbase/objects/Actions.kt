package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedConsumer

class Action<T> {
    private var client: SidedConsumer.Client<T>? = null
    private var server: (T.() -> Unit)? = null
    private var common: (T.() -> Unit)? = null

    val exists: Boolean
        get() = client != null || server != null || common != null

    fun run(isClient: Boolean, context: T) {
        common?.also { context.it() }
        if(isClient)
            client?.accept(context)
        else
            server?.also { context.it() }
    }

    @PublishedApi
    internal fun addClientRaw(client: SidedConsumer.Client<T>) {
        this.client = client
    }

    inline fun client(crossinline client: T.() -> Unit) = addClientRaw(SidedConsumer.Client { it.client() })
    fun common(common: T.() -> Unit) {
        this.common = common
    }
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
