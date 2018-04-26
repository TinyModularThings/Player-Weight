package playerWeight.effects;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;
import playerWeight.misc.JsonHelper;

public class ApplyAttributeEffect extends BaseEffect
{
	String name;
	AttributeModifier modifier;
	
	public ApplyAttributeEffect(JsonObject obj)
	{
		super(obj.get("min").getAsDouble(), obj.get("max").getAsDouble(), false, JsonHelper.getOrDefault(obj, "effectRidden", false));
		setIsPercent(JsonHelper.getOrDefault(obj, "percent", false));
		name = obj.get("effect").getAsString();
		modifier = new AttributeModifier(obj.get("id").getAsString(), obj.get("amount").getAsDouble(), obj.get("modifierType").getAsInt());
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		IAttributeInstance instance = getLowestEntity(player).getAttributeMap().getAttributeInstanceByName(name);
		if(!instance.hasModifier(modifier))
		{
			instance.applyModifier(modifier);
		}
	}
	
	@Override
	public void clearEffects(EntityPlayer player)
	{
		getLowestEntity(player).getAttributeMap().getAttributeInstanceByName(name).removeModifier(modifier);
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("attribute", new Function<JsonObject, IWeightEffect>()
		{
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyAttributeEffect(t);
			}
		});
	}
}
