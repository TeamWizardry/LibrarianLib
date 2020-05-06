package com.teamwizardry.librarianlib.testbase.junit

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestIdentifier
import kotlin.math.max

interface TestReport: Comparable<TestReport> {
    val identifier: TestIdentifier
    val result: TestResult
    val reportEntries: List<ReportEntry>
    val parent: TestReport?
    val children: List<TestReport>

    val displayName: String
    val displayPath: List<String>
}

class MutableTestReport(override val identifier: TestIdentifier): TestReport {
    override var result: TestResult = TestResult.Pending
    override val reportEntries: MutableList<ReportEntry> = mutableListOf()
    override var parent: TestReport? = null
    set(value) {
        field = value
        _displayPath = null
    }
    override val children: MutableList<TestReport> = mutableListOf()

    override val displayName: String
        get() = identifier.displayName
    private var _displayPath: List<String>? = null
    override val displayPath: List<String>
        get() = _displayPath
            ?: (if(parent?.parent == null) listOf(displayName) else parent!!.displayPath + listOf(displayName)).also {
                _displayPath = it
            }

    override fun compareTo(other: TestReport): Int {
        if(displayPath.size < other.displayPath.size)
            return -1
        if(displayPath.size > other.displayPath.size)
            return 1
        for(i in displayPath.indices) {
            val comparison = displayPath[i].compareTo(other.displayPath[i])
            if(comparison != 0)
                return comparison
        }
        return 0
    }
}

sealed class TestResult {
    object Pending: TestResult()
    data class Skipped(val reason: String): TestResult()
    data class Finished(val result: TestExecutionResult): TestResult()
}
