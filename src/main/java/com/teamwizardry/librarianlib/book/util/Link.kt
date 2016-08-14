package com.teamwizardry.librarianlib.book.util

class Link {


    val path: String
    val page: Int


    constructor(str: String) {
        val i = str.lastIndexOf(":")
        var page = 0

        if (i != -1) {
            try {
                page = Integer.parseInt(str.substring(i))
            } catch (e: NumberFormatException) {
                // TODO: logging
            }

        }
        val path = str.substring(0, if (i == -1) str.length else i)

        this.page = page
        this.path = path
    }

    constructor(path: String, page: Int) {
        this.path = path
        this.page = page
    }

}
