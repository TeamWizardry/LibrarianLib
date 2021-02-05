package com.teamwizardry.librarianlib.core.util.sided

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * A marker denoting a functional interface that should only run on the client
 */
public interface ClientSideFunction

public fun interface ClientMetaSupplier<T: ClientSideFunction> {
    @OnlyIn(Dist.CLIENT)
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
    @OnlyIn(Dist.DEDICATED_SERVER)
    public fun getServerFunction(): T
}
