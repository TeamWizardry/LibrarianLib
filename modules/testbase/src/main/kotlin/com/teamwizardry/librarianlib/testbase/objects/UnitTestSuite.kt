package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.testbase.junit.TestSuiteResult
import com.teamwizardry.librarianlib.testbase.junit.UnitTestRunner
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.registries.ForgeRegistryEntry

class UnitTestSuite: ForgeRegistryEntry<UnitTestSuite>() {
    private val _tests = mutableListOf<Class<*>>()
    val tests: List<Class<*>> = _tests.unmodifiableView()

    fun addTests(vararg testClasses: Class<*>): UnitTestSuite {
        this._tests.addAll(testClasses)
        return this
    }

    fun addTests(testClasses: List<Class<*>>): UnitTestSuite {
        this._tests.addAll(testClasses)
        return this
    }

    inline fun<reified T> add(): UnitTestSuite {
        this.addTests(T::class.java)
        return this
    }
}