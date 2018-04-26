package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class StackType implements ITypeEntry
{
	ItemStack stack;
	double weight;
	
	public StackType(ItemStack stack, double weight)
	{
		this.stack = stack;
		this.weight = weight;
	}
	
	@Override
	public String getData(int index)
	{
		if(index == 1) return ""+weight;
		return stack.getDisplayName();
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
}
