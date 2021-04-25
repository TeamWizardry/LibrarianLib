package com.teamwizardry.librarianlib.core.util.sided

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

//todo: bad?
/**
 * A marker denoting a functional interface that should only run on the client
 */
public interface ClientSideFunction

public fun interface ClientMetaSupplier<T: ClientSideFunction> {
    @Environment(EnvType.CLIENT)
    public fun getClientFunction(): T
}

/**
 * A marker denoting a functional interface that should only run on a dedicated server
 */
public interface ServerSideFunction

/**
 *
 */
public fun interface ServerMetaSupplier<T: ServerSideFunction> {
    @Environment(EnvType.SERVER)
    public fun getServerFunction(): T
}
