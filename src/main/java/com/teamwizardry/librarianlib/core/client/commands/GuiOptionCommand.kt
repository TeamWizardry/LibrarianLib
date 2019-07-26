package com.teamwizardry.librarianlib.core.client.commands

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import kotlin.reflect.KMutableProperty0
import net.minecraft.command.CommandException
import net.minecraft.util.text.TextComponentString
import net.minecraft.command.ICommand
import java.util.ArrayList
import net.minecraft.util.math.BlockPos



class OptionCommand(private val _name: String): CommandBase() {
    private val options = mutableListOf<Option<*>>()

    fun add(option: Option<*>) {
        options.add(option)
    }

    override fun getName(): String = _name
    override fun getUsage(sender: ICommandSender): String = "librarianlib.command.liblib.option.usage"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if(args.isEmpty()) {
            sender.sendMessage(TextComponentString(options.map { it.name }.sorted().joinToString(", ")))
            return
        }

        val option = options.find { it.name == args[0] }
            ?: throw CommandException("librarianlib.command.liblib.option.nosuchoption", args[0])
        if(args.size == 1) {
            sender.sendMessage(TextComponentString(args[0]).appendText(" = ").appendText(option.valueString()))
            return
        }

        val value = args.sliceArray(1 until args.size).joinToString(" ")
        option.setValue(value)
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): MutableList<String> {
        if (args.size == 1) {
            val keys = options.map { it.name }.sorted()
            return getListOfStringsMatchingLastWord(args, keys)
        }

        return super.getTabCompletions(server, sender, args, targetPos)
    }
}

abstract class Option<T>(val name: String,
    val getter: () -> T, val setter: (T) -> Unit
) {
    constructor(name: String, property: KMutableProperty0<T>): this(name, property::get, property::set)

    abstract fun valueString(): String
    abstract fun setValue(arg: String)
}

class BooleanGuiOption: Option<Boolean> {
    constructor(name: String, getter: () -> Boolean, setter: (Boolean) -> Unit): super(name, getter, setter)
    constructor(name: String, property: KMutableProperty0<Boolean>): super(name, property)

    override fun valueString(): String = getter().toString()

    override fun setValue(arg: String) {
        setter(CommandBase.parseBoolean(arg))
    }

}
