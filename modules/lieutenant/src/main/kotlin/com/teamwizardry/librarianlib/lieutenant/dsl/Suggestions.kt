package com.teamwizardry.librarianlib.lieutenant.dsl

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

public class ChoiceListSuggestionProvider<S>(private val listProvider: ChoiceListProvider<S>) : SuggestionProvider<S> {
    override fun getSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val remaining = builder.remaining
        listProvider.getChoices(context, builder)
            .filter { it.startsWith(remaining, true) }
            .forEach { builder.suggest(it) }
        return builder.buildFuture()
    }

    public fun interface ChoiceListProvider<S> {
        public fun getChoices(context: CommandContext<S>, builder: SuggestionsBuilder): List<String>
    }
}
