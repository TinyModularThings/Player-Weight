package playerWeight.ui.typeEntry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidType implements ITypeEntry
{
	Fluid fluid;
	String name;

	double weight;
	
	public FluidType(Fluid fluid, double weight)
	{
		this.fluid = fluid;
		this.weight = weight;
		
		name = new FluidStack(fluid, 1000).getLocalizedName();
	}
	
	
	@Override
	public String getData(int index)
	{
		if(index == 1) return ""+weight;
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
}
