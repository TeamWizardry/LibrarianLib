package com.teamwizardry.librarianlib.features.kotlin

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val clientDispatcher = MainThreadExecutors.Client.asCoroutineDispatcher()
private val serverDispatcher = MainThreadExecutors.Server.asCoroutineDispatcher()
private val clientTickDispatcher = MainThreadExecutors.ClientTick.asCoroutineDispatcher()
private val serverTickDispatcher = MainThreadExecutors.ServerTick.asCoroutineDispatcher()

val Dispatchers.Client: CoroutineDispatcher get() = clientDispatcher
val Dispatchers.Server: CoroutineDispatcher get() = serverDispatcher
val Dispatchers.ClientTick: CoroutineDispatcher get() = clientTickDispatcher
val Dispatchers.ServerTick: CoroutineDispatcher get() = serverTickDispatcher

object ClientScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Client
}

object ClientTickScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.ClientTick
}

object ServerScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Server
}

object ServerTickScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.ServerTick
}
