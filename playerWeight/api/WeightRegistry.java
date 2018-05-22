package playerWeight.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public enum WeightRegistry
{
	INSTANCE;
	
	public static final IAttribute WEIGHT = new RangedAttribute(null, "generic.Weight", 0, 0, Double.MAX_VALUE).setShouldWatch(true);
	public static final IAttribute MAX_WEIGHT = new RangedAttribute(null, "generic.maxWeight", 0, 0, Double.MAX_VALUE).setShouldWatch(true);
	
	Map<Item, Double> itemWeights = new LinkedHashMap<Item, Double>();
	Map<ItemEntry, Double> stackWeights = new LinkedHashMap<ItemEntry, Double>();
	Map<Fluid, Double> fluidWeight = new LinkedHashMap<Fluid, Double>();
	double defaultWeight = 0D;
	double playerDefaultWeight = 0D;
	Map<String, Function<JsonObject, IWeightEffect>> registry = new HashMap<String, Function<JsonObject, IWeightEffect>>();
	Map<Item, Function<ItemStack, IItemHandler>> invRegistry = new HashMap<Item, Function<ItemStack, IItemHandler>>();
	List<Function<EntityPlayer, IItemHandler>> extraInventories = new ArrayList<Function<EntityPlayer, IItemHandler>>();
	
	public void setPlayerDefaultWeight(double value)
	{
		playerDefaultWeight = value;
	}
	
	public void setDefaultWeight(double value)
	{
		defaultWeight = value;
	}
	
	public void registerFluid(Fluid fluid, double weight)
	{
		fluidWeight.put(fluid, weight);
	}
	
	public void registerStack(ItemStack stack, double weight)
	{
		if(stack.getMetadata() == Short.MAX_VALUE)
		{
			itemWeights.put(stack.getItem(), weight);
		}
		else
		{
			stackWeights.put(new ItemEntry(stack), weight);
		}
	}
	
	public void registerItem(Item item, double weight)
	{
		itemWeights.put(item, weight);
	}
	
	public void registerStack(ItemEntry entry, double weight)
	{
		if(entry.getMeta() == Short.MAX_VALUE)
		{
			itemWeights.put(entry.getItem(), weight);
		}
		else
		{
			stackWeights.put(entry, weight);
		}
	}
	
	public void registerOre(String id, double value)
	{
		for(ItemStack stack : OreDictionary.getOres(id))
		{
			registerStack(stack, value);
		}
	}
	
	public double getWeight(FluidStack fluid)
	{
		return fluidWeight.getOrDefault(fluid.getFluid(), ((double)fluid.getFluid().getDensity(fluid) / 1000D)) * ((double)fluid.amount / 1000D);
	}
	
	public double getWeight(ItemStack stack)
	{
		return stack.getMetadata() == Short.MAX_VALUE ? getWeightForItem(stack.getItem()) : stackWeights.getOrDefault(new ItemEntry(stack), getWeightForItem(stack.getItem()));
	}
	
	public double getWeightForFluid(Fluid fluid)
	{
		return fluidWeight.getOrDefault(fluid, (double)fluid.getDensity() / 1000D);
	}
	
	public double getWeightForItem(Item item)
	{
		return itemWeights.getOrDefault(item, defaultWeight);
	}
	
	public double getWeightForStack(ItemStack stack)
	{
		if(stack.getMetadata() == Short.MAX_VALUE)
		{
			return getWeightForItem(stack.getItem());
		}
		return stackWeights.getOrDefault(new ItemEntry(stack), defaultWeight);
	}
	
	public double getDefaultWeight()
	{
		return defaultWeight;
	}
	
	public double getDefaultPlayerWeight()
	{
		return playerDefaultWeight;
	}
	
	public void clear()
	{
		itemWeights.clear();
		stackWeights.clear();
		fluidWeight.clear();
		defaultWeight = 0D;
	}
	
	public static void setPlayerWeight(EntityPlayer player, double weight)
	{
		player.getAttributeMap().getAttributeInstance(WEIGHT).setBaseValue(weight);
	}
	
	public static double getPlayerWeight(EntityPlayer player)
	{
		return player.getAttributeMap().getAttributeInstance(WEIGHT).getBaseValue();
	}
	
	public static double getMaxPlayerWeight(EntityPlayer player)
	{
		return player.getAttributeMap().getAttributeInstance(MAX_WEIGHT).getAttributeValue();
	}
	
	public void registerWeightEffect(String id, Function<JsonObject, IWeightEffect> function)
	{
		registry.put(id.toLowerCase(Locale.ENGLISH), function);
	}
	
	public Function<JsonObject, IWeightEffect> getWeightEffect(String id)
	{
		if(id == null)
		{
			return null;
		}
		return registry.get(id.toLowerCase(Locale.ENGLISH));
	}
	
	public void registerItemHandler(Function<ItemStack, IItemHandler> itemHandler, Item item)
	{
		invRegistry.put(item, itemHandler);
	}
	
	public void registerItemHandler(Function<ItemStack, IItemHandler> itemHandler, Item...items)
	{
		for(Item item : items)
		{
			invRegistry.put(item, itemHandler);
		}
	}
	
	public void registerItemHandler(Function<ItemStack, IItemHandler> itemHandler, Block block)
	{
		Item item = Item.getItemFromBlock(block);
		if(item != null)
		{
			invRegistry.put(item, itemHandler);
		}
	}
	
	public void registerItemHandler(Function<ItemStack, IItemHandler> itemHandler, Block...blocks)
	{
		for(Block block : blocks)
		{
			Item item = Item.getItemFromBlock(block);
			if(item != null)
			{
				invRegistry.put(item, itemHandler);
			}
		}
	}
	
	public IItemHandler getItemHandler(ItemStack stack)
	{
		Function<ItemStack, IItemHandler> handler = invRegistry.get(stack.getItem());
		if(handler == null)
		{
			return getIItemHandler(null, stack);
		}
		return getIItemHandler(handler.apply(stack), stack);
	}
	
	private IItemHandler getIItemHandler(IItemHandler base, ItemStack stack)
	{
		if(base == null)
		{
			return stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ? stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) : null;
		}
		return base;
	}
	
	public void registerExtraPlayerInventory(Function<EntityPlayer, IItemHandler> handler)
	{
		if(handler == null)
		{
			return;
		}
		extraInventories.add(handler);
	}
	
	public double getPlayerWeight(EntityPlayer player, Function<ItemStack, Double> function)
	{
		double totalWeight = calculateWeight(player.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ? player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) : null, function);
		for(Function<EntityPlayer, IItemHandler> entry : extraInventories)
		{
			totalWeight += calculateWeight(entry.apply(player), function);
		}
		return totalWeight;
	}
	
	private double calculateWeight(IItemHandler handler, Function<ItemStack, Double> function)
	{
		if(handler == null)
		{
			return 0D;
		}
		double weight = 0D;
		int size = handler.getSlots();
		for(int i = 0;i<size;i++)
		{
			ItemStack stack = handler.getStackInSlot(i);
			if(stack.isEmpty())
			{
				continue;
			}
			weight += function.apply(stack);
		}
		return weight;
	}
}
