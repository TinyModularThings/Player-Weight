package playerWeight.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import playerWeight.PlayerWeight;
import playerWeight.api.ItemEntry;
import playerWeight.api.WeightRegistry;
import playerWeight.ui.typeEntry.FluidType;
import playerWeight.ui.typeEntry.ITypeEntry;
import playerWeight.ui.typeEntry.ItemType;
import playerWeight.ui.typeEntry.OreType;
import playerWeight.ui.typeEntry.StackType;

public final class ChangeRegistry
{
	public static ChangeRegistry INSTANCE = new ChangeRegistry();
	Map<Item, Integer> baseStacksize = new LinkedHashMap<Item, Integer>();
	Map<String, Double> oreDictWeights = new LinkedHashMap<String, Double>();
	List<ITypeEntry>[] entries = new List[4];
	Set<String> changes = new LinkedHashSet<String>();
	
	
	public void init()
	{
		for(Item item : Item.REGISTRY)
		{
			baseStacksize.put(item, new ItemStack(item).getMaxStackSize());
		}
	}
	
	public void postInit()
	{
		for(int i = 0;i<entries.length;i++)
		{
			entries[i] = new ArrayList<ITypeEntry>();
		}
		double lastDefaultWeight = WeightRegistry.INSTANCE.getDefaultWeight();
		WeightRegistry.INSTANCE.setDefaultWeight(0D);
		Map<Item, ITypeEntry> miniCacheItem = new HashMap<Item, ITypeEntry>();
		Map<ItemEntry, ITypeEntry> miniCacheStack = new HashMap<ItemEntry, ITypeEntry>();
		
		for(Item item : Item.REGISTRY)
		{
			ITypeEntry entry = new ItemType(item, WeightRegistry.INSTANCE.getWeightForItem(item), baseStacksize.get(item));
			entries[0].add(entry);
			miniCacheItem.put(item, entry);
			NonNullList<ItemStack> items = NonNullList.create();
			item.getSubItems(CreativeTabs.SEARCH, items);
			for(ItemStack stack : items)
			{
				if(stack.getMetadata() != Short.MAX_VALUE)
				{
					entry = new StackType(stack, WeightRegistry.INSTANCE.getWeightForStack(stack));
					entries[1].add(entry);
					miniCacheStack.put(new ItemEntry(stack), entry);
				}
			}
		}
		for(String id : OreDictionary.getOreNames())
		{
			OreType entry = new OreType(id, oreDictWeights.getOrDefault(id, 0D));
			for(ItemStack stack : OreDictionary.getOres(id))
			{
				if(stack.getMetadata() == Short.MAX_VALUE)
				{
					ITypeEntry type = miniCacheItem.get(stack.getItem());
					if(type != null)
					{
						entry.addEntry(type);
					}
				}
				else
				{
					ITypeEntry type = miniCacheStack.get(new ItemEntry(stack));
					if(type != null)
					{
						entry.addEntry(type);
					}
				}
			}
			entries[2].add(entry);
		}
		for(Fluid fluid : FluidRegistry.getRegisteredFluids().values())
		{
			entries[3].add(new FluidType(fluid, WeightRegistry.INSTANCE.getWeightForFluid(fluid)));
		}
		WeightRegistry.INSTANCE.setDefaultWeight(lastDefaultWeight);
	}
	
	public void addOreDictSize(String id, double weight)
	{
		oreDictWeights.put(id, weight);
	}
	
	void addToList(int type, List<ITypeEntry> entry)
	{
		entry.addAll(entries[type]);
	}
	
	void addChange(String change)
	{
		changes.add(change);
	}
	
	void removeChange(String change)
	{
		changes.remove(change);
	}
	
	int loadChanges()
	{
		int totalChanges = 0;
		for(ITypeEntry entry : entries[0])
		{
			if(entry.isChanged(false))
			{
				String s = entry.makeChange(false);
				if(s.length() > 0)
				{
					if(changes.add(s))
					{
						totalChanges++;
					}
				}
			}
			if(entry.isChanged(true))
			{
				String s = entry.makeChange(true);
				if(s.length() > 0)
				{
					if(changes.add(s))
					{
						totalChanges++;
					}
				}
			}
		}
		for(ITypeEntry entry : entries[1])
		{
			if(entry.isChanged(false))
			{
				String s = entry.makeChange(false);
				if(s.length() > 0)
				{
					if(changes.add(s))
					{
						totalChanges++;
					}
				}
			}
		}
		for(ITypeEntry entry : entries[2])
		{
			if(entry.isChanged(false))
			{
				String s = entry.makeChange(false);
				if(s.length() > 0)
				{
					if(changes.add(s))
					{
						totalChanges++;
					}
				}
				for(ITypeEntry subEntry : entry.getSubEntries())
				{
					s = subEntry.makeChange(false);
					if(s.length() > 0)
					{
						changes.remove(s);
					}
				}
			}
		}
		for(ITypeEntry entry : entries[3])
		{
			if(entry.isChanged(false))
			{
				String s = entry.makeChange(false);
				if(s.length() > 0)
				{
					if(changes.add(s))
					{
						totalChanges++;
					}
				}
			}
		}
		return totalChanges;
	}
	
	int exportChanges(String name, boolean clear)
	{
		File file = new File(PlayerWeight.INSTANCE.configFolder, name);
		try
		{
			BufferedWriter buffered = new BufferedWriter(new FileWriter(file));
			for(String s : changes)
			{
				buffered.write(s);
				buffered.newLine();
			}
			buffered.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int size = changes.size();
		if(clear)
		{
			changes.clear();
		}
		return size;
	}
}
