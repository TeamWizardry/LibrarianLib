package com.teamwizardry.librarianlib.features.animator.internal

import com.teamwizardry.librarianlib.features.animator.Lerper
import jdk.nashorn.internal.objects.Global.Infinity
import java.util.*

data class Edit(val start: Int, val delete: Int, val insert: String)

fun <T> List<T>.splice(start: Int, delete: Int, vararg insert: T): List<T> {
    return this.slice(0..start-1) + listOf(*insert) + this.slice(start-1+delete .. this.size-1)
}
/**
 * Ported version of [this](https://yukkurigames.com/string-lerp/)
 *
 * Created by Joe Wreschnig, ported by Pierce Corcoran
 */
object StringLerper : Lerper<String> {
    val MAX_MATRIX_SIZE = 256 * 256;

    fun costMatrix(source: List<String>, target: List<String>, ins: Int = 1, del: Int = ins, sub: Int = Math.max(ins, del)): Array<Int> {
        /** Calculate the Levenshtein cost matrix for source and target

        If source and target are strings, they cannot contain any
        astral or combining codepoints. Such data must be passed
        as arrays of strings with one element per glyph.

        ins, del, and sub are the costs for insertion, deletion,
        and substition respectively. Their default value is 1. If
        only ins is passed, del and sub are set to the same cost.
        If ins and del are passed, sub is set to the more
        expensive of the two.

        The matrix is returned as a flat typed array.

        Following http://en.wikipedia.org/wiki/Levenshtein_distance
         */
        val m = source.size + 1
        val n = target.size + 1
        val d = Array(m * n) { 0 }

        for (i in 1..m-1)
            d[n * i] = i
        for (j in 1..n-1)
            d[j] = j
        for (j in 1..n-1)
            for (i in 1..m-1)
                if (source[i - 1] == target[j - 1])
                    d[n * i + j] = d[n * (i - 1) + j - 1]
                else
                    d[n * i + j] = Math.min(del + d[n * (i - 1) + j    ],
                            Math.min(ins + d[n *    i    + j - 1],
                                    sub + d[n * (i - 1) + j - 1])
                    )
        return d
    }

    // First, note that deletion is just substition with nothing, so
    // any DEL operation can be replaced by a SUB. Second, the
    // operation code *is* the necessary slice offset for applying the
    // diff.
    val INS = 0
    val SUB = 1

    fun editPath(costs: Array<Int>, target: List<String>): List<Edit> {
        /** Given a cost matrix and a target, create an edit list */
        val path = LinkedList<Edit>();
        var j = target.size
        val n = j + 1
        var i = costs.size / n - 1;
        while (i != 0 || j != 0) {
            val sub = if(i != 0 && j != 0) costs[n * (i - 1) + j - 1].toDouble() else Infinity
            val del = if(i != 0) costs[n * (i - 1) + j].toDouble() else Infinity
            val ins = if(j != 0) costs[n * i + j - 1].toDouble() else Infinity
            if (sub <= ins && sub <= del) {
                if (costs[n * i + j] != costs[n * (i - 1) + j - 1])
                    path.push(Edit(SUB, i - 1, target[j - 1]))
                --i; --j
            } else if (ins <= del) {
                path.push(Edit(INS, i, target[j - 1]))
                --j
            } else {
                path.push(Edit(SUB, i - 1, ""))
                --i
            }
        }
        return path
    }

    fun diff(source: List<String>, target: List<String>, ins: Int, del: Int, sub: Int): List<Edit>{
        /** Create a list of edits to turn source into target

        ins, del, and sub are as passed to costMatrix.
         */
        return editPath(costMatrix(source, target, ins, del, sub), target);
    }

    fun patch(diff: List<Edit>, source: List<String>): List<String> {
        var source = source
        for (edit in diff) {
            source = source.splice(edit.start, edit.delete, edit.insert)
        }
        return source
    }

    fun patch(diff: List<Edit>, source: String): String {
        var source = source
        for (edit in diff) {
            source = source.substring(0, edit.start) + edit.insert + source.substring(edit.start + edit.delete)
        }
        return source;
    }

    // Matches if a string contains combining characters or astral
    // codepoints (technically, the first half surrogate of an astral
    // codepoint).
    var MULTI = "[\u0300-\u036F\u1DC0-\u1DFF\u20D0-\u20FF\uD800-\uDBFF\uFE20-\uFE2F]".toRegex()

    // Match an entire (potentially astral) codepoint and any
    // combining characters following it.
    var GLYPH = "[\\00-\u02FF\u0370-\u1DBF\u1E00-\u20CF\u2100-\uD7FF\uD800-\uFE1F\uFE30-\uFFFF][\u0300-\u036F\u1DC0-\u1DFF\u20D0-\u20FF\uDC00-\uDFFF\uFE20-\uFE2F]*".toRegex();

    fun diffLerpAstral(source: String, target: String, amount: Float): String {
        // This split is not perfect for all languages, but at least
        // it won't create invalid surrogate pairs or orphaned
        // combining characters.
        val sourceGlyphs = GLYPH.findAll(source).map { it.value }.toList()
        val targetGlyphs = GLYPH.findAll(target).map { it.value }.toList()
        val edits = diff(targetGlyphs, sourceGlyphs, 2, 2, 3)
        // The edit path works from the string end, forwards, because
        // that's how Levenshtein edits work. To match LTR reading
        // direction (and the behavior of fastLerp), swap the strings
        // and invert the parameter when editing.
        val partial = edits.subList(0, Math.round((1 - amount) * edits.size))
        return patch(partial, targetGlyphs).joinToString("")
    }

