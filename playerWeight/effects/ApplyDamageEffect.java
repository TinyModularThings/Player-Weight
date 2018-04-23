package playerWeight.effects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public class ApplyDamageEffect implements IWeightEffect
{
	Map<UUID, MutableInt> countdowns = new HashMap<UUID, MutableInt>();
	float amount;
	int cooldown;
	double percentLeft;
	
	public ApplyDamageEffect(JsonObject obj)
	{
		amount = obj.get("amount").getAsFloat();
		cooldown = obj.get("cooldown").getAsInt();
		percentLeft = obj.get("activation").getAsDouble() / 100;
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		double scale = weight / maxWeight;
		if(scale < percentLeft)
		{
			return;
		}
		MutableInt counter = getCounter(player);
		counter.decrement();
		if(counter.getValue() < 0)
		{
			counter.setValue(cooldown);
			player.attackEntityFrom(DamageSource.MAGIC, amount);
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
		return 0;
	}
	
	@Override
	public double maxWeight()
	{
		return Double.MAX_VALUE;
	}
	
	@Override
	public boolean isPassive()
	{
		return false;
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
		WeightRegistry.INSTANCE.registerWeightEffect("damage", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyDamageEffect(t);
			}
		});
	}
}
