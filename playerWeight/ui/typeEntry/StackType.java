package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import playerWeight.handler.ClientHandler;

public class StackType implements ITypeEntry
{
	ItemStack stack;
	double weight;
	String name;
	String mod;
	
	public StackType(ItemStack stack, double weight)
	{
		this.stack = stack;
		this.weight = weight;
		name = stack.getDisplayName();
		mod = stack.getItem().getRegistryName().getResourceDomain();
	}
	
	@Override
	public String getData(int index)
	{
		if(index == 1) return mod;
		else if(index == 2) return ClientHandler.createToolTip(weight);
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
		
	}

	@Override
	public void reset(boolean size)
	{
		if(!size)
		{
			weight = 0;
		}
	}

	@Override
	public String makeChange(boolean size)
	{
		if(!size)
		{
			return "<type=item name="+stack.getItem().getRegistryName().toString()+" meta="+stack.getMetadata()+" weight="+weight+">";
		}
		return "";
	}

	@Override
	public ItemStack makeStack()
	{
		return stack.copy();
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
		return new ArrayList<ITypeEntry>();
	}

	@Override
	public int sort(ITypeEntry other, SorterType type)
	{
		if(other instanceof StackType)
		{
			StackType otherStack = (StackType)other;
			if(type == SorterType.IDMeta || type == SorterType.ID)
			{
				int first = Item.getIdFromItem(stack.getItem());
				int second = Item.getIdFromItem(otherStack.stack.getItem());
				if(first < second)
				{
					return -1;
				}
				else if(first > second)
				{
					return 1;
				}
				else
				{
					first = stack.getMetadata();
					second = otherStack.stack.getMetadata();
					if(first < second)
					{
						return -1;
					}
					else if(first > second)
					{
						return 1;
					}
				}
			}
			else if(type == SorterType.Name)
			{
				return name.compareTo(otherStack.name);
			}
			else if(type == SorterType.Mod)
			{
				int result = stack.getItem().getRegistryName().getResourceDomain().compareTo(otherStack.stack.getItem().getRegistryName().getResourceDomain());
				if(result == 0)
				{
					return sort(other, SorterType.IDMeta);
				}
				return result;
			}
			else if(type == SorterType.Weight)
			{
				if(weight > otherStack.weight)
				{
					return -1;
				}
				else if(weight < otherStack.weight)
				{
					return 1;
				}
			}
		}
		return 0;
	}
}
