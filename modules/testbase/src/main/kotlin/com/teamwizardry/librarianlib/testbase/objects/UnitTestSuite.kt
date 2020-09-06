package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraftforge.registries.ForgeRegistryEntry

public class UnitTestSuite: ForgeRegistryEntry<UnitTestSuite>() {
    private val _tests = mutableListOf<Class<*>>()
    public val tests: List<Class<*>> = _tests.unmodifiableView()

    public fun addTests(vararg testClasses: Class<*>): UnitTestSuite {
        this._tests.addAll(testClasses)
        return this
    }

    public fun addTests(testClasses: List<Class<*>>): UnitTestSuite {
        this._tests.addAll(testClasses)
        return this
    }

    public inline fun<reified T> add(): UnitTestSuite {
        this.addTests(T::class.java)
        return this
    }
}