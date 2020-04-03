package com.teamwizardry.librarianlib.{{cookiecutter.module_name}}

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

object Lib{{cookiecutter.camel_name}}Module : LibrarianLibModule("{{cookiecutter.module_name}}", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: {{cookiecutter.human_name}}")
