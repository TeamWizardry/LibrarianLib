package com.teamwizardry.librarianlib.testbase.objects

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.teamwizardry.librarianlib.testbase.junit.TestResult
import com.teamwizardry.librarianlib.testbase.junit.TestSuiteResult
import com.teamwizardry.librarianlib.testbase.junit.UnitTestRunner
import com.teamwizardry.librarianlib.testbase.logger
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.junit.platform.engine.TestExecutionResult
import java.util.concurrent.CompletableFuture
import java.util.function.Function

public object UnitTestCommand {
    public fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
            Commands.literal("unittest").then(
                Commands.argument("test", UnitTestArgument())
                    .executes { context ->
                        runTestSuite(context.source, context.input, UnitTestArgument.getUnitTest(context, "test"))
                        Command.SINGLE_SUCCESS
                    }
            )

        )
    }

    private fun runTestSuite(source: CommandSource, input: String, suite: UnitTestSuite) {
        source.sendFeedback(StringTextComponent("Running §5${suite.registryName}§r tests..."), true)
        val report = UnitTestRunner.runUnitTests(suite.tests)
        logger.info("Unit tests for ${suite.registryName}\n" + report.roots.joinToString("\n") { UnitTestRunner.format(it) })
        source.sendFeedback(makeTextComponent(input, report), true)
    }

    private fun makeTextComponent(input: String, report: TestSuiteResult): ITextComponent {
        // [ $ tests found | $ tests passed | $ tests failed ]
        val fullCount = report.reports.asSequence().filter { it.key.isTest }.count()
        val passed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.SUCCESSFUL
        }
        val failed = report.reports.filter { (key, value) ->
            key.isTest && (value.result as? TestResult.Finished)?.result?.status == TestExecutionResult.Status.FAILED
        }

        val passedStyle = Style.EMPTY
            .applyFormatting(TextFormatting.GREEN)
            .setHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    StringTextComponent("${passed.size} tests passed\n").mergeStyle(TextFormatting.GREEN)
                        .append(StringTextComponent(passed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val failedStyle = Style.EMPTY
            .applyFormatting(TextFormatting.RED)
            .setHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    StringTextComponent("${failed.size} tests failed\n").mergeStyle(TextFormatting.RED)
                        .append(StringTextComponent(failed.values.joinToString("\n") { it.displayPath.joinToString(" > ") }))
                )
            )
        val rerunStyle = Style.EMPTY
            .applyFormatting(TextFormatting.BLUE)
            .setUnderlined(true)
            .setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, input))

        return StringTextComponent("[ $fullCount tests found | ").append(
                StringTextComponent("${passed.size} tests passed").setStyle(passedStyle)
            ).appendString(" | ")
            .append(
                StringTextComponent("${failed.size} tests failed").setStyle(failedStyle)
            ).appendString(" ] ")
            .append(
                StringTextComponent("(Rerun)").setStyle(rerunStyle)
            )
    }
}

public class UnitTestArgument: ArgumentType<UnitTestSuite> {
    private val registry = GameRegistry.findRegistry(UnitTestSuite::class.java)

    @Throws(CommandSyntaxException::class)
    override fun parse(p_parse_1_: StringReader): UnitTestSuite {
        val resourcelocation = ResourceLocation.read(p_parse_1_)
        return registry.getValue(resourcelocation)
            ?: throw TEST_NOT_FOUND.create(resourcelocation)
    }

    override fun <S> listSuggestions(p_listSuggestions_1_: CommandContext<S>, p_listSuggestions_2_: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return ISuggestionProvider.suggestIterable(registry.keys, p_listSuggestions_2_)
    }

    public companion object {
        public val TEST_NOT_FOUND: DynamicCommandExceptionType = DynamicCommandExceptionType { p_208663_0_: Any? -> TranslationTextComponent("librarianlib-testbase.unitTestNotFound", p_208663_0_) }

        @Throws(CommandSyntaxException::class)
        public fun getUnitTest(context: CommandContext<CommandSource>, name: String): UnitTestSuite {
            return context.getArgument(name, UnitTestSuite::class.java)
        }
    }
}