package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import net.minecraft.nbt.*
import net.minecraftforge.common.util.Constants

/**
 * @author WireSegal
 * Created at 8:59 PM on 3/21/17.
 */
private val MATCHER = "(?:(?:(?:\\[\\d+])|(?:[^.\\[\\]]+))(\\.|$|(?=\\[)))+".toRegex()

private val TOKENIZER = "((?:\\[\\d+])|(?:[^.\\[\\]]+))(?=[.\\[]|$)".toRegex()

fun NBTTagCompound.getObject(key: String): NBTBase? {
    if (!MATCHER.matches(key)) return null

    var currentElement: NBTBase = this

    val matched = TOKENIZER.findAll(key)
    for (match in matched) {
        val m = match.groupValues[1]
        when {
            m.startsWith("[") -> {
                val ind = m.removePrefix("[").removeSuffix("]").toInt()
                currentElement = when (currentElement) {
                    is NBTTagList -> {
                        if (currentElement.tagCount() < ind + 1) return null
                        currentElement[ind]
                    }
                    is NBTTagByteArray -> {
                        if (currentElement.byteArray.size < ind + 1) return null
                        NBTTagByte(currentElement.byteArray[ind])
                    }
                    is NBTTagIntArray -> {
                        if (currentElement.intArray.size < ind + 1) return null
                        NBTTagInt(currentElement.intArray[ind])
                    }
                    else -> return null
                }
            }
            currentElement is NBTTagCompound -> {
                if (!currentElement.hasKey(m)) return null
                currentElement = currentElement.getTag(m)
            }
            else -> return null
        }
    }
    return currentElement
}

private val CLASSES = arrayOf(
        NBTTagEnd::class.java,
        NBTTagByte::class.java,
        NBTTagShort::class.java,
        NBTTagInt::class.java,
        NBTTagLong::class.java,
        NBTTagFloat::class.java,
        NBTTagDouble::class.java,
        NBTTagByteArray::class.java,
        NBTTagString::class.java,
        NBTTagList::class.java,
        NBTTagCompound::class.java,
        NBTTagIntArray::class.java
)

fun NBTTagCompound.setObject(key: String, tag: NBTBase): Boolean {
    if (!MATCHER.matches(key)) return false

    var currentElement: NBTBase = this

    val matched = TOKENIZER.findAll(key).toList()
    val max = matched.size - 1
    for ((index, match) in matched.withIndex()) {
        val m = match.groupValues[1]
        val done = index == max
        if (m.startsWith("[")) {
            val ind = m.removePrefix("[").removeSuffix("]").toInt()
            if (currentElement is NBTTagList) {
                if (currentElement.tagCount() < ind + 1 && !done) {
                    val new = if ((currentElement.tagType == 0 || currentElement.tagType == Constants.NBT.TAG_LIST)
                            && matched[index + 1].groupValues[1].startsWith("[")) NBTTagList()
                    else if ((currentElement.tagType == 0 || currentElement.tagType == Constants.NBT.TAG_COMPOUND))
                        NBTTagCompound()
                    else return false
                    currentElement.appendTag(new)
                    currentElement = new
                } else {
                    if (!done) currentElement = currentElement[ind]
                    else {
                        var type = currentElement.tagType
                        if (type == 0) type = Constants.NBT.TAG_DOUBLE
                        val transformedTag = tag.castOrDefault(CLASSES[type])
                        if (ind >= currentElement.tagCount()) currentElement.appendTag(transformedTag)
                        else currentElement.set(ind, transformedTag)
                    }
                }
            } else if (currentElement is NBTTagByteArray) {
                if (currentElement.byteArray.size < ind + 1 || !done || tag !is NBTPrimitive) return false
                currentElement.byteArray[ind] = tag.byte
            } else if (currentElement is NBTTagIntArray) {
                if (currentElement.intArray.size < ind + 1 || !done || tag !is NBTPrimitive) return false
                currentElement.intArray[ind] = tag.int
            } else return false
        } else if (currentElement is NBTTagCompound) {
            if (!currentElement.hasKey(m) && !done) {
                val new = if (matched[index + 1].groupValues[1].startsWith("[")) NBTTagList() else NBTTagCompound()
                currentElement.setTag(m, new)
                currentElement = new
            } else {
                if (!done) currentElement = currentElement.getTag(m)
                else currentElement.setTag(m, tag)
            }
        } else return false
    }
    return true
}
