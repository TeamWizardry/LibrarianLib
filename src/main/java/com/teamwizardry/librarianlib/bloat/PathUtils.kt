package com.teamwizardry.librarianlib.bloat

import java.util.*


object PathUtils {

    val SEP = "/"
    val HERE = "."
    val UP = ".."

    fun resolve(path: String): String {

        val parts = ArrayList<String>()
        parts.addAll(Arrays.asList<String>(*path.split(SEP).dropLastWhile({ it.isEmpty() }).toTypedArray()))

        var i = 0
        while (i < parts.size) {
            if ("" == parts[i] || HERE == parts[i])
                parts.removeAt(i)
            else if (UP == parts[i]) {
                parts.removeAt(i)
                if (i >= 0) {
                    parts.removeAt(i + 1)
                    i--
                }
            }
            i++
        }

        return SEP + parts.joinToString(SEP)
    }

    fun resolve(parent: String, relative: String): String {
        if (relative.startsWith(SEP))
            return resolve(relative)
        return resolve(parent + SEP + relative)
    }

    fun parent(path: String): String {
        var newPath = path
        if (newPath.endsWith(SEP)) {
            newPath = newPath.substring(0, newPath.length - 1)
        }
        var index = newPath.lastIndexOf(SEP)
        if (index == -1)
            index = 0
        return newPath.substring(0, index)
    }

}
