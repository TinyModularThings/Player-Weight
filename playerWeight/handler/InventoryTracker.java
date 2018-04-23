package playerWeight.handler;

import java.util.UUID;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryTracker implements IContainerListener
{
	UUID player;
	
	protected InventoryTracker(UUID playerID)
	{
		player = playerID;
	}
	
	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
	{
		PlayerHandler.INSTANCE.dirtyPlayers.add(player);
	}
	
	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
	{
		PlayerHandler.INSTANCE.dirtyPlayers.add(player);
	}
	
	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
	{		
	}
	
	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory)
	{		
	}
	
	@Override
	public int hashCode()
	{
		return player.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof InventoryTracker)
		{
			InventoryTracker other = (InventoryTracker)obj;
			return other.player.equals(player);
		}
		return false;
	}
}
