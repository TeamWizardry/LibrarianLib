package com.teamwizardry.librarianlib.testbase.junit

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestIdentifier

interface TestReport {
    val identifier: TestIdentifier
    val result: TestResult
    val reportEntries: List<ReportEntry>
    val parent: TestReport?
    val children: List<TestReport>

    val displayName: String
    val displayPath: String
}

class MutableTestReport(override val identifier: TestIdentifier): TestReport {
    override var result: TestResult = TestResult.Pending
    override val reportEntries: MutableList<ReportEntry> = mutableListOf()
    override var parent: TestReport? = null
    override val children: MutableList<TestReport> = mutableListOf()

    override val displayName: String
        get() = identifier.displayName
    override val displayPath: String
        get() = if(parent?.parent == null) displayName else parent!!.displayPath + "." + displayName
}

sealed class TestResult {
    object Pending: TestResult()
    data class Skipped(val reason: String): TestResult()
    data class Finished(val result: TestExecutionResult): TestResult()
}
