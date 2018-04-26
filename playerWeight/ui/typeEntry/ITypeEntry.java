package playerWeight.ui.typeEntry;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ITypeEntry
{
	public String getData(int index);
	
	public void setWeight(double newWeight);
	
	public void setSize(int size);
	
	public void reset(boolean size);
	
	public String makeChange(boolean size);
	
	public ItemStack makeStack();
	
	public boolean isChanged(boolean size);
	
	public List<ITypeEntry> getSubEntries();
}
