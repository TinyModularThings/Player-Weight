package playerWeight.effects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public class ApplyExhaustion implements IWeightEffect
{
	Map<UUID, MutableInt> countdowns = new HashMap<UUID, MutableInt>();
	float amount;
	double minValue;
	double maxValue;
	final int cooldown;
	
	public ApplyExhaustion(JsonObject obj)
	{
		amount = obj.get("amount").getAsFloat();
		minValue = obj.get("min").getAsDouble();
		maxValue = obj.get("max").getAsDouble();
		cooldown = obj.get("cooldown").getAsInt();
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		MutableInt value = getCounter(player);
		value.decrement();
		if(value.getValue() < 0)
		{
			value.setValue(cooldown);
			player.getFoodStats().addExhaustion(amount);
		}
	}
	
	@Override
	public void onPlayerUnloaded(EntityPlayer player)
	{
		countdowns.remove(player.getUniqueID());		
	}
	
	@Override
	public void clearEffects(EntityPlayer player)
	{
		countdowns.remove(player.getUniqueID());
	}
	
	@Override
	public double minWeight()
	{
		return minValue;
	}
	
	@Override
	public double maxWeight()
	{
		return maxValue;
	}
	
	public MutableInt getCounter(EntityPlayer player)
	{
		MutableInt data = countdowns.get(player.getUniqueID());
		if(data == null)
		{
			data = new MutableInt(cooldown);
			countdowns.put(player.getUniqueID(), data);
		}
		return data;
	}
	
	@Override
	public boolean isPassive()
	{
		return false;
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("exhaustion", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyExhaustion(t);
			}
		});
	}
}
