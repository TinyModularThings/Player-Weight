package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemType implements ITypeEntry
{
	Item item;
	String name;
	double weight;
	int maxSize;
	int defaultMaxSize;
	
	public ItemType(Item item, double weight, int max)
	{
		this.item = item;
		this.weight = weight;
		defaultMaxSize = max;
		ItemStack stack = new ItemStack(item);
		maxSize = stack.getMaxStackSize();
		name = stack.getDisplayName();
	}
	

	@Override
	public String getData(int index)
	{
		if(index == 1) return ""+weight;
		else if(index == 2) return ""+maxSize;
		else if(index == 3) return ""+defaultMaxSize;
		return name;
	}

	@Override
	public void setWeight(double newWeight)
	{
		weight = newWeight;
	}


	@Override
	public void setSize(int size)
	{
		maxSize = size;
	}


	@Override
	public void reset(boolean size)
	{
		if(size)
		{
			maxSize = defaultMaxSize;
		}
		else
		{
			weight = 0D;
		}
	}
	
	@Override
	public String makeChange(boolean size)
	{
		if(!size)
		{
			return "<type=item name="+item.getRegistryName().toString()+" weight="+weight+">";
		}
		return "<type=item name="+item.getRegistryName().toString()+" maxsize="+maxSize+">";
	}


	@Override
	public ItemStack makeStack()
	{
		return new ItemStack(item);
	}
	
	@Override
	public boolean isChanged(boolean size)
	{
		if(size) return maxSize != defaultMaxSize;
		return weight != 0D;
	}
	
	@Override
	public List<ITypeEntry> getSubEntries()
	{
		return new ArrayList<ITypeEntry>();
	}
}
