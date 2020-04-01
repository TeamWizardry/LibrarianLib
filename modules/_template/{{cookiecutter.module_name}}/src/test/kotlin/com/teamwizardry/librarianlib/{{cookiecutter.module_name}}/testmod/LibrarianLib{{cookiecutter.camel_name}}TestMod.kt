package com.teamwizardry.librarianlib.{{cookiecutter.module_name}}.testmod

import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-{{cookiecutter.module_name}}-test")
object LibrarianLib{{cookiecutter.camel_name}}TestMod: TestMod("{{cookiecutter.module_name}}", "{{cookiecutter.human_name}}", logger) {
}

internal val logger = LogManager.getLogger("LibrarianLib: {{cookiecutter.human_name}} Test")
