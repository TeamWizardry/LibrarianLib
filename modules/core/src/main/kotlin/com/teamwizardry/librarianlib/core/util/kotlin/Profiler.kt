package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.profiler.IProfiler
import net.minecraft.profiler.Profiler
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@UseExperimental(ExperimentalContracts::class)
inline fun <T> IProfiler.tick(block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.startTick()
    return try {
        block()
    } finally {
        this.endTick()
    }
}

@UseExperimental(ExperimentalContracts::class)
inline fun <T> IProfiler.section(name: String, block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.startSection(name)
    return try {
        block()
    } finally {
        this.endSection()
    }
}

@UseExperimental(ExperimentalContracts::class)
inline fun <T> IProfiler.section(noinline name: () -> String, block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.startSection(name)
    return try {
        block()
    } finally {
        this.endSection()
    }
}
