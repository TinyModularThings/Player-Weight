package playerWeight.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import playerWeight.PlayerWeight;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public final class PlayerHandler
{
	public static final List<IWeightEffect> EMPTY_EFFECTS = new ArrayList<IWeightEffect>();
	public static final PlayerHandler INSTANCE = new PlayerHandler();
	Set<UUID> dirtyPlayers = new LinkedHashSet<UUID>();
	Map<UUID, List<IWeightEffect>> playerEffects = new LinkedHashMap<UUID, List<IWeightEffect>>();
	List<IWeightEffect> effects = new LinkedList<IWeightEffect>();
	List<IWeightEffect> passiveEffects = new ArrayList<IWeightEffect>();
	
	
	@SubscribeEvent
	public void onContainerOpened(PlayerContainerEvent.Open event)
	{
		event.getContainer().addListener(new InventoryTracker(event.getEntityPlayer().getUniqueID()));
	}
	
	@SubscribeEvent
	public void onPlayerConstruct(EntityConstructing event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof EntityPlayerMP)
		{
			AbstractAttributeMap map = ((EntityPlayerMP)entity).getAttributeMap();
			map.registerAttribute(WeightRegistry.WEIGHT);
			map.registerAttribute(WeightRegistry.MAX_WEIGHT);
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoined(PlayerLoggedInEvent event)
	{
		event.player.inventoryContainer.addListener(new InventoryTracker(event.player.getUniqueID()));
		dirtyPlayers.add(event.player.getUniqueID());
		AbstractAttributeMap map = event.player.getAttributeMap();
		double weight = map.getAttributeInstance(WeightRegistry.WEIGHT).getBaseValue();
		IAttributeInstance inst = map.getAttributeInstance(WeightRegistry.MAX_WEIGHT);
		inst.setBaseValue(PlayerWeight.MAX_WEIGHT);
		double max_weight = inst.getAttributeValue();
		for(IWeightEffect effect : passiveEffects)
		{
			effect.applyToPlayer(event.player, weight, max_weight, inst);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedEvent(PlayerLoggedOutEvent event)
	{
		for(IWeightEffect effect : effects)
		{
			effect.onPlayerUnloaded(event.player);
		}
	}
	
	public void addEffect(IWeightEffect effect)
	{
		if(effect == null)
		{
			return;
		}
		if(effect.isPassive())
		{
			passiveEffects.add(effect);
		}
		else 
		{
			effects.add(effect);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		EntityPlayer player = event.player;
		if(dirtyPlayers.contains(player.getUniqueID()))
		{
			dirtyPlayers.remove(player.getUniqueID());
			updatePlayer(player);
		}
		if(event.side.isClient())
		{
			return;
		}
		AbstractAttributeMap map = player.getAttributeMap();
		double weight = map.getAttributeInstance(WeightRegistry.WEIGHT).getBaseValue();
		IAttributeInstance inst = map.getAttributeInstance(WeightRegistry.MAX_WEIGHT);
		double max_weight = inst.getAttributeValue();
		for(IWeightEffect effect : playerEffects.getOrDefault(player.getUniqueID(), EMPTY_EFFECTS))
		{
			effect.applyToPlayer(player, weight, max_weight, inst);
		}
		if(player.world.getTotalWorldTime() % 200 != 0)
		{
			return;
		}
		FMLLog.getLogger().info("Effects: "+effects);
		for(IWeightEffect effect : passiveEffects)
		{
			effect.applyToPlayer(player, weight, max_weight, inst);
		}
	}
	
	private void updatePlayer(EntityPlayer player)
	{
		IItemHandler handler = player.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ? player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) : null;
		if(handler == null)
		{
			WeightRegistry.setPlayerWeight(player, 0D);
			return;
		}
		double totalWeight = 0;
		int size = handler.getSlots();
		for(int i = 0;i<size;i++)
		{
			ItemStack stack = handler.getStackInSlot(i);
			if(stack.isEmpty())
			{
				continue;
			}
			totalWeight += calculateStack(stack);
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if(fluid != null)
			{
				totalWeight += (WeightRegistry.INSTANCE.getWeight(fluid) * (double)stack.getCount());
			}
		}
		WeightRegistry.setPlayerWeight(player, totalWeight);
		List<IWeightEffect> list = new ArrayList<IWeightEffect>();
		for(IWeightEffect effect : effects)
		{
			if(totalWeight >= effect.minWeight() && totalWeight <= effect.maxWeight())
			{
				list.add(effect);
			}
		}
		for(IWeightEffect effect : playerEffects.getOrDefault(player.getUniqueID(), EMPTY_EFFECTS))
		{
			effect.clearEffects(player);
		}
		playerEffects.put(player.getUniqueID(), list);
	}
	
	private double calculateStack(ItemStack item)
	{
		if(item.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
		{
			IItemHandler handler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(handler == null)
			{
				return WeightRegistry.INSTANCE.getWeight(item) * (double)item.getCount();
			}
			int size = handler.getSlots();
			double weight = 0D;
			for(int i = 0;i<size;i++)
			{
				ItemStack stack = handler.getStackInSlot(i);
				if(stack.isEmpty())
				{
					continue;
				}
				weight += calculateStack(stack);
				FluidStack fluid = FluidUtil.getFluidContained(stack);
				if(fluid != null)
				{
					weight += (WeightRegistry.INSTANCE.getWeight(fluid) * (double)stack.getCount());
				}
			}
		}
		return WeightRegistry.INSTANCE.getWeight(item) * (double)item.getCount();
	}
}
