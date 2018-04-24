package playerWeight.effects;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;
import playerWeight.misc.JsonHelper;

public class ApplyPotionIncrease extends BaseEffect
{
	Potion effect;
	AttributeModifier mod;
	
	public ApplyPotionIncrease(JsonObject obj)
	{
		super(0D, Double.MAX_VALUE, JsonHelper.getOrDefault(obj, "passive", true));
		effect = Potion.getPotionFromResourceLocation(obj.get("name").getAsString());
		mod = new AttributeModifier(obj.get("id").getAsString(), obj.get("amount").getAsDouble(), obj.get("effectType").getAsInt());
	}

	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		if(player.isPotionActive(effect))
		{
			if(!maxWeightInstance.hasModifier(mod))
			{
				maxWeightInstance.applyModifier(mod);
			}
		}
		else
		{
			if(maxWeightInstance.hasModifier(mod))
			{
				maxWeightInstance.removeModifier(mod);
			}
		}
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("potion_modify", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyPotionIncrease(t);
			}
		});
	}
}
