package playerWeight.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;
import playerWeight.misc.JsonHelper;

public class ApplyPotionEffects implements IWeightEffect
{
	Map<UUID, MutableInt> countdowns = new HashMap<UUID, MutableInt>();
	List<PotionEffect> effects = new ArrayList<PotionEffect>();
	final int coolDown;
	double minWeight;
	double maxWeight;
	
	public ApplyPotionEffects(JsonObject obj)
	{
		coolDown = JsonHelper.getOrDefault(obj, "cooldown", 0);
		minWeight = obj.get("min").getAsDouble();
		maxWeight = obj.get("max").getAsDouble();
		JsonHelper.convertToObject(obj.get("potions"), new Consumer<JsonObject>(){
			@Override
			public void accept(JsonObject t)
			{
				PotionEffect effect = JsonHelper.createPotionEffect(t);
				if(effect == null)
				{
					throw new RuntimeException("PotionEffect doesnt exist");
				}
				effects.add(effect);
			}
		});
	}
	
	@Override
	public double minWeight()
	{
		return minWeight;
	}


	@Override
	public double maxWeight()
	{
		return maxWeight;
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		MutableInt countdown = getCounter(player);
		countdown.decrement();
		if(countdown.getValue() < 0)
		{
			countdown.setValue(coolDown);
			for(PotionEffect effect : effects)
			{
				player.addPotionEffect(new PotionEffect(effect));
			}
		}
	}
	
	@Override
	public void clearEffects(EntityPlayer player)
	{
		countdowns.remove(player);
	}
	
	@Override
	public void onPlayerUnloaded(EntityPlayer player)
	{
		countdowns.remove(player.getUniqueID());
	}
	
	public MutableInt getCounter(EntityPlayer player)
	{
		MutableInt data = countdowns.get(player.getUniqueID());
		if(data == null)
		{
			data = new MutableInt(0);
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
		WeightRegistry.INSTANCE.registerWeightEffect("potion", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyPotionEffects(t);
			}
		});
	}
}
