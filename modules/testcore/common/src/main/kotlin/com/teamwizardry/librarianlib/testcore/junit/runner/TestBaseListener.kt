package com.teamwizardry.librarianlib.testcore.junit.runner

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan

public open class TestBaseListener: TestExecutionListener {
    public lateinit var roots: List<MutableTestReport>
    public lateinit var reports: Map<TestIdentifier, MutableTestReport>

    public open fun runStarted() {
    }
    public open fun testStarted(report: TestReport) {
    }
    public open fun testFinished(report: TestReport, result: TestResult) {
    }

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        val testReports = mutableListOf<MutableTestReport>()
        fun createReport(parent: TestReport?, testId: TestIdentifier): MutableTestReport {
            val report = MutableTestReport(parent, testId)
            testReports.add(report)

            for (childId in testPlan.getChildren(testId)) {
                report.children.add(createReport(report, childId))
            }

            report.children.sort()
            return report
        }

        this.roots = testPlan.roots.map { createReport(null, it) }.sorted()
        testReports.sort()
        this.reports = testReports.associateBy { it.identifier }
        runStarted()
    }

    override fun executionStarted(testIdentifier: TestIdentifier) {
        reports[testIdentifier]?.let(::testStarted)
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {
        val result = TestResult.Skipped(reason)
        reports[testIdentifier]?.let {
            it.result = result
            testFinished(it, result)
        }
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        val result = TestResult.Finished(testExecutionResult)
        reports[testIdentifier]?.let {
            it.result = result
            testFinished(it, result)
        }
    }
}
