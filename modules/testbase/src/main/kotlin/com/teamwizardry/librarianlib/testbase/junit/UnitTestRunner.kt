package com.teamwizardry.librarianlib.testbase.junit

import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.HoverEvent
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.TestExecutionSummary
import org.opentest4j.AssertionFailedError
import java.io.PrintWriter
import java.io.StringWriter

object UnitTestRunner {
    fun runUnitTests(tests: Collection<Class<*>>): TestSuiteResult {
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(tests.map { selectClass(it) })
            .build()

        val launcher: Launcher = LauncherFactory.create()

        val listener = TestBaseListener()
        launcher.registerTestExecutionListeners(listener)
        launcher.execute(request)

        return listener.createReport()
    }

    fun format(report: TestReport): String {
        var text = ""
        val result = report.result
        val name = report.displayName
        text += when(result) {
            TestResult.Pending -> "?  $name - Pending"
            is TestResult.Skipped -> "-  $name - Skipped"
            is TestResult.Finished -> when(result.result.status) {
                TestExecutionResult.Status.SUCCESSFUL -> "✅ $name - Succeeded"
                TestExecutionResult.Status.ABORTED -> "⚠️ $name - Aborted"
                TestExecutionResult.Status.FAILED -> {
                    if(result.result.throwable.isPresent) {
                        when(result.result.throwable.get()) {
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
            exception.printStackTrace(PrintWriter(sw))

            // remove everything after the `runUnitTests` line
            var skipping = false
            val trace = sw.toString().lineSequence().filter { line ->
                var shouldSkip = skipping
                if(line.startsWith("\tat com.teamwizardry.librarianlib.testbase.junit.UnitTestRunner.runUnitTests"))
                    skipping = true
                if(!line.startsWith("\t")) {
                    shouldSkip = false
                    skipping = false
                }
                !shouldSkip
            }.joinToString("\n")

            text += "\n" + trace.trimEnd().prependIndent("││ ") + "\n╘╧════════════════════"
        }
        if(report.children.isNotEmpty())
            text += "\n" + report.children.joinToString("\n") {
                format(it)
            }.prependIndent("    ")
        return text
    }
}