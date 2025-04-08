package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.junit.UnitTestCommand

public object LibrarianLibTestCoreCommon {
    public fun onInitialize() {
        UnitTestCommand.register()
    }
}