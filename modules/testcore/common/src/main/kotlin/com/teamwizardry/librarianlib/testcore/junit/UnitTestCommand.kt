package com.teamwizardry.librarianlib.testcore.junit

import com.mojang.brigadier.Command
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.teamwizardry.librarianlib.testcore.TestCoreMod
import com.teamwizardry.librarianlib.testcore.content.UnitTestSuite
import com.teamwizardry.librarianlib.testcore.junit.runner.TestBaseListener
import com.teamwizardry.librarianlib.testcore.junit.runner.TestReport
import com.teamwizardry.librarianlib.testcore.junit.runner.TestResult
import com.teamwizardry.librarianlib.testcore.junit.runner.UnitTestRunner
import com.teamwizardry.librarianlib.testcore.platform.TestCoreCommonPlatform
import dev.architectury.event.events.common.CommandRegistrationEvent
import net.minecraft.command.CommandRegistryAccess
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
        TestCoreMod.argumentTypeRegistrar.register(UnitTestArgument.ARGUMENT_TYPE_ID) {
            UnitTestArgument.ARGUMENT_SERIALIZER
        }
        TestCoreCommonPlatform.instance.registerArgumentType(
            UnitTestArgument.ARGUMENT_TYPE_ID,
            UnitTestArgument::class.java,
            UnitTestArgument.ARGUMENT_SERIALIZER
        )
        CommandRegistrationEvent.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("unittest").then(
                    CommandManager.argument("test", UnitTestArgument.create(registryAccess))
                        .executes { context ->
                            runTestSuite(context.source, context.input, UnitTestArgument.getUnitTest(context, "test"))
                            Command.SINGLE_SUCCESS
                        }
                )
            )
        }
    }

    private fun runTestSuite(source: ServerCommandSource, input: String, suite: UnitTestSuite) {
        source.sendFeedback({ Text.literal("Running §5${suite.id}§r tests...") }, false)
        suite.description?.also {
            source.sendFeedback({ Text.literal("§7> ${it}§r") }, false)
        }

        val listener = object : TestBaseListener() {
            var maxPathWidth = 0
            var digitsWidth = 0

            var totalCount = 0
            var finishedCount = 0
            var skipCount = 0
            var successCount = 0
            var failedCount = 0

            override fun runStarted() {
                totalCount = reports.count { it.key.isTest }
                maxPathWidth = reports.maxOf { it.value.displayPathString.length }
                digitsWidth = "$totalCount".length
            }

            private fun padCount(count: Int) = "$count".padStart(digitsWidth)

            override fun testStarted(report: TestReport) {
                val statusText =
                    Text.literal("[ ${padCount(finishedCount + 1)} / ${padCount(totalCount)} ] ")
                        .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
                        .append(Text.literal(report.displayPathString).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE)))
                source.player?.sendMessageToClient(statusText, true)
            }

            override fun testFinished(report: TestReport, result: TestResult) {
                if (!report.identifier.isTest) return
                finishedCount++
                when {
                    result is TestResult.Skipped -> skipCount++
                    result is TestResult.Finished && result.result.status == TestExecutionResult.Status.SUCCESSFUL -> successCount++
                    result is TestResult.Finished && result.result.status == TestExecutionResult.Status.FAILED -> failedCount++
                }
            }
        }

        UnitTestRunner.runUnitTests(suite.tests, listener)

        logger.info("Unit tests for ${suite.id}\n" + listener.roots.joinToString("\n") { UnitTestRunner.format(it) })
        source.player?.sendMessageToClient(Text.literal(" "), true)
        source.sendFeedback({ makeTextComponent(input, listener) }, false)
    }

    private fun makeTextComponent(input: String, listener: TestBaseListener): Text {
        // [ $ tests found | $ tests passed | $ tests failed ]
        val fullCount = listener.reports.count { it.key.isTest }
        val passed = listener.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.SUCCESSFUL
        }
        val failed = listener.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.FAILED
        }
        val skipped = listener.reports.filter { (key, value) ->
            key.isTest && value.result is TestResult.Skipped
        }

        val passedStyle = Style.EMPTY
            .withFormatting(Formatting.GREEN)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal("${passed.size} tests passed\n").formatted(Formatting.GREEN)
                        .append(Text.literal(passed.values.joinToString("\n") { it.displayPathString }))
                )
            )
        val failedStyle = Style.EMPTY
            .withFormatting(Formatting.RED)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal("${failed.size} tests failed\n").formatted(Formatting.RED)
                        .append(Text.literal(failed.values.joinToString("\n") { it.displayPathString }))
                )
            )
        val skippedStyle = Style.EMPTY
            .withFormatting(Formatting.GRAY)
            .withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal("${skipped.size} tests skipped\n").formatted(Formatting.GRAY)
                        .append(Text.literal(skipped.values.joinToString("\n") { it.displayPathString }))
                )
            )
        val rerunStyle = Style.EMPTY
            .withFormatting(Formatting.BLUE)
            .withUnderline(true)
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$input"))

        return Text.literal("[ $fullCount tests found | ")
            .append(Text.literal("${passed.size} tests passed").setStyle(passedStyle))
            .append(" | ")
            .append(Text.literal("${failed.size} tests failed").setStyle(failedStyle))
            .append(" ] ")
            .append(Text.literal("(Rerun)").setStyle(rerunStyle))
    }

    private val logger = TestCoreMod.logManager.makeLogger<UnitTestCommand>()
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

        public val ARGUMENT_TYPE_ID: Identifier = Identifier.of("liblib_testcore:unit_test")
        public val ARGUMENT_SERIALIZER: ConstantArgumentSerializer<UnitTestArgument> =
            ConstantArgumentSerializer.of(::create)

        public fun create(registryAccess: CommandRegistryAccess): UnitTestArgument {
            return UnitTestArgument(registryAccess.getWrapperOrThrow(UnitTestSuite.REGISTRY_KEY))
        }

        @Throws(CommandSyntaxException::class)
        public fun getUnitTest(context: CommandContext<ServerCommandSource>, name: String): UnitTestSuite {
            return context.getArgument(name, UnitTestSuite::class.java)
        }
    }
}