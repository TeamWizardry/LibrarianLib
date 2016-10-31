package com.teamwizardry.librarianlib.common.util

/**
 * Created by TheCodeWarrior
 */
fun bitsNeededToStoreNValues(valueCount: Int): Int {
    var value = valueCount - 1
    var count = 0
    while (value > 0) {
        count++
        value = value shr 1
    }
    return count
}
