package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.profiler.IProfiler
import net.minecraft.profiler.Profiler

inline fun <T> IProfiler.tick(block: () -> T): T {
    this.startTick()
    return try {
        block()
    } finally {
        this.endTick()
    }
}

inline fun <T> IProfiler.section(name: String, block: () -> T): T {
    this.startSection(name)
    return try {
        block()
    } finally {
        this.endSection()
    }
}

inline fun <T> IProfiler.section(noinline name: () -> String, block: () -> T): T {
    this.startSection(name)
    return try {
        block()
    } finally {
        this.endSection()
    }
}
