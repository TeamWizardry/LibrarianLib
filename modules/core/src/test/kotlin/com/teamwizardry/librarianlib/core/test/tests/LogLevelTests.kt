package com.teamwizardry.librarianlib.core.test.tests

import com.teamwizardry.librarianlib.core.test.LLCoreTest
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import org.junit.jupiter.api.Test

class LogLevelTests {
    private val classLogger = LLCoreTest.logManager.makeLogger<LogLevelTests>()
    private val levels = listOf(
        Level.TRACE,
        Level.DEBUG,
        Level.INFO,
        Level.WARN,
        Level.ERROR,
        Level.FATAL,
    )

    private fun logger(name: String): Logger {
        return LogManager.getLogger("LibrarianLib Core Test|LogLevelTests_$name")
    }

    private fun printTests(logger: Logger, maxLevel: Level) {
        classLogger.info("Testing log levels. Only messages with severity $maxLevel or higher should print.")
        for(level in levels) {
            if(level <= maxLevel) {
                logger.log(level, "Message with level $level. This should appear.")
            } else {
                classLogger.info("A message with level $level should not appear:")
                logger.log(level, "Message with level $level. This should not appear.")
            }
        }
        classLogger.info("Done.")
    }

    @Test
    fun defaultLogger() {
        val logger = logger("defaultLogger")
        printTests(logger, Level.INFO)
    }

    @Test
    fun traceLogger() {
        val logger = logger("traceLogger")
        Configurator.setLevel(logger.name, Level.TRACE)
        printTests(logger, Level.TRACE)
    }

    @Test
    fun debugLogger() {
        val logger = logger("debugLogger")
        Configurator.setLevel(logger.name, Level.DEBUG)
        printTests(logger, Level.DEBUG)
    }

    @Test
    fun infoLogger() {
        val logger = logger("infoLogger")
        Configurator.setLevel(logger.name, Level.INFO)
        printTests(logger, Level.INFO)
    }

    @Test
    fun warnLogger() {
        val logger = logger("warnLogger")
        Configurator.setLevel(logger.name, Level.WARN)
        printTests(logger, Level.WARN)
    }

    @Test
    fun errorLogger() {
        val logger = logger("errorLogger")
        Configurator.setLevel(logger.name, Level.ERROR)
        printTests(logger, Level.ERROR)
    }

    @Test
    fun fatalLogger() {
        val logger = logger("fatalLogger")
        Configurator.setLevel(logger.name, Level.FATAL)
        printTests(logger, Level.FATAL)
    }
}