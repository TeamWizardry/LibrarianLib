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
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.junit.platform.engine.TestExecutionResult
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

public object UnitTestCommand {
    public fun register() {
        ArgumentTypeRegistry.registerArgumentType(
            Identifier.of("liblib-testcore:unit_test"),
            UnitTestArgument::class.java,
            ConstantArgumentSerializer.of { registryAccess ->
                UnitTestArgument(registryAccess.getWrapperOrThrow(UnitTestSuite.REGISTRY_KEY))
            }
        )

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("unittest").then(
                    CommandManager.argument("test", UnitTestArgument(registryAccess.getWrapperOrThrow(UnitTestSuite.REGISTRY_KEY)))
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
        source.sendFeedback({ Text.literal("Running §5${suiteId}§r tests...") }, true)
        suite.description?.also {
            source.sendFeedback({ Text.literal("§7> ${it}§r") }, true)
        }
        val report = UnitTestRunner.runUnitTests(suite.tests)
        logger.info("Unit tests for ${suiteId}\n" + report.roots.joinToString("\n") { UnitTestRunner.format(it) })
        source.sendFeedback({ makeTextComponent(input, report) }, true)
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
                    Text.literal("${passed.size} tests passed\n").formatted(Formatting.GREEN)
                        .append(Text.literal(passed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val failedStyle = Style.EMPTY
            .withFormatting(Formatting.RED)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal("${failed.size} tests failed\n").formatted(Formatting.RED)
                        .append(Text.literal(failed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val rerunStyle = Style.EMPTY
            .withFormatting(Formatting.BLUE)
            .withUnderline(true)
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, input))

        return Text.literal("[ $fullCount tests found | ")
            .append(Text.literal("${passed.size} tests passed").setStyle(passedStyle))
            .append(" | ")
            .append(Text.literal("${failed.size} tests failed").setStyle(failedStyle))
            .append(" ] ")
            .append(Text.literal("(Rerun)").setStyle(rerunStyle))
    }

    private val logger = TestCore.logManager.makeLogger<UnitTestCommand>()
}

public class UnitTestArgument(private val registryWrapper: RegistryWrapper.Impl<UnitTestSuite>) : ArgumentType<UnitTestSuite> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): UnitTestSuite {
        val identifier = Identifier.fromCommandInput(reader)
        val entry = this.registryWrapper.getOptional(RegistryKey.of(UnitTestSuite.REGISTRY_KEY, identifier)).getOrNull()
        return entry?.value() ?: throw TEST_NOT_FOUND.create(identifier)
    }

    override fun <S> listSuggestions(context: CommandContext<S>, suggestions: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestIdentifiers(registryWrapper.streamKeys().map { it.value }, suggestions)
    }

    public companion object {
        public val TEST_NOT_FOUND: DynamicCommandExceptionType = DynamicCommandExceptionType { function: Any? -> Text.translatable("testcore.unitTestNotFound", function) }

        @Throws(CommandSyntaxException::class)
        public fun getUnitTest(context: CommandContext<ServerCommandSource>, name: String): UnitTestSuite {
            return context.getArgument(name, UnitTestSuite::class.java)
        }
    }
}