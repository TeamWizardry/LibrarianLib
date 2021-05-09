package com.teamwizardry.librarianlib.testcore.junit.runner

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan

internal class TestBaseListener: TestExecutionListener {
    private val roots = mutableListOf<MutableTestReport>()
    private val reports = mutableMapOf<TestIdentifier, MutableTestReport>()
    lateinit var testPlan: TestPlan

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        this.testPlan = testPlan
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {
        report(testIdentifier).result = TestResult.Skipped(reason)
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        report(testIdentifier).result = TestResult.Finished(testExecutionResult)
    }

    override fun reportingEntryPublished(testIdentifier: TestIdentifier, entry: ReportEntry) {
        report(testIdentifier).reportEntries.add(entry)
    }

    private fun report(testIdentifier: TestIdentifier): MutableTestReport {
        val existed = testIdentifier in reports
        val report = reports.getOrPut(testIdentifier) { MutableTestReport(testIdentifier) }
        if(!existed) {
            val parent = testPlan.getParent(testIdentifier)
            parent.ifPresent {
                val parentReport = report(it)
                parentReport.children.add(report)
                report.parent = parentReport
            }
            if (!parent.isPresent) {
                roots.add(report)
            }
        }
        return report
    }

    fun createReport(): TestSuiteResult {
        reports.values.forEach { it.children.sort() }
        val suite = TestSuiteResult(roots.sorted(), reports.toList().sortedBy { it.second }.toMap())

        return suite
    }
}
