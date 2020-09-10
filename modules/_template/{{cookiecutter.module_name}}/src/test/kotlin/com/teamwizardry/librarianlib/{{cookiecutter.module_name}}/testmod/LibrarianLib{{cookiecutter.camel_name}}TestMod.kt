package com.teamwizardry.librarianlib.{{cookiecutter.module_name}}.testmod

import com.teamwizardry.librarianlib.{{cookiecutter.module_name}}.LibrarianLib{{cookiecutter.camel_name}}Module
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-{{cookiecutter.module_name}}-test")
object LibrarianLib{{cookiecutter.camel_name}}TestMod: TestMod(LibrarianLib{{cookiecutter.camel_name}}Module) {
}

internal val logger = LibrarianLib{{cookiecutter.camel_name}}TestMod.makeLogger(null)
