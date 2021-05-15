package com.teamwizardry.librarianlib.testcore.util

public class Action<T> {
    private var callback: (T.() -> Unit)? = null

    public fun run(context: T) {
        callback?.invoke(context)
    }

    public fun clear() {
        callback = null
    }

    public operator fun invoke(callback: T.() -> Unit) {
        this.callback = callback
    }
}

public class ClientAction<T> {
    private var callback: (T.() -> Unit)? = null

    public fun run(context: T) {
        callback?.invoke(context)
    }

    public fun clear() {
        callback = null
    }

    /**
     * Sets the client callback. This is only called on the logical client.
     */
    public operator fun invoke(callback: T.() -> Unit) {
        this.callback = callback
    }
}

public class SidedAction<T> {
    private var client: (T.() -> Unit)? = null
    private var server: (T.() -> Unit)? = null
    private var common: (T.() -> Unit)? = null

    public val exists: Boolean
        get() = client != null || server != null || common != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    public fun run(isClient: Boolean, context: T) {
        common?.invoke(context)
        if(isClient)
            client?.invoke(context)
        else
            server?.invoke(context)
    }

    public fun clear() {
        client = null
        common = null
        server = null
    }

    /**
     * Sets the client callback. This is only called on the logical client.
     */
    public fun client(client: T.() -> Unit) {
        this.client = client
    }

    /**
     * Sets the common callback, which is called before both the server and client callbacks.
     */
    public fun common(common: T.() -> Unit) {
        this.common = common
    }

    /**
     * Sets the server callback. This is only called on the logical server.
     */
    public fun server(server: T.() -> Unit) {
        this.server = server
    }
}

public object ClientActionScope {
    public operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.client(block)
    }
}
public object ServerActionScope {
    public operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.server(block)
    }
}
public object CommonActionScope {
    public operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.common(block)
    }
}
