package com.teamwizardry.librarianlib.testbase.junit

import org.junit.platform.launcher.TestIdentifier

data class TestSuiteResult(
    val roots: List<TestReport>,
    val reports: Map<TestIdentifier, TestReport>
) {

}

