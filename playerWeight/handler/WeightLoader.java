package playerWeight.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import playerWeight.api.ItemEntry;
import playerWeight.api.WeightRegistry;
import playerWeight.misc.IteratorWrapper;
import playerWeight.misc.XMLNode;
import playerWeight.ui.ChangeRegistry;

public class WeightLoader
{
	int currentIndex = 0;
	List<String> errors = new ArrayList<String>();
	
	public void loadItems(File file)
	{
		currentIndex = 0;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			for(String s : new IteratorWrapper<String>(reader.lines().iterator()))
			{
				s = s.trim();
				if(s.startsWith("//"))
				{
					currentIndex++;
					continue;
				}
				if(s.isEmpty())
				{
					currentIndex++;
					continue;
				}
				if(!s.startsWith("<") || !s.endsWith(">"))
				{
					errors.add("Line ["+currentIndex+"] has missing <> to identify the Data");
					currentIndex++;
					continue;
				}
				if(s.indexOf("<", 1) >= 0)
				{
					errors.add("Line ["+currentIndex+"] has Multiple entries in one line. Thats not allowed");
					currentIndex++;
					continue;
				}
				try
				{
					processNode(new XMLNode(s));
				}
				catch(Exception e)
				{
					errors.add("Error at line: "+currentIndex+" "+e.getMessage());
				}
				currentIndex++;
			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void processNode(XMLNode node)
	{
		String type = node.get("type");
		if(type.equalsIgnoreCase("item"))
		{
			Item item = Item.getByNameOrId(node.get("name"));
			if(item == null)
			{
				errors.add("Line ["+currentIndex+"] has a Null Item");
				return;
			}
			double weight = node.getAsDouble("weight");
			if(node.hasEntry("meta"))
			{
				int meta = node.getAsInt("meta");
				if(meta < 0)
				{
					errors.add("Line ["+currentIndex+"] a Metadata of negative value. Not allowed");
					return;
				}
				WeightRegistry.INSTANCE.registerStack(new ItemEntry(item, meta), weight);
			}
			else if(node.hasEntry("metas"))
			{
				for(int id : node.getAsIntArray("metas"))
				{
					if(id < 0)
					{
						errors.add("Line ["+currentIndex+"] a Metadata of negative value. Not allowed");
						continue;
					}
					WeightRegistry.INSTANCE.registerStack(new ItemEntry(item, id), weight);
				}
			}
			else
			{
				WeightRegistry.INSTANCE.registerItem(item, weight);
			}
		}
		else if(type.equalsIgnoreCase("ore"))
		{
			String id = node.get("name");
			double weight = node.getAsDouble("weight");
			WeightRegistry.INSTANCE.registerOre(id, weight);
			ChangeRegistry.INSTANCE.addOreDictSize(id, weight);
		}
		else if(type.equalsIgnoreCase("fluid"))
		{
			Fluid fluid = FluidRegistry.getFluid(node.get("name"));
			if(fluid == null)
			{
				errors.add("Line ["+currentIndex+"] has a Null Fluid");
				return;
			}
			WeightRegistry.INSTANCE.registerFluid(fluid, node.getAsDouble("weight"));
		}
		else if(type.equalsIgnoreCase("defaultWeight"))
		{
			WeightRegistry.INSTANCE.setDefaultWeight(node.getAsDouble("weight"));
		}
		else if(type.equalsIgnoreCase("defaultPlayerWeight"))
		{
			WeightRegistry.INSTANCE.setPlayerDefaultWeight(node.getAsDouble("weight"));
		}
		else if(type.equalsIgnoreCase("size"))
		{
			Item item = Item.getByNameOrId(node.get("name"));
			if(item == null)
			{
				errors.add("Line ["+currentIndex+"] has a Null Item");
				return;
			}
			int size = node.getAsInt("maxsize");
			if(size < 1 || size > 64)
			{
				errors.add("Line ["+currentIndex+"] custom Max ItemStacksize goes out of bounds valid bounds [1-64]");
				return;
			}
			item.setMaxStackSize(size);
		}
	}
	
	
	public List<String> getErrors()
	{
		List<String> newList = new ArrayList<String>(errors);
		errors.clear();
		return newList;
	}
	
	public boolean hasErrors()
	{
		return errors.size() > 0;
	}
}
