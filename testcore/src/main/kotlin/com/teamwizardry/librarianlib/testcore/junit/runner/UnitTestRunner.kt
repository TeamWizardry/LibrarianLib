package com.teamwizardry.librarianlib.testcore.junit.runner

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.opentest4j.AssertionFailedError
import java.io.PrintWriter
import java.io.StringWriter

public object UnitTestRunner {
    public fun runUnitTests(tests: Collection<Class<*>>): TestSuiteResult {
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(tests.map { selectClass(it) })
            .build()

        val launcher: Launcher = LauncherFactory.create()

        val listener = TestBaseListener()
        launcher.registerTestExecutionListeners(listener)
        launcher.execute(request)

        return listener.createReport()
    }

    public fun format(report: TestReport): String {
        var text = ""
        val result = report.result
        val name = report.displayName
        text += when (result) {
            TestResult.Pending -> "?  $name - Pending"
            is TestResult.Skipped -> "-  $name - Skipped"
            is TestResult.Finished -> when (result.result.status) {
                TestExecutionResult.Status.SUCCESSFUL -> "✅ $name - Succeeded"
                TestExecutionResult.Status.ABORTED -> "⚠️ $name - Aborted"
                TestExecutionResult.Status.FAILED -> {
                    if (result.result.throwable.isPresent) {
                        when (result.result.throwable.get()) {
                            is AssertionFailedError -> "❌ $name - Failed"
                            else -> "‼️ $name - Failed"
                        }
                    } else {
                        "‼️ $name - Failed"
                    }
                }
                null -> "?  $name - null status"
            }
        }
        (result as? TestResult.Finished)?.result?.throwable?.ifPresent { exception ->
            val sw = StringWriter()
            // remove everything above the test runner
            exception.stackTrace = exception.stackTrace.asSequence()
                .takeWhile { it.fileName != "UnitTestRunner.kt" }
                .toList().toTypedArray()
            exception.printStackTrace(PrintWriter(sw))

            text += "\n" + sw.toString().trimEnd().prependIndent("││ ") + "\n╘╧════════════════════"
        }
        if (report.children.isNotEmpty())
            text += "\n" + report.children.joinToString("\n") {
                format(it)
            }.prependIndent("    ")
        return text
    }
}