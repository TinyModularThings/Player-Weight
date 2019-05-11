package playerWeight;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;
import playerWeight.effects.ApplyAdvancementIncrease;
import playerWeight.effects.ApplyAttributeEffect;
import playerWeight.effects.ApplyDamageEffect;
import playerWeight.effects.ApplyExhaustion;
import playerWeight.effects.ApplyKillRidden;
import playerWeight.effects.ApplyPotionEffects;
import playerWeight.effects.ApplyPotionIncrease;
import playerWeight.handler.ClientHandler;
import playerWeight.handler.EffectLoader;
import playerWeight.handler.PlayerHandler;
import playerWeight.handler.WeightLoader;
import playerWeight.misc.ShulkerBoxHandler;
import playerWeight.ui.ChangeRegistry;
import playerWeight.ui.HelperUI;

@Mod(name = "Player Weight", modid = "playerweight", version = "1.4", acceptedMinecraftVersions = "[1.12]")
public class PlayerWeight
{
	public static PlayerWeight INSTANCE;
	public static double MAX_WEIGHT = 100D;
	
	WeightLoader loader = new WeightLoader();
	EffectLoader effects = new EffectLoader();
	Configuration config;
	public File configFolder;
	boolean loadUI;
	boolean ender;
	public int xOffset = 0;
	public int yOffset = 0;
	public String[] weightNames = new String[]{"T", "Kg", "g", "mg"};
	
	List<File> weightFiles = new LinkedList<File>();
	List<File> effectFiles = new LinkedList<File>();
	
	Map<File, List<String>> errorMap = new LinkedHashMap<File, List<String>>();
	List<IWeightPlugin> plugins = new ArrayList<IWeightPlugin>();
	
	@EventHandler
	public void onPreLoad(FMLPreInitializationEvent evt)
	{
		INSTANCE = this;
		MinecraftForge.EVENT_BUS.register(PlayerHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
		configFolder = new File(evt.getModConfigurationDirectory(), "playerWeight");
		config = new Configuration(new File(configFolder, "config.cfg"));
		loadUI = config.get("general", "loadHelperUI", false).getBoolean();
		ender = config.get("general", "Include EnderChest", false, "Includes the EnderChest into the WeightCalculation").getBoolean();
		xOffset = config.get("general", "xOffset", 0, "Offsets the Weight Hud horizontally").getInt();
		yOffset = config.get("general", "yOffset", 0, "Offsets the Weight Hud vertically").getInt();
		String[] names = config.get("general", "weightCategories", new String[]{"T", "Kg", "g", "mg"}, "Defines the Weight Definetions, has to be exactly 4 entries").getStringList();
		if(names != null && names.length == 4)
		{
			weightNames = names;
		}
		reloadConfigs(false);
		ApplyAdvancementIncrease.register();
		ApplyPotionEffects.register();
		ApplyExhaustion.register();
		ApplyDamageEffect.register();
		ApplyAttributeEffect.register();
		ApplyPotionIncrease.register();
		ApplyKillRidden.register();
		WeightRegistry.INSTANCE.registerItemHandler(new Function<ItemStack, IItemHandler>(){
			@Override
			public IItemHandler apply(ItemStack t)
			{
				return new ShulkerBoxHandler(t);
			}
		}, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
		if(ender)
		{
			WeightRegistry.INSTANCE.registerExtraPlayerInventory(new Function<EntityPlayer, IItemHandler>(){
				@Override
				public IItemHandler apply(EntityPlayer t)
				{
					return new InvWrapper(t.getInventoryEnderChest());
				}
			});
		}
		for(ASMData data : evt.getAsmData().getAll(WeightPluginListener.class.getCanonicalName()))
		{
			try
			{
				Class clz = Class.forName(data.getClassName());
				if(clz != null)
				{
					WeightPluginListener plug = (WeightPluginListener)clz.getAnnotation(WeightPluginListener.class);
					FMLLog.log.info("Test: "+plug);
					FMLLog.log.info("Loading: ["+plug.name()+", Version="+plug.version()+"]");
					IWeightPlugin modul = (IWeightPlugin)clz.newInstance();
					if(modul != null && modul.canLoad())
					{
						plugins.add(modul);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPostLoad(FMLPostInitializationEvent evt)
	{
		for(IWeightPlugin plugin : plugins)
		{
			try
			{
				plugin.onLoad();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(loadUI && FMLCommonHandler.instance().getSide().isClient())
		{
			ChangeRegistry.INSTANCE.init();
			reload();
			ChangeRegistry.INSTANCE.postInit();
			new HelperUI().setVisible(true);
		}
		else
		{
			reload();
		}
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new PlayerWeightCommand());
	}
	
	@EventHandler
	public void onServerStop(FMLServerStoppingEvent event)
	{
		PlayerHandler.INSTANCE.onServerStop();
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
		WeightRegistry.INSTANCE.clear();
		PlayerHandler.INSTANCE.clearEffects();
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
		PlayerHandler.INSTANCE.onReload();
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