    fun diffLerpBasic(source: String, target: String, amount: Float): String {
        val edits = diff(target.split(""), source.split(""), 2, 2, 3);
        // The edit path works from the string end, forwards, because
        // that's how Levenshtein edits work. To match LTR reading
        // direction (and the behavior of fastLerp), swap the strings
        // and invert the parameter when editing.
        val partial = edits.subList(0, Math.round((1 - amount) * edits.size))
        return patch(partial, target)
    }

    fun diffLerp(source: String, target: String, amount: Float): String {
        /** Interpolate between two strings using edit operations

        This interpolation algorithm applys a partial edit of one
        string into the other. This produces nice looking results,
        but can take a significant amount of time and memory to
        compute the edits. It is not recommended for strings
        longer than a few hundred characters.
         */

        if (MULTI.matches(source) || MULTI.matches(target))
            return diffLerpAstral(source, target, amount)
        else
            return diffLerpBasic(source, target, amount)
    }

    var NUMBERS = "(-?\\d{1,20}(?:\\.\\d{1,20})?)".toRegex()

    fun areNumericTwins(source: String, target: String): Boolean {
        /** Check if a and b differ only in numerals */
        return source.replace(NUMBERS, "0") === target.replace(NUMBERS, "0");
    }

    fun nlerp(source: Float, target: Float, amount: Float): Float {
        return source + (target - source) * amount
    }

    fun numericLerp(source: String, target: String, amount: Float): String {
        /** Interpolate numerically between strings containing numbers

        Numbers may have a leading "-" and a single "." to mark
        the decimal point, but something must be after the ".".
        No other floating point syntax (e.g. 1e6) is supported.
        They are treated as fixed-point values, with the point's
        position itself interpolating.

        For example, numericLerp("0.0", "100".0, 0.123) === "12.3"
        because the "." in "0.0" is interpreted as a decimal
        point. But numericLerp("0.", "100.", 0.123) === "12."
        because the strings are interpreted as integers followed
        by a full stop.

        Calling this functions on strings that differ in more than
        numerals gives undefined results.
         */

        val splits = target.split(NUMBERS)
        val matches = NUMBERS.findAll(source).map { it.value }.toList()

        val targetParts = mutableListOf<String>()
        for(i in splits.indices) {
            val split = targetParts[i]
            val match = matches.getOrNull(i)

            targetParts.add(split)
            match?.also { targetParts.add(it) }
        }
        var i = 1;
        for (sourcePart in matches) {
            val targetPart = targetParts[i]
            val part = nlerp(sourcePart.toFloat(), targetPart.toFloat(), amount)
            val sourcePoint = sourcePart.indexOf(".")
            val targetPoint = targetPart.indexOf(".")
            val point = Math.round(nlerp(
                    if(sourcePoint >= 0) (sourcePart.length - 1) - sourcePoint.toFloat() else 0f,
                    if(targetPoint >= 0) (targetPart.length - 1) - targetPoint.toFloat() else 0f,
                    amount
            ))
            targetParts[i] = "%.${point}f".format(part)
            i += 2
        }
        return targetParts.joinToString("")
    }

    fun fastLerpAstral(source: String, target: String, amount: Float): String {
        val sourceGlyphs = GLYPH.findAll(source).map { it.value }.toList()
        val targetGlyphs = GLYPH.findAll(target).map { it.value }.toList()
        val sourceLength = Math.round(sourceGlyphs.size * amount)
        val targetLength = Math.round(targetGlyphs.size * amount)
        var head = targetGlyphs.subList(0, targetLength)
        val tail = sourceGlyphs.subList(sourceLength, sourceGlyphs.size)
        head += tail
        return head.joinToString("")
    }

    fun fastLerpBasic(source: String, target: String, amount: Float): String {
        val sourceLength = Math.round(source.length * amount)
        val targetLength = Math.round(target.length * amount)
        val head = target.substring(0, targetLength)
        val tail = source.substring(sourceLength, source.length)
        return head + tail
    }

    fun fastLerp(source: String, target: String, amount: Float): String {
        /** Interpolate between two strings based on length

        This interpolation algorithm progressively replaces the
        front of one string with another. This approach is fast
        but does not look good when the strings are similar.
         */

        // Consider fast-pathing this even more for very large
        // strings, e.g. in the megabyte range. These are large enough
        // that it should be fine to just pick a codepoint and search
        // for the nearest glyph start.
        if (MULTI.matches(source) || MULTI.matches(target))
            return fastLerpAstral(source, target, amount);
        else
            return fastLerpBasic(source, target, amount);
    }

    fun _lerp(source: String, target: String, amount: Float): String {
        /** Interpolate between two strings as best as possible

        If the strings are identical aside from numbers in them,
        they are passed through numericLerp.

        If the strings are not numbers and short, they are passed
        through diffLerp.

        Otherwise, they are passed through fastLerp.
         */

        // Fast path for boundary cases.
        if (amount == 0f) return source
        if (amount == 1f) return target

        if (areNumericTwins(source, target))
            return numericLerp(source, target, amount)

        // Numeric lerps should over- and under-shoot when fed numbers
        // outside 0 to 1, but other types cannot.
        if (amount < 0) return source
        if (amount > 1) return target

        val n = source.length * target.length
        if(n != 0 && n < MAX_MATRIX_SIZE)
            return diffLerp(source, target, amount)
        else
            return fastLerp(source, target, amount)
    }

    override fun lerp(from: String, to: String, fraction: Float): String {
        return _lerp(from, to, fraction)
    }
}
