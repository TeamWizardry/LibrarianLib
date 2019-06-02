package com.teamwizardry.librarianlib.features.facade.provided.book.search

import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.Entry
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author WireSegal
 * Created at 9:05 AM on 2/24/18.
 */
@SideOnly(Side.CLIENT)
class TFIDFSearch(private val book: IBookGui) : ISearchAlgorithm {

    override fun search(input: String): List<ISearchAlgorithm.Result>? {
        val player = Minecraft.getMinecraft().player

        val query = input.replace("'", "").toLowerCase(Locale.ROOT)
        val keywords = query.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val unfilteredTfidfResults = ArrayList<FrequencySearchResult>()
        val matchCountSearchResults = ArrayList<MatchCountSearchResult>()

        val contentCache = book.cachedSearchContent

        val nbOfDocuments = contentCache.size
        for (cachedEntry in contentCache.keys)
            if (cachedEntry.isUnlocked(player)) {
                val cachedDocument = contentCache.getOrDefault(cachedEntry, "")
                        .toLowerCase(Locale.ROOT)
                        .replace("'", "")

                val words = cachedDocument.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                val mostRepeatedWord = words.toSet()
                        .associate { it to words.count { word -> word == it } }
                        .maxBy { it.value }

                if (mostRepeatedWord != null) {
                    var documentTfidf = 0.0
                    for (keyword in keywords) {
                        val pattern = "\\b" + keyword.replace("[\\\\.+*?^$\\[\\](){}/'#:!=|]".toRegex()) {
                            "\\" + it.value
                        }

                        val keywordOccurance = pattern.toRegex().findAll(cachedDocument).count() - 1
                        val termFrequency = 0.5 + 0.5 * keywordOccurance / mostRepeatedWord.value

                        var keywordDocumentOccurance = contentCache.keys
                                .filter { it.isUnlocked(player) }
                                .map { contentCache.getOrDefault(it, "").toLowerCase(Locale.ROOT) }
                                .count { it.contains(keyword) }
                        keywordDocumentOccurance = if (keywordDocumentOccurance == 0) keywordDocumentOccurance + 1 else keywordDocumentOccurance

                        val inverseDocumentFrequency = Math.log((nbOfDocuments / keywordDocumentOccurance).toDouble())

                        val keywordTfidf = termFrequency * inverseDocumentFrequency

                        documentTfidf += keywordTfidf
                    }

                    unfilteredTfidfResults.add(FrequencySearchResult(cachedEntry, documentTfidf))
                }
            }

        val filteredTfidfResults = ArrayList<FrequencySearchResult>()

        var largestTFIDF = 0.0
        var smallestTFIDF = Integer.MAX_VALUE.toDouble()
        for (resultItem2 in unfilteredTfidfResults) {
            largestTFIDF = if (resultItem2.frequency > largestTFIDF) resultItem2.frequency else largestTFIDF
            smallestTFIDF = if (resultItem2.frequency < smallestTFIDF) resultItem2.frequency else smallestTFIDF
        }

        for (resultItem in unfilteredTfidfResults) {
            val matchPercentage = Math.round((resultItem.frequency - smallestTFIDF) / (largestTFIDF - smallestTFIDF) * 100).toDouble()
            if (matchPercentage < 5 || java.lang.Double.isNaN(matchPercentage)) continue

            filteredTfidfResults.add(resultItem)
        }

        if (!filteredTfidfResults.isEmpty())
            return filteredTfidfResults
        else {
            for (cachedComponent in contentCache.keys)
                if (cachedComponent.isUnlocked(player)) {
                    val cachedDocument = contentCache.getOrDefault(cachedComponent, "")
                            .toLowerCase(Locale.ROOT)
                            .replace("'", "")

                    val mostMatches = keywords
                            .map { StringUtils.countMatches(cachedDocument, it) }
                            .sum()

                    if (mostMatches > 0)
                        matchCountSearchResults.add(MatchCountSearchResult(cachedComponent, mostMatches))
                }

            return if (!matchCountSearchResults.isEmpty()) matchCountSearchResults else null
        }
    }

    class FrequencySearchResult(override val entry: Entry, override val frequency: Double) : ISearchAlgorithm.Result {
        override val isSpecificResult: Boolean
            get() = true
    }

    class MatchCountSearchResult(override val entry: Entry, private val nbOfMatches: Int) : ISearchAlgorithm.Result {
        override val isSpecificResult: Boolean
            get() = false

        override val frequency: Double
            get() = nbOfMatches.toDouble()
    }
}
