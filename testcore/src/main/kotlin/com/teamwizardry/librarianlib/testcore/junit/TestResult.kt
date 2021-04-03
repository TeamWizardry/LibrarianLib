package com.teamwizardry.librarianlib.testcore.junit

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestIdentifier

public interface TestReport: Comparable<TestReport> {
    public val identifier: TestIdentifier
    public val result: TestResult
    public val reportEntries: List<ReportEntry>
    public val parent: TestReport?
    public val children: List<TestReport>

    public val displayName: String
    public val displayPath: List<String>
}

public class MutableTestReport(override val identifier: TestIdentifier): TestReport {
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
            ?: (if (parent?.parent == null) listOf(displayName) else parent!!.displayPath + listOf(displayName)).also {
                _displayPath = it
            }

    override fun compareTo(other: TestReport): Int {
        if (displayPath.size < other.displayPath.size)
            return -1
        if (displayPath.size > other.displayPath.size)
            return 1
        for (i in displayPath.indices) {
            val comparison = displayPath[i].compareTo(other.displayPath[i])
            if (comparison != 0)
                return comparison
        }
        return 0
    }
}

public sealed class TestResult {
    public object Pending: TestResult()
    public data class Skipped(val reason: String): TestResult()
    public data class Finished(val result: TestExecutionResult): TestResult()
}
