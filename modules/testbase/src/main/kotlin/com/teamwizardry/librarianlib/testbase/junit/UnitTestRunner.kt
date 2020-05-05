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
            var atEnd = false
            val trace = sw.toString().lineSequence().takeWhile { line ->
                !atEnd.also {
                    atEnd = line.startsWith("\tat com.teamwizardry.librarianlib.testbase.junit.UnitTestRunner.runUnitTests")
                }
            }.joinToString("\n")

            text += "\n" + trace.prependIndent("││ ") + "\n╘╧════════════════════"
        }
        if(report.children.isNotEmpty())
            text += "\n" + report.children.joinToString("\n") {
                format(it)
            }.prependIndent("    ")
        return text
    }

    fun makeTextComponent(report: TestSuiteResult): ITextComponent {
        // [ $ tests found | $ tests passed | $ tests failed ]
        val fullCount = report.reports.asSequence().filter { it.key.isTest }.count()
        val passed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.SUCCESSFUL
        }
        val failed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.FAILED
        }

        val passedStyle = Style().apply {
            color = TextFormatting.GREEN
            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                StringTextComponent("${passed.size} tests passed\n").applyTextStyle(TextFormatting.GREEN)
                    .appendSibling(StringTextComponent(passed.values.joinToString("\n") { it.displayPath }))
            )
        }
        val failedStyle = Style().apply {
            color = TextFormatting.RED
            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                StringTextComponent("${failed.size} tests failed\n").applyTextStyle(TextFormatting.RED)
                    .appendSibling(StringTextComponent(failed.values.joinToString("\n") { it.displayPath }))
            )
        }


        return StringTextComponent("[ $fullCount tests found | ").appendSibling(
            StringTextComponent("${passed.size} tests passed").setStyle(passedStyle)
        ).appendText(" | ")
            .appendSibling(
            StringTextComponent("${failed.size} tests failed").setStyle(failedStyle)
        ).appendText(" ]")
    }

}