package playerWeight.effects;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public class ApplyAttributeEffect implements IWeightEffect
{
	String name;
	double minValue;
	double maxValue;
	AttributeModifier modifier;
	
	public ApplyAttributeEffect(JsonObject obj)
	{
		name = obj.get("effect").getAsString();
		minValue = obj.get("min").getAsDouble();
		maxValue = obj.get("max").getAsDouble();
		modifier = new AttributeModifier(obj.get("id").getAsString(), obj.get("amount").getAsDouble(), obj.get("modifierType").getAsInt());
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		IAttributeInstance instance = player.getAttributeMap().getAttributeInstanceByName(name);
		if(!instance.hasModifier(modifier))
		{
			instance.applyModifier(modifier);
		}
	}
	
	@Override
	public void onPlayerUnloaded(EntityPlayer player)
	{
		
	}
	
	@Override
	public void clearEffects(EntityPlayer player)
	{
		player.getAttributeMap().getAttributeInstanceByName(name).removeModifier(modifier);
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
	
	@Override
	public boolean isPassive()
	{
		return false;
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("attribute", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyAttributeEffect(t);
			}
		});
	}
}
