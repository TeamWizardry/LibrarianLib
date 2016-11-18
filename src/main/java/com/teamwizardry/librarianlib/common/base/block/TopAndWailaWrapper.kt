package com.teamwizardry.librarianlib.common.base.block

/**
 * Created by Elad on 11/18/2016.
 * yo dawg, i hear you like wrappers so i put some more wrappers in your wrapper
 */
class TopAndWailaWrapper(var list: MutableList<String> = mutableListOf()) {

    fun addString(vararg string: String) {
        for(str in string) list.add("$str")
    }
}