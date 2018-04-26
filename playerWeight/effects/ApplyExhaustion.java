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
import playerWeight.misc.JsonHelper;

public class ApplyExhaustion extends BaseEffect
{
	Map<UUID, MutableInt> countdowns = new HashMap<UUID, MutableInt>();
	float amount;
	final int cooldown;
	
	public ApplyExhaustion(JsonObject obj)
	{
		super(obj.get("min").getAsDouble(), obj.get("max").getAsDouble(), false);
		setIsPercent(JsonHelper.getOrDefault(obj, "percent", false));
		amount = obj.get("amount").getAsFloat();
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
	public void onServerStop()
	{
		 countdowns.clear();
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
