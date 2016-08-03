package com.teamwizardry.librarianlib;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
public class ExampleBookCommand extends CommandBase {
	
	
	@Override
	public String getCommandName()
	{
		return "liblibBook";
	}
	
	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return "/liblibBook";
	}
	
	// Older versions (<1.9) had instead of execute method processCommand method (without MinecraftServer parameter)
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws WrongUsageException {
		
		server.addScheduledTask(() -> LibrarianLib.guide.display());
		
	}
	
	public void wrongUsage(ICommandSender sender) throws WrongUsageException {
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
