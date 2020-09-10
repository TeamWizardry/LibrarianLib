package com.teamwizardry.librarianlib.{{cookiecutter.module_name}}

import com.teamwizardry.librarianlib.LibrarianLibModule
import org.apache.logging.log4j.LogManager

public object LibrarianLib{{cookiecutter.camel_name}}Module : LibrarianLibModule("{{cookiecutter.module_name}}", "{{cookiecutter.human_name}}") {
}

internal val logger = LibrarianLib{{cookiecutter.camel_name}}Module.makeLogger(null)
