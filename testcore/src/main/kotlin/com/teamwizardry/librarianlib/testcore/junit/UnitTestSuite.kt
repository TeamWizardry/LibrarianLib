package com.teamwizardry.librarianlib.testcore.junit

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

public class UnitTestSuite {
    private val _tests = mutableListOf<Class<*>>()
    public val tests: List<Class<*>> = _tests.unmodifiableView()

    public var description: String? = null

    public constructor() {}
    public constructor(block: UnitTestSuite.() -> Unit) {
        this.apply(block)
    }

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

    public companion object {
        @JvmField
        public val REGISTRY_ID: Identifier = Identifier("testcore:unit_tests")

        @JvmField
        public val REGISTRY: Registry<UnitTestSuite> = FabricRegistryBuilder
            .createSimple(UnitTestSuite::class.java, REGISTRY_ID)
            .buildAndRegister()
    }
}