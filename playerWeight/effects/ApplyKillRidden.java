package playerWeight.effects;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public class ApplyKillRidden extends BaseEffect
{
	Class<? extends Entity> clz;
	double limit;
	
	public ApplyKillRidden(JsonObject obj)
	{
		super(0, Double.MAX_VALUE, true);
		limit = obj.get("amount").getAsDouble();
		clz = EntityList.getClass(new ResourceLocation(obj.get("name").getAsString()));
		if(clz == null)
		{
			throw new RuntimeException("Entity in Obj ["+obj+"] is null. Not valid");
		}
	}

	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		if(weight < limit)
		{
			return;
		}
		Entity entity = getLowest(player);
		if(entity instanceof EntityPlayer || !clz.isInstance(entity))
		{
			return;
		}
		entity.onKillCommand();
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("kill_ridden", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyKillRidden(t);
			}			
		});
	}
}
