package com.teamwizardry.librarianlib.testcore.junit

import com.mojang.brigadier.Command
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.teamwizardry.librarianlib.testcore.TestCore
import com.teamwizardry.librarianlib.testcore.junit.runner.TestResult
import com.teamwizardry.librarianlib.testcore.junit.runner.TestSuiteResult
import com.teamwizardry.librarianlib.testcore.junit.runner.UnitTestRunner
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.junit.platform.engine.TestExecutionResult
import java.util.concurrent.CompletableFuture

public object UnitTestCommand {
    public fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, dedicated ->
            dispatcher.register(
                CommandManager.literal("unittest").then(
                    CommandManager.argument("test", UnitTestArgument())
                        .executes { context ->
                            runTestSuite(context.source, context.input, UnitTestArgument.getUnitTest(context, "test"))
                            Command.SINGLE_SUCCESS
                        }
                )
            )
        }
    }

    private fun runTestSuite(source: ServerCommandSource, input: String, suite: UnitTestSuite) {
        val suiteId = UnitTestSuite.REGISTRY.getId(suite)
        source.sendFeedback(LiteralText("Running §5${suiteId}§r tests..."), true)
        suite.description?.also {
            source.sendFeedback(LiteralText("§7> ${it}§r"), true)
        }
        val report = UnitTestRunner.runUnitTests(suite.tests)
        logger.info("Unit tests for ${suiteId}\n" + report.roots.joinToString("\n") { UnitTestRunner.format(it) })
        source.sendFeedback(makeTextComponent(input, report), true)
    }

    private fun makeTextComponent(input: String, report: TestSuiteResult): Text {
        // [ $ tests found | $ tests passed | $ tests failed ]
        val fullCount = report.reports.asSequence().filter { it.key.isTest }.count()
        val passed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.SUCCESSFUL
        }
        val failed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.FAILED
        }

        val passedStyle = Style.EMPTY
            .withFormatting(Formatting.GREEN)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    LiteralText("${passed.size} tests passed\n").formatted(Formatting.GREEN)
                        .append(LiteralText(passed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val failedStyle = Style.EMPTY
            .withFormatting(Formatting.RED)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    LiteralText("${failed.size} tests failed\n").formatted(Formatting.RED)
                        .append(LiteralText(failed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val rerunStyle = Style.EMPTY
            .withFormatting(Formatting.BLUE)
            .withUnderline(true)
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, input))

        return LiteralText("[ $fullCount tests found | ")
            .append(LiteralText("${passed.size} tests passed").setStyle(passedStyle))
            .append(" | ")
            .append(LiteralText("${failed.size} tests failed").setStyle(failedStyle))
            .append(" ] ")
            .append(LiteralText("(Rerun)").setStyle(rerunStyle))
    }

    private val logger = TestCore.logManager.makeLogger<UnitTestCommand>()
}

public class UnitTestArgument: ArgumentType<UnitTestSuite> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): UnitTestSuite {
        val identifier = Identifier.fromCommandInput(reader)
        return UnitTestSuite.REGISTRY.get(identifier)
            ?: throw TEST_NOT_FOUND.create(identifier)
    }

    override fun <S> listSuggestions(context: CommandContext<S>, suggestions: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestIdentifiers(UnitTestSuite.REGISTRY.ids, suggestions)
    }

    public companion object {
        public val TEST_NOT_FOUND: DynamicCommandExceptionType = DynamicCommandExceptionType { function: Any? -> TranslatableText("testcore.unitTestNotFound", function) }

        @Throws(CommandSyntaxException::class)
        public fun getUnitTest(context: CommandContext<ServerCommandSource>, name: String): UnitTestSuite {
            return context.getArgument(name, UnitTestSuite::class.java)
        }
    }
}