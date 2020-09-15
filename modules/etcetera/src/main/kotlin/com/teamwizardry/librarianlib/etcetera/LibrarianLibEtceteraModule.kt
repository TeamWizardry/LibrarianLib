package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.LibrarianLibModule

internal object LibrarianLibEtceteraModule : LibrarianLibModule("etcetera", "Etcetera")

internal val logger = LibrarianLibEtceteraModule.makeLogger(null)
