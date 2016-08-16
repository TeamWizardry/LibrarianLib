package com.teamwizardry.librarianlib.util

import java.util.*


object PathUtils {

    fun resolve(path: String): String {

        val parts = ArrayList<String>()
        parts.addAll(Arrays.asList<String>(*path.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()))

        var i = 0
        while (i < parts.size) {
            if ("" == parts[i] || "." == parts[i]) {
                parts.removeAt(i)
                i--
                i++
                continue
            }
            if (".." == parts[i]) {
                parts.removeAt(i)
                i--
                if (i >= 0) {
                    parts.removeAt(i)
                    i--
                }
                i++
                continue
            }
            i++
        }

        return "/" + parts.joinToString("/")
    }

    fun resolve(parent: String, relative: String): String {
        if (relative.startsWith("/"))
            return resolve(relative)
        return resolve(parent + "/" + relative)
    }

    fun parent(path: String): String {
        var path = path
        if (path.endsWith("/")) {
            path = path.substring(0, path.length - 1)
        }
        var index = path.lastIndexOf("/")
        if (index == -1)
            index = 0
        return path.substring(0, index)
    }

}
