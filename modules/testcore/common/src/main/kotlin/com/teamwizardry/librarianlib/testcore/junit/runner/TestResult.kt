package com.teamwizardry.librarianlib.testcore.junit.runner

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestIdentifier

public interface TestReport: Comparable<TestReport> {
    public val identifier: TestIdentifier
    public val result: TestResult
    public val parent: TestReport?
    public val children: List<TestReport>

    public val isRoot: Boolean get() = parent == null
    public val displayName: String
    public val displayPath: List<String>
    public val displayPathString: String
}

public class MutableTestReport(override val parent: TestReport?, override val identifier: TestIdentifier): TestReport {
    override var result: TestResult = TestResult.Pending
    override val children: MutableList<TestReport> = mutableListOf()

    override val displayName: String get() = identifier.displayName
    override val displayPath: List<String> = if (parent == null || parent.isRoot) listOf(displayName) else parent.displayPath + listOf(displayName)
    override val displayPathString: String = displayPath.joinToString(" > ")

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
