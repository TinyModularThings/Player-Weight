package playerWeight.misc;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.items.IItemHandler;

public class ShulkerBoxHandler implements IItemHandler
{
	NonNullList<ItemStack> list;
	
	public ShulkerBoxHandler(ItemStack stack)
	{
		list = NonNullList.withSize(27, ItemStack.EMPTY);
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey("BlockEntityTag", 10))
		{
            NBTTagCompound data = nbt.getCompoundTag("BlockEntityTag");
            if(data.hasKey("Items", 9))
            {
                ItemStackHelper.loadAllItems(data, list);
            }
		}
	}
	
	@Override
	public int getSlots()
	{
		return list.size();
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return list.get(slot);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}
	
}
