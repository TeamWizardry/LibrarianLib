package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

@TestConfigDslMarker
public class UnitTestSuite(moduleConfig: TestModuleConfig, id: Identifier) : TestConfig(moduleConfig, id) {
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

    public companion object {
        public val REGISTRY_KEY: RegistryKey<Registry<UnitTestSuite>> = RegistryKey.ofRegistry<UnitTestSuite>(Identifier.of("testcore:unit_tests"))
    }
}