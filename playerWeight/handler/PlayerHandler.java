package playerWeight.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
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
	public void onPlayerMount(EntityMountEvent event)
	{
		if(event.getEntityMounting() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityMounting();
			dirtyPlayers.add(player.getUniqueID());
			for(IWeightEffect effect : playerEffects.getOrDefault(player.getUniqueID(), EMPTY_EFFECTS))
			{
				effect.clearEffects(player);
			}
			InventoryTracker tracker = new InventoryTracker(player.getUniqueID());
			Entity toMount = event.getEntityBeingMounted();
			do
			{
				updateTracker(toMount, tracker, event.isMounting());
				toMount = toMount.getRidingEntity();
			}
			while(toMount != null);
		}
	}
	
	private void updateTracker(Entity entity, InventoryTracker tracker, boolean mount)
	{
		if(entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
		{
			IItemHandler handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(handler instanceof InvWrapper)
			{
				IInventory inv = ((InvWrapper)handler).getInv();
				if(inv instanceof InventoryBasic)
				{
					if(mount)
					{
						((InventoryBasic)inv).addInventoryChangeListener(tracker);
					}
					else
					{
						((InventoryBasic)inv).removeInventoryChangeListener(tracker);
					}
				}
			}
		}
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
	public void onRespawn(PlayerRespawnEvent event)
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
	
	public void onServerStop()
	{
		playerEffects.clear();
		for(IWeightEffect effect : effects)
		{
			effect.onServerStop();
		}
		for(IWeightEffect effect : passiveEffects)
		{
			effect.onServerStop();
		}
	}
	
	public void clearEffects()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server == null)
		{
			return;
		}
		for(EntityPlayer player : server.getPlayerList().getPlayers())
		{
			for(IWeightEffect effect : effects)
			{
				effect.onPlayerUnloaded(player);
			}
			for(IWeightEffect effect : passiveEffects)
			{
				effect.onPlayerUnloaded(player);
			}
		}
	}
	
	public void onReload()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server == null)
		{
			return;
		}
		for(EntityPlayer player : server.getPlayerList().getPlayers())
		{
			dirtyPlayers.add(player.getUniqueID());
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
		if(event.side.isClient())
		{
			dirtyPlayers.clear();
			return;
		}
		EntityPlayer player = event.player;
		if(dirtyPlayers.contains(player.getUniqueID()))
		{
			dirtyPlayers.remove(player.getUniqueID());
			updatePlayer(player);
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
		for(IWeightEffect effect : passiveEffects)
		{
			effect.applyToPlayer(player, weight, max_weight, inst);
		}
	}
	
	private void updatePlayer(EntityPlayer player)
	{
		double totalWeight = WeightRegistry.INSTANCE.getDefaultPlayerWeight() + WeightRegistry.INSTANCE.getPlayerWeight(player, new Function<ItemStack, Double>(){
			@Override
			public Double apply(ItemStack t)
			{
				return calculateStack(t, true);
			}
		});
		Entity toCheck = player.getRidingEntity();
		while(toCheck != null)
		{
			if(toCheck.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
			{
				totalWeight += calculateWeight(toCheck.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
			}
			toCheck = toCheck.getRidingEntity();
		}
		if(totalWeight < 0D)
		{
			totalWeight = 0D;
		}
		WeightRegistry.setPlayerWeight(player, totalWeight);
		for(IWeightEffect effect : playerEffects.getOrDefault(player.getUniqueID(), EMPTY_EFFECTS))
		{
			effect.clearEffects(player);
		}
		double max = WeightRegistry.getMaxPlayerWeight(player);
		double current = totalWeight / max;
		
		List<IWeightEffect> list = new ArrayList<IWeightEffect>();
		for(IWeightEffect effect : effects)
		{
			if(effect.isPercent())
			{
				if(current > effect.minWeight() && current <= effect.maxWeight())
				{
					list.add(effect);
				}
			}
			else
			{
				if(totalWeight > effect.minWeight() && totalWeight <= effect.maxWeight())
				{
					list.add(effect);
				}
			}
		}
		playerEffects.put(player.getUniqueID(), list);
	}
	
	private double calculateWeight(IItemHandler handler)
	{
		if(handler == null)
		{
			return 0D;
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
			totalWeight += calculateStack(stack, true);
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if(fluid != null)
			{
				totalWeight += (WeightRegistry.INSTANCE.getWeight(fluid) * (double)stack.getCount());
			}
		}
		return totalWeight;
	}
	
	static double calculateStack(ItemStack item, boolean stackSize)
	{
		IItemHandler handler = WeightRegistry.INSTANCE.getItemHandler(item);
		if(handler == null)
		{
			return WeightRegistry.INSTANCE.getWeight(item) * (stackSize ? (double)item.getCount() : 1D);
		}
		int size = handler.getSlots();
		double weight = WeightRegistry.INSTANCE.getWeight(item) * (stackSize ? (double)item.getCount() : 1D);
		for(int i = 0;i<size;i++)
		{
			ItemStack stack = handler.getStackInSlot(i);
			if(stack.isEmpty())
			{
				continue;
			}
			weight += calculateStack(stack, true);
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if(fluid != null)
			{
				weight += (WeightRegistry.INSTANCE.getWeight(fluid) * (stackSize ? (double)stack.getCount() : 1D));
			}
		}
		return weight;
	}
}
