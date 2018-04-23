package playerWeight.api;

import com.google.common.base.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemEntry
{
	final Item item;
	final int meta;
	
	public ItemEntry(ItemStack stack)
	{
		this(stack.getItem(), stack.getMetadata());
	}
	
	public ItemEntry(Item item, int meta)
	{
		this.item = item;
		this.meta = meta;
	}
	
	public final Item getItem()
	{
		return item;
	}
	
	public final  int getMeta()
	{
		return meta;
	}
	
	@Override
	public final int hashCode()
	{
		return Objects.hashCode(item, meta);
	}
	
	@Override
	public final boolean equals(Object obj)
	{
		if(obj instanceof ItemEntry)
		{
			ItemEntry entry = (ItemEntry)obj;
			return entry.item == item && entry.meta == meta;
		}
		return false;
	}
}
