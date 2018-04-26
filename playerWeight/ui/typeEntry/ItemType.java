package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import playerWeight.handler.ClientHandler;

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
		if(index == 1) return item.getRegistryName().getResourceDomain();
		else if(index == 2) return ClientHandler.createToolTip(weight);
		else if(index == 3) return ""+maxSize;
		else if(index == 4) return ""+defaultMaxSize;
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
	
	@Override
	public int sort(ITypeEntry other, SorterType type)
	{
		if(other instanceof ItemType)
		{
			ItemType otherItem = (ItemType)other;
			if(type == SorterType.ID)
			{
				int first = Item.REGISTRY.getIDForObject(item);
				int second = Item.REGISTRY.getIDForObject(otherItem.item);
				if(first < second)
				{
					return -1;
				}
				else if(first > second)
				{
					return 1;
				}
			}
			else if(type == SorterType.Name)
			{
				return name.compareTo(otherItem.name);
			}
			else if(type == SorterType.Mod)
			{
				int result = item.getRegistryName().getResourceDomain().compareTo(otherItem.item.getRegistryName().getResourceDomain());
				if(result == 0)
				{
					return sort(other, SorterType.ID);
				}
				return result;
			}
			else if(type == SorterType.Weight)
			{
				if(weight > otherItem.weight)
				{
					return -1;
				}
				else if(weight < otherItem.weight)
				{
					return 1;
				}
			}
			else if(type == SorterType.ItemSize)
			{
				if(maxSize > otherItem.maxSize)
				{
					return -1;
				}
				else if(maxSize < otherItem.maxSize)
				{
					return 1;
				}
			}
		}
		return 0;
	}
}
