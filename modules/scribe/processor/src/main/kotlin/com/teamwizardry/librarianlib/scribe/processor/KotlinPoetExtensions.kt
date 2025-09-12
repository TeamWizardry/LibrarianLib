package com.teamwizardry.librarianlib.scribe.processor

import com.squareup.kotlinpoet.CodeBlock

fun CodeBlock.Builder.beginStatement() = add("«")
fun CodeBlock.Builder.endStatement() = add("\n»")
fun CodeBlock.Builder.nextStatement() = add("\n»«")
