package playerWeight.ui.typeEntry;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import playerWeight.handler.ClientHandler;

public class OreType implements ITypeEntry
{
	String type;
	double weight;
	List<ITypeEntry> representive = new LinkedList<ITypeEntry>();
	
	public OreType(String type, double weight)
	{
		this.type = type;
		this.weight = weight;
	}
	
	public void addEntry(ITypeEntry entry)
	{
		representive.add(entry);
	}
	
	@Override
	public String getData(int index)
	{
		if(index == 1) return ClientHandler.createToolTip(weight);
		return type;
	}

	@Override
	public void setWeight(double newWeight)
	{
		weight = newWeight;
		for(ITypeEntry entries : representive)
		{
			entries.setWeight(newWeight);
		}
	}
	
	@Override
	public void setSize(int size)
	{
		
	}

	@Override
	public void reset(boolean size)
	{
		if(!size)
		{
			setWeight(0D);
		}
	}

	@Override
	public String makeChange(boolean size)
	{
		if(!size)
		{
			return "<type=ore name="+type+" weight="+weight+">";
		}
		return "";
	}

	@Override
	public ItemStack makeStack()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isChanged(boolean size)
	{
		if(size) return false;
		return weight != 0D;
	}
	
	@Override
	public List<ITypeEntry> getSubEntries()
	{
		return representive;
	}
	
	@Override
	public int sort(ITypeEntry other, SorterType sorter)
	{
		if(other instanceof OreType)
		{
			OreType ore = (OreType)other;
			if(sorter == SorterType.ID)
			{
				int first = OreDictionary.getOreID(type);
				int second = OreDictionary.getOreID(ore.type);
				if(first < second)
				{
					return -1;
				}
				else if(first > second)
				{
					return 1;
				}
			}
			else if(sorter == SorterType.Name)
			{
				return type.compareTo(ore.type);
			}
			else if(sorter == SorterType.Weight)
			{
				if(weight > ore.weight)
				{
					return -1;
				}
				else if(weight < ore.weight)
				{
					return 1;
				}
			}
		}
		return 0;
	}
}
