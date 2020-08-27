package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibrarianLibModule

object LibrarianLibEtceteraModule : LibrarianLibModule("etcetera", "Etcetera")

internal val logger = LibrarianLibEtceteraModule.makeLogger(null)
