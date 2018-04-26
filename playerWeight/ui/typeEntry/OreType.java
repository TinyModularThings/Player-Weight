package playerWeight.ui.typeEntry;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

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
		if(index == 1) return ""+weight;
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
}
