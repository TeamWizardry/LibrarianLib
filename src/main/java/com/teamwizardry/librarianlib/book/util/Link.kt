package com.teamwizardry.librarianlib.book.util

data class Link(val path:String, val tag:String)

object LinkParser {
    fun parse(link:String) : Link {
        var tag = ""
        var path = link

        val hashLoc = link.lastIndexOf("#")

        if (hashLoc != -1) {
            tag = link.substring(hashLoc)
            path = link.substring(0, hashLoc)
        }

        return Link(path, tag)
    }

}
