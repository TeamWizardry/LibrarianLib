package com.teamwizardry.librarianlib.testcore.objects

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLEnvironment

public fun interface ClientCallback<T> {
    public fun call(t: T) {
        if (FMLEnvironment.dist.isClient) {
            callClient(t)
        }
    }

    @OnlyIn(Dist.CLIENT)
    public fun callClient(t: T)
}

public class ClientAction<T> {
    private var client: ClientCallback<T>? = null
    public val exists: Boolean
        get() = client != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    public fun run(context: T) {
        client?.call(context)
    }

    public fun clear() {
        client = null
    }

    @PublishedApi
    internal fun addClientRaw(client: ClientCallback<T>) {
        this.client = client
    }

    /**
     * Sets the client callback. This is only called on the logical client. This inlines to a [ClientCallback]
     * SAM object, so client-only code is safe to call inside it.
     */
    public inline operator fun invoke(crossinline client: T.() -> Unit) {
        addClientRaw { it.client() }
    }
}

public class SidedAction<T> {
    private var client: ClientCallback<T>? = null
    private var server: (T.() -> Unit)? = null
    private var common: (T.() -> Unit)? = null

    public val exists: Boolean
        get() = client != null || server != null || common != null

    /**
     * Run the common and client or server callbacks, depending on [isClient]
     */
    public fun run(isClient: Boolean, context: T) {
        common?.also { context.it() }
        if(isClient)
            client?.call(context)
        else
            server?.also { context.it() }
    }

    public fun clear() {
        client = null
        common = null
        server = null
    }

    /**
     * Sets the client callback. This is only called on the logical client.
     */
    public fun client(client: ClientCallback<T>) {
        this.client = client
    }

    /**
     * Sets the client callback. This is only called on the logical client.
     */
    public inline fun client(crossinline client: T.() -> Unit) {
        this.client(ClientCallback { it.client() })
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

public object ClientActions {
    public inline operator fun <T> SidedAction<T>.invoke(crossinline block: T.() -> Unit) {
        this.client(block)
    }
}
public object ServerActions {
    public operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.server(block)
    }
}
public object CommonActions {
    public operator fun <T> SidedAction<T>.invoke(block: T.() -> Unit) {
        this.common(block)
    }
}
