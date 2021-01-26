package com.teamwizardry.librarianlib.lieutenant

import com.teamwizardry.librarianlib.LibrarianLibModule

internal object LibrarianLibLieutenantModule : LibrarianLibModule("lieutenant", "Lieutenant") {

}

internal val logger = LibrarianLibLieutenantModule.makeLogger(null)
