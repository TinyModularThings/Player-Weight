package playerWeight;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import playerWeight.effects.ApplyAdvancementIncrease;
import playerWeight.effects.ApplyAttributeEffect;
import playerWeight.effects.ApplyDamageEffect;
import playerWeight.effects.ApplyExhaustion;
import playerWeight.effects.ApplyPotionEffects;
import playerWeight.handler.ClientHandler;
import playerWeight.handler.EffectLoader;
import playerWeight.handler.PlayerHandler;
import playerWeight.handler.WeightLoader;

@Mod(name = "Player Weight", modid = "playerweight", version = "1.0", acceptedMinecraftVersions = "[1.12]")
public class PlayerWeight
{
	public static PlayerWeight INSTANCE;
	public static double MAX_WEIGHT = 100D;
	
	WeightLoader loader = new WeightLoader();
	EffectLoader effects = new EffectLoader();
	Configuration config;
	File configFolder;
	
	List<File> weightFiles = new LinkedList<File>();
	List<File> effectFiles = new LinkedList<File>();
	
	Map<File, List<String>> errorMap = new LinkedHashMap<File, List<String>>();
	
	
	@EventHandler
	public void onPreLoad(FMLPreInitializationEvent evt)
	{
		INSTANCE = this;
		MinecraftForge.EVENT_BUS.register(PlayerHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
		configFolder = new File(evt.getModConfigurationDirectory(), "playerWeight");
		config = new Configuration(new File(configFolder, "config.cfg"));
		reloadConfigs(false);
		ApplyAdvancementIncrease.register();
		ApplyPotionEffects.register();
		ApplyExhaustion.register();
		ApplyDamageEffect.register();
		ApplyAttributeEffect.register();
	}
	
	@EventHandler
	public void onPostLoad(FMLPostInitializationEvent evt)
	{
		reload();
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new PlayerWeightCommand());
	}
	
	public void reloadConfigs(boolean reload)
	{
		weightFiles.clear();
		effectFiles.clear();
		try
		{
			if(reload)
			{
				config.load();
			}
			MAX_WEIGHT = config.get("general", "max_weight", 100D, "").getDouble();
			String[] array = config.getStringList("WeightFiles", "general", new String[0], "Select the Files where the Weight Data should be loaded from");
			for(int i = 0;i<array.length;i++)
			{
				File entry = new File(configFolder, array[i]);
				if(entry.exists())
				{
					weightFiles.add(entry);
				}
			}
			array = config.getStringList("EffectFiles", "general", new String[0], "Select the Files where the Effect Data should be loaded from");
			for(int i = 0;i<array.length;i++)
			{
				File entry = new File(configFolder, array[i]);
				if(entry.exists())
				{
					effectFiles.add(entry);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			config.save();
		}
		if(reload)
		{
			reload();
		}
	}
	
	public void reload()
	{
		errorMap.clear();
		for(File file : weightFiles)
		{
			loader.loadItems(file);
			if(loader.hasErrors())
			{
				errorMap.put(file, loader.getErrors());
			}
		}
		for(File file : effectFiles)
		{
			effects.loadEffects(file);
			if(effects.hasErrors())
			{
				errorMap.put(file, effects.getErrors());
			}
		}
		if(errorMap.size() > 0)
		{
			for(Entry<File, List<String>> error : errorMap.entrySet())
			{
				FMLLog.getLogger().info("Errors for File: " + error.getKey());
				for(String s : error.getValue())
				{
					FMLLog.getLogger().info(s);
				}
			}
		}
	}
	
	public void printToChat(ICommandSender sender)
	{
		if(errorMap.size() > 0)
		{
			for(Entry<File, List<String>> error : errorMap.entrySet())
			{
				sender.sendMessage(new TextComponentString("Errors for File: " + error.getKey()));
				for(String s : error.getValue())
				{
					sender.sendMessage(new TextComponentString(s));
				}
				sender.sendMessage(new TextComponentString(""));
			}
		}
		else
		{
			sender.sendMessage(new TextComponentString("No Errors Found!"));
		}
	}
}
