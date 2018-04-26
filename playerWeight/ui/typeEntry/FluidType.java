package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import playerWeight.handler.ClientHandler;

public class FluidType implements ITypeEntry
{
	Fluid fluid;
	String name;
	String mod;
	
	int index;
	double weight;
	
	public FluidType(Fluid fluid, double weight, int theIndex)
	{
		this.fluid = fluid;
		this.weight = weight;
		
		index = theIndex;
		name = new FluidStack(fluid, 1000).getLocalizedName();
		mod = new ResourceLocation(FluidRegistry.getDefaultFluidName(fluid)).getResourceDomain();
	}
	
	
	@Override
	public String getData(int index)
	{
		if(index == 1) return ClientHandler.createToolTip(weight);
		else if(index == 2) return mod;
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
			weight = ((double)fluid.getDensity() / 1000D);
		}
	}
	
	@Override
	public String makeChange(boolean size)
	{
		if(!size)
		{
			return "<type=fluid name="+FluidRegistry.getDefaultFluidName(fluid)+" weight="+weight+">";
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
		return weight != ((double)fluid.getDensity() / 1000D);
	}
	
	@Override
	public List<ITypeEntry> getSubEntries()
	{
		return new ArrayList<ITypeEntry>();
	}

	@Override
	public int sort(ITypeEntry other, SorterType type)
	{
		if(other instanceof FluidType)
		{
			FluidType otherFluid = (FluidType)other;
			if(type == SorterType.Weight)
			{
				if(weight > otherFluid.weight)
				{
					return -1;
				}
				else if(weight < otherFluid.weight)
				{
					return 1;
				}
			}
			else if(type == SorterType.Name)
			{
				return name.compareTo(otherFluid.name);
			}
			else if(type == SorterType.ID)
			{
				if(index < otherFluid.index)
				{
					return -1;
				}
				else if(index > otherFluid.index)
				{
					return 1;
				}
			}
			else if(type == SorterType.Mod)
			{
				int result = mod.compareTo(otherFluid.mod);
				if(result == 0)
				{
					return sort(other, SorterType.ID);
				}
				return result;
			}
		}
		return 0;
	}
}
