package playerWeight;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class PlayerWeightCommand extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "playerweight";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/playerweight allows to reload stuff";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("reload"))
			{
				PlayerWeight.INSTANCE.reloadConfigs(true);
				PlayerWeight.INSTANCE.printToChat(sender);
			}
			else if(args[1].equalsIgnoreCase("printErrors"))
			{
				PlayerWeight.INSTANCE.printToChat(sender);
			}
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
	{
		if(args.length <= 1) return getListOfStringsMatchingLastWord(args, "reload", "printErrors");
		return new ArrayList<String>();
	}
}
