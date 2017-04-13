package com.teamwizardry.librarianlib.features.base.block

import com.google.common.base.Optional
import net.minecraft.block.properties.PropertyHelper
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * @author WireSegal
 * Created at 4:31 PM on 1/8/17.
 */
open class PropertyString(name: String, open val values: LinkedHashSet<String>) : PropertyHelper<String>(name, String::class.java) {
    constructor(name: String, vararg values: String) : this(name, LinkedHashSet(values.toList()))

    init {
        @Suppress("LeakingThis")
        if (values.isEmpty())
            throw IllegalArgumentException("Values are empty!")
    }

    private val indexToValues by lazy { values.withIndex().associate { it.index to it.value } }
    private val valuesToIndex by lazy { values.withIndex().associate { it.value to it.index } }

    override fun parseValue(value: String): Optional<String> = if (value in values) Optional.of(value) else Optional.absent()
    override fun getName(value: String) = value
    override fun getAllowedValues() = values

    fun getMetaFromName(name: String) = valuesToIndex[name] ?: 0
    fun getNameFromMeta(meta: Int): String = indexToValues[meta] ?: values.first()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PropertyString) return false
        if (!super.equals(other)) return false
        if (values != other.values) return false
        return true
    }

    override fun hashCode() = 31 * super.hashCode() + values.hashCode()
}



